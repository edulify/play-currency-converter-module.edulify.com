package test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.fest.assertions.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.Rates;

import play.test.Helpers;
import play.test.FakeApplication;

public class RatesTest {

  protected static FakeApplication app;

  @BeforeClass
  public static void startApp() {
    app = Helpers.fakeApplication();
    Helpers.start(app);
  }

  @AfterClass
  public static void stopApp() {
    Helpers.stop(app);
    System.out.println();
  }

  @Test
  public void should_return_zero_when_currency_code_is_not_valid() {
    Assertions.assertThat(Rates.convert(new BigDecimal(1.0), "brl", "some")).isEqualTo(new BigDecimal(0.0));
  }

  @Test
  public void returned_rates_should_be_transitive() {
    BigDecimal brl_usd = Rates.convert(new BigDecimal(1.0), "brl", "usd");
    BigDecimal usd_eur = Rates.convert(new BigDecimal(1.0), "usd", "eur");
    BigDecimal brl_eur = Rates.convert(new BigDecimal(1.0), "brl", "eur");

    Assertions.assertThat(brl_usd.multiply(usd_eur)
                                 .setScale(5, RoundingMode.HALF_EVEN))
              .isEqualTo(brl_eur.setScale(5, RoundingMode.HALF_EVEN));
  }

  @Test
  public void returned_rates_should_be_simetric() {
    BigDecimal brl_usd = Rates.convert(new BigDecimal(1.0), "brl", "usd");
    BigDecimal usd_brl = Rates.convert(new BigDecimal(1.0), "usd", "brl");

    Assertions.assertThat(brl_usd.setScale(5, RoundingMode.HALF_EVEN))
              .isEqualTo(new BigDecimal(1).divide(usd_brl, 5, RoundingMode.HALF_EVEN));
  }

  @Test
  public void should_return_same_values_for_string_based_and_currency_based_calls() {
    Currency from = Currency.getInstance("USD");
    Currency to   = Currency.getInstance("BRL");
    BigDecimal v  = new BigDecimal(3);

    Assertions.assertThat(Rates.convert(v, from, to)).isEqualTo(Rates.convert(v, "usd", "brl"));
  }

}