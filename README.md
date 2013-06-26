# play-currency-converter-module

This is a play module for currency conversion.

Currently, the module is using the [Get Exchange Rates](http://www.getexchangerates.com/) service to made the conversion.

## Configuring

The first step is include the currency converter in your dependencies list, in `Build.scala` file:

```
import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "currency-converter-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.edulify" % "currency-converter_2.10" % "1.1.4
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += Resolver.url("currency converter repository", url("http://blabluble.github.com/modules/releases/"))(Resolver.ivyStylePatterns)
  )

}

```

Don't forget to add the resolver to your list of resolvers, or it won't work!

### Caching

This module saves a cache of the request made for the service. The default cache time to live is 60 seconds, but this time can be changed using the method `setCacheTime(long seconds)`.

Also, you can set cache off, by calling the method `useCache(false)`. **Warning**: by setting off the cache, you can overload your system (and the service) due to multiple requests. Without cache activated, every call to Converter.convert will make a request to the choosen web service.

#### Caching auto-update job (optional)

In order to keep your cache always updated, you can create a background job to auto-update in frequent intervals (based on *time to live* of the cache). To do this, there are three simple steps to follow:

- Start the job inside the `Global` class:

```java
import play.Application;
import play.GlobalSettings;

import com.edulify.modules.currency.ConverterCacheJob;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
   ConverterCacheJob.startConverterCacheUpdate();
  }
}
```
- Set up your `Global` class in your `application.conf`:

```
# Global object class
# ~~~~~
# Define the Global object class for this application.
# Default to Global in the root package.
application.global=Global
```
- Set the name for the dispatcher that will do this job (also in the `application.conf` file):

```
converter.dispatcher.name = "converter"
```

### Precision

By default, the module makes operations using a precision of 100 decimals. You can change this value using the method `setPrecision(int precision)`. **Warning**: Low precisions can result in wrong conversions. Change it carefully.

### Global configurations

These configurations (about caching and precision) can also be setted globally in the `application.conf` file, using the following keys:

```
converter {
  cacheTTL  = 600
  useCache  = true
  precision = 100
}
```
or
```
converter.cacheTTL  = 10
converter.useCache  = true
converter.precision = 100
```

## Using

To use this module, its enough to import it in your class and use the static method `Converter.convert`:

```java
import com.edulify.modules.currency.Converter;

public class Application {
  public static Result index() {
    ...
    try {
      BigDecimal convertedValue = Converter.convert(new BigDecimal(199.0), "EUR", "USD");
    } catch (InvalidCurrencyException ex) {
      ...
    } catch (CommunicationErrorException ex) {
      ...
    }
    ...
  }
}
```

This method, as shown in the example, should raise two exceptions:
- `InvalidCurrencyException`: thrown when the currency code is not valid for the service.
- `CommuncationErrorException`: thrown when the service returns an invalid json.
