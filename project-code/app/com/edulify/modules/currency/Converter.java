package com.edulify.modules.currency;

import static play.mvc.Controller.async;
import static play.mvc.Controller.ok;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import play.cache.Cache;
import play.libs.F.Function;
import play.libs.WS;
import play.mvc.Result;

public class Converter {

  public enum Source {
    GET_EXCHANGE_RATES
  }

  private static boolean useCache = true;
  private static int cacheTTL = 60;

  public static void useCache(boolean useCache) {
    Converter.useCache = useCache;
  }

  public static void setCacheTime(int seconds) {
    Converter.cacheTTL = seconds;
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

  private static WS.Response getExchangeRates() {
    String cacheKey = Source.GET_EXCHANGE_RATES.toString();
    WS.Response wsResponse = (WS.Response) Cache.get(cacheKey);
    if (!useCache || wsResponse == null) {
      String url = String.format("http://www.getexchangerates.com/api/latest.json");
      play.Logger.debug("requesting " + url);
      wsResponse = WS.url(url).get().get(10000l);
      Cache.set(cacheKey, wsResponse, cacheTTL);
    }
    return wsResponse;
  }

  private static BigDecimal withGetExchangeRates(final BigDecimal value, String from, String to) {
    WS.Response wsResponse = getExchangeRates();

    JsonNode jsonResponse  = wsResponse.asJson().get(0);
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
      fromRateBD = new BigDecimal(fromRate.getTextValue().trim());
      toRateBD   = new BigDecimal(toRate.getTextValue().trim());
    } catch (NumberFormatException ex) {
      throw new CommunicationErrorException();
    }

    BigDecimal exchangeRate = toRateBD.divide(fromRateBD, 3, RoundingMode.HALF_EVEN); // precision: 3 decimals

    return value.multiply(exchangeRate).setScale(3, RoundingMode.HALF_EVEN);
  }
}