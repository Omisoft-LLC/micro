package eu.omisoft.micro.common.di;

import javax.websocket.server.ServerEndpointConfig;
// TODO Move to common

/**
 * Helper class for guice - ws integration
 */
public class GuiceWebSocketConfigurator extends ServerEndpointConfig.Configurator {

  @Override
  public <T> T getEndpointInstance(Class<T> clazz)
      throws InstantiationException {

    return InjectorHolder.getInjector().getInstance(clazz);
  }
}