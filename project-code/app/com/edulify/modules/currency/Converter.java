package com.edulify.modules.currency;

import static play.mvc.Controller.async;
import static play.mvc.Controller.ok;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Function;
import play.libs.WS;
import play.mvc.Result;

public class Converter {

  public enum Source {
    GET_EXCHANGE_RATES
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

  private static Double withGetExchangeRates(final Double value, String from, String to) {
    String url = String.format("http://www.getexchangerates.com/api/latest.json");
    WS.Response wsResponse = WS.url(url).get().get(10000l);

    JsonNode jsonResponse  = wsResponse.asJson().get(0);
    if (jsonResponse == null) {
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