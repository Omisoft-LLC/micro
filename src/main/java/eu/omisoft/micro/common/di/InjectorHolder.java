package eu.omisoft.micro.common.di;

import com.google.inject.Injector;

/**
 * Holds instance of the Guice injector. Should be filled after initialization of Injector (after
 * calling createInjector())
 */
public final class InjectorHolder {

  private static Injector staticInjector;

  private InjectorHolder() {
  }

  public static Injector getInjector() {
    return staticInjector;
  }

  public static void setInjector(Injector injector) {
    staticInjector = injector;
  }
}
