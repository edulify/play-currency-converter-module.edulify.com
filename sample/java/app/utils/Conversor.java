package utils;

import com.edulify.modules.currency.Converter;

public class Conversor {

  public static Double convert(double value, String from, String to) {
    return Converter.convert(1.0, from, to);
  }

}