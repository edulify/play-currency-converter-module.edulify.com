package com.edulify.modules.currency;

public class CommunicationErrorException extends RuntimeException {
  public CommunicationErrorException(String arg, Throwable cause) {
    super(arg, cause);
  }

  public CommunicationErrorException(String arg) {
    super(arg);
  }

  public CommunicationErrorException() {
    super();
  }
}