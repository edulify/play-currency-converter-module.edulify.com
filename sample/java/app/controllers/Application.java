package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import utils.Conversor;

public class Application extends Controller {

  public static Result index() {
    return ok();
  }

  public static Result convert(String from, String to) {
    String conversion = String.format("1%s = %s%s",
                                      from,
                                      Conversor.convert(1.0, from, to),
                                      to);
    return ok(conversion);
  }

}
