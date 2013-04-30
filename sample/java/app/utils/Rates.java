package utils;

import java.math.BigDecimal;

import com.edulify.modules.currency.Converter;

import com.edulify.modules.currency.CommunicationErrorException;
import com.edulify.modules.currency.InvalidCurrencyException;

public class Rates {

  public static BigDecimal convert(BigDecimal value, String from, String to) {
    Converter.setCacheTime(5);
    try {
      return Converter.convert(value, from, to);
    } catch (InvalidCurrencyException ex) {
      return new BigDecimal(0.0);
    } catch (CommunicationErrorException ex) {
      return null;
    }
  }

}