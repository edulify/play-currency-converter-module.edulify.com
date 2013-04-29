package com.edulify.modules.currency;

import static play.mvc.Controller.async;
import static play.mvc.Controller.ok;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Function;
import play.libs.WS;
import play.mvc.Result;

public class Converter {

  public enum Source {
    GET_EXCHANGE_RATES
  }

  private static boolean useCache = true;
  private static long cacheTTL = 60000l;
  private static Map<Source, WS.Response> cache = new HashMap<Source, WS.Response>();
  private static Map<Source, Long> cacheLastUpdate = new HashMap<Source, Long>();

  public static void useCache(boolean useCache) {
    Converter.useCache = useCache;
  }

  public static void setCacheTime(long milliseconds) {
    Converter.cacheTTL = milliseconds;
  }

  public static Double convert(final Double value, String from, String to) {
    return convert(value, from, to, Source.GET_EXCHANGE_RATES);
  }

  public static Double convert(final Double value, String from, String to,  Source source) {
    if (source.equals(Source.GET_EXCHANGE_RATES)) {
      return withGetExchangeRates(value, from, to);
    }

    return null;
  }

  private static WS.Response getExchangeRates() {
    WS.Response wsResponse = cache.get(Source.GET_EXCHANGE_RATES);
    long timeNow = new Date().getTime();
    if (!useCache ||
        wsResponse == null ||
        timeNow - cacheTTL > cacheLastUpdate.get(Source.GET_EXCHANGE_RATES)) {
      String url = String.format("http://www.getexchangerates.com/api/latest.json");
      play.Logger.debug("requesting " + url);
      wsResponse = WS.url(url).get().get(10000l);
      cache.put(Source.GET_EXCHANGE_RATES, wsResponse);
      cacheLastUpdate.put(Source.GET_EXCHANGE_RATES, timeNow);
    }
    return wsResponse;
  }

  private static Double withGetExchangeRates(final Double value, String from, String to) {
    WS.Response wsResponse = getExchangeRates();

    JsonNode jsonResponse  = wsResponse.asJson().get(0);
    if (jsonResponse == null) {
      cache.remove(Source.GET_EXCHANGE_RATES);
      throw new CommunicationErrorException();
    }
    JsonNode fromRate = jsonResponse.get(from.toUpperCase());
    JsonNode toRate   = jsonResponse.get(to.toUpperCase());

    if (fromRate == null || toRate == null) {
      throw new InvalidCurrencyException();
    }

    double exchangeRate = new Double(new String(toRate.getTextValue())) /
                          new Double(new String(fromRate.getTextValue()));

    return value * exchangeRate;
  }
}