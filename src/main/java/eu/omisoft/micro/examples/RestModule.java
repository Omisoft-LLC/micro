package eu.omisoft.micro.examples;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import eu.omisoft.micro.common.providers.*;
import eu.omisoft.micro.examples.rest.TestEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.util.Map;

/**
 * All redis resources should be described here Created by dido on 12/6/16.
 */
@Slf4j
public class RestModule extends ServletModule {

  private final String path;

  public RestModule() {
    this.path = RestUrl.REST;
  }

  public RestModule(final String path) {
    this.path = path;  // e.g., "/api"
  }

  @Override
  protected void configureServlets() {

    bind(GuiceResteasyBootstrapServletContextListener.class);
    bind(HttpServletDispatcher.class).in(Singleton.class);
//    bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
//    bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
    bind(DataBaseExceptionProvider.class);
    bind(SecurityExceptionProvider.class);
    bind(ValidationExceptionHandler.class);
    bind(NotFoundExceptionProvider.class);
    bind(GenericExceptionProvider.class);

    // Add Endpoints
    bind(TestEndpoint.class);

    if (path == null) {
      serve("/*").with(HttpServletDispatcher.class);
    } else {
      final Map<String, String> initParams =
          ImmutableMap.of("resteasy.servlet.mapping.prefix", path);
      serve(path + "/*").with(HttpServletDispatcher.class, initParams);
    }

    log.info("INITING REST SERVICES");
  }
}
