package utils;

import com.edulify.modules.currency.Converter;

import com.edulify.modules.currency.CommunicationErrorException;
import com.edulify.modules.currency.InvalidCurrencyException;

public class Rates {

  public static Double convert(double value, String from, String to) {
    try {
      return Converter.convert(1.0, from, to);
    } catch (InvalidCurrencyException ex) {
      return 0.0;
    } catch (CommunicationErrorException ex) {
      return null;
    }
  }

}