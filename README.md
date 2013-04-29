# play-currency-converter-module

This is a play module for currency conversion.

Currently, the module is using the [Get Exchange Rates](http://www.getexchangerates.com/) service to made the conversion.

## How to use

In order to use this module, its enough to import it in your class and use the static method `Converter.convert`:

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