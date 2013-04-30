package com.edulify.modules.currency;

import java.util.concurrent.TimeUnit;

import play.Play;
import play.cache.Cache;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

import akka.dispatch.MessageDispatcher;

public class ConverterCacheJob implements Runnable {
  @Override
  public void run() {
    Converter.getExchangeRates();
  }

  public static void startConverterCacheUpdate() {
    String dispatcherName = Play.application().configuration().getString("converter.dispatcher.name");
    play.Logger.debug("dispatcherName: " + dispatcherName);
    play.Logger.debug("Akka.system(): " + Akka.system());
    play.Logger.debug("Akka.system().dispatchers(): " + Akka.system().dispatchers());
    play.Logger.debug("Akka.system().dispatchers().lookup(dispatcherName): " + Akka.system().dispatchers().lookup(dispatcherName));
    MessageDispatcher executionContext = Akka.system().dispatchers().lookup(dispatcherName);
    Akka.system()
        .scheduler()
        .schedule(
            FiniteDuration.create(0, TimeUnit.MILLISECONDS),
            FiniteDuration.create(Converter.cacheTTL, TimeUnit.SECONDS),
            new ConverterCacheJob(),
            executionContext
        );
  }
}