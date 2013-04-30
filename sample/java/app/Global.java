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