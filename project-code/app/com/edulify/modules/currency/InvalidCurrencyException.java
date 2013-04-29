package com.edulify.modules.currency;

public class InvalidCurrencyException extends RuntimeException {
  public InvalidCurrencyException(String arg, Throwable cause) {
    super(arg, cause);
  }

  public InvalidCurrencyException(String arg) {
    super(arg);
  }

  public InvalidCurrencyException() {
    super();
  }
}