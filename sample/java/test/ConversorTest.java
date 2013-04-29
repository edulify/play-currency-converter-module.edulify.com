package test;

import org.fest.assertions.Assertions;
import org.junit.Test;

import utils.Conversor;

public class ConversorTest {

  @Test
  public void should_return_null_when_currency_code_is_not_valid() {
    Assertions.assertThat(Conversor.convert(1.0, "brl", "some")).isNull();
  }

  @Test
  public void returned_rates_should_be_transitive() {
    Double brl_usd = Conversor.convert(1.0, "brl", "usd");
    Double usd_eur = Conversor.convert(1.0, "usd", "eur");
    Double brl_eur = Conversor.convert(1.0, "brl", "eur");

    Assertions.assertThat(Math.round(brl_usd * usd_eur * 100)).isEqualTo(Math.round(brl_eur * 100));
  }

}