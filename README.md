# play-currency-converter-module

This is a play module for currency conversion.

Currently, the module is using the [Get Exchange Rates](http://www.getexchangerates.com/) service to made the conversion.

## How to use

### Configuring

The first step is include the sitemapper in your dependencies list, in `Build.scala` file:

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
    "com.edulify" % "currency-converter_2.10" % "1.1.1
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += Resolver.url("sitemapper repository", url("http://blabluble.github.com/modules/releases/"))(Resolver.ivyStylePatterns)
  )

}

```

Don't forget to add the resolver to your list of resolvers, or it won't work!

### Using

To use this module, its enough to import it in your class and use the static method `Converter.convert`:

```java
import com.edulify.modules.currency.Converter;

public class Application {
  public static Result index() {
    ...
    try {
      Double convertedValue = Converter.convert(199.0, "EUR", "USD");
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

## About caching

This class saves a cache of the request made for the service. The default cache time is 60 seconds, but this time can be changed using the method `setCacheTime(long milliseconds)`.

Also, you can set cache off, by calling the method `useCache(false)`.