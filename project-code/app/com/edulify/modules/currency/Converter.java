package com.edulify.modules.currency;

import static play.mvc.Controller.async;
import static play.mvc.Controller.ok;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import play.Play;
import play.cache.Cache;
import play.libs.F.Function;
import play.libs.WS;
import play.mvc.Result;

public class Converter {

  public enum Source {
    GET_EXCHANGE_RATES
  }

  protected static boolean useCache = Play.application().configuration().getBoolean("converter.useCache", true);
  protected static int cacheTTL     = Play.application().configuration().getInt("converter.cacheTTL", 60);
  protected static int precision    = Play.application().configuration().getInt("converter.precision", 100);

  public static void useCache(boolean useCache) {
    Converter.useCache = useCache;
  }

  public static void setCacheTime(int seconds) {
    Converter.cacheTTL = seconds;
  }

  public static void setPrecision(int precision) {
    Converter.precision = precision;
  }

  public static BigDecimal convert(final BigDecimal value, String from, String to) {
    return convert(value, from, to, Source.GET_EXCHANGE_RATES);
  }

  public static BigDecimal convert(final BigDecimal value, String from, String to,  Source source) {
    if (source.equals(Source.GET_EXCHANGE_RATES)) {
      return withGetExchangeRates(value, from, to);
    }

    return null;
  }

  public static BigDecimal convert(final BigDecimal value, Currency from, Currency to) {
    return convert(value, from.getCurrencyCode(), to.getCurrencyCode());
  }

  public static BigDecimal convert(final BigDecimal value, Currency from, Currency to, Source source) {
    return convert(value, from.getCurrencyCode(), to.getCurrencyCode(), source);
  }



  protected static JsonNode getExchangeRates() {
    String cacheKey = Source.GET_EXCHANGE_RATES.toString();
    JsonNode response = (JsonNode) Cache.get(cacheKey);
    if (!useCache || response == null) {
      String url = String.format("http://www.getexchangerates.com/api/latest.json");
      play.Logger.debug("requesting " + url);
      WS.Response wsResponse = WS.url(url).get().get(10000l);
      response = wsResponse.asJson().get(0);
      Cache.set(cacheKey, response, cacheTTL);
    }
    return response;
  }

  private static BigDecimal withGetExchangeRates(final BigDecimal value, String from, String to) {
    JsonNode jsonResponse  = getExchangeRates();
    if (jsonResponse == null) {
      Cache.remove(Source.GET_EXCHANGE_RATES.toString());
      throw new CommunicationErrorException();
    }
    JsonNode fromRate = jsonResponse.get(from.toUpperCase());
    JsonNode toRate   = jsonResponse.get(to.toUpperCase());

    if (fromRate == null || toRate == null) {
      throw new InvalidCurrencyException();
    }

    BigDecimal fromRateBD = null;
    BigDecimal toRateBD   = null;
    try {
      fromRateBD = new BigDecimal(fromRate.asText().trim());
      toRateBD   = new BigDecimal(toRate.asText().trim());
    } catch (NumberFormatException ex) {
      throw new CommunicationErrorException();
    }

    BigDecimal exchangeRate = toRateBD.divide(fromRateBD, Converter.precision, RoundingMode.HALF_EVEN);

    return value.multiply(exchangeRate).setScale(Converter.precision, RoundingMode.HALF_EVEN);
  }
}