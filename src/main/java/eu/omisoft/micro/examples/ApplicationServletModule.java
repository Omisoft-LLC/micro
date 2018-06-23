package eu.omisoft.micro.examples;

import com.google.inject.servlet.ServletModule;
import eu.omisoft.micro.common.filters.AuthorityFilter;
import eu.omisoft.micro.common.filters.GZipServletFilter;
import eu.omisoft.micro.common.filters.PersistFilter;


/**
 * Configure servlets and filters
 */
public class ApplicationServletModule extends ServletModule {

  @Override
  protected void configureServlets() {

    filter("/*").through(GZipServletFilter.class);
    filter(RestUrl.REST + "/*").through(PersistFilter.class);
    filter(RestUrl.REST + RestUrl.SECURE + "/*").through(AuthorityFilter.class);

  }

}

