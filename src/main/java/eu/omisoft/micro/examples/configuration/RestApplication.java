package eu.omisoft.micro.examples.configuration;

import eu.omisoft.micro.examples.rest.TestEndpoint;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dido on 05.04.17.
 */
@ApplicationPath("")
public class RestApplication extends Application {

  @Override
  public Set<Object> getSingletons() {
    HashSet<Object> set = new HashSet<>();

    set.add(new TestEndpoint());
    return set;
  }
}
