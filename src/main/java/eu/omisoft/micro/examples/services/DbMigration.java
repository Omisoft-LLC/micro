package eu.omisoft.micro.examples.services;


import eu.omisoft.micro.common.constants.CommonConstants;
import eu.omisoft.micro.examples.configuration.Configuration;
import eu.omisoft.micro.examples.configuration.ServerConstants;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

/**
 * Created by nslavov on 6/20/17.
 */
@Slf4j
public class DbMigration {

  public void start() {
    log.info("services STARTED");
    Flyway flyway = new Flyway();
    flyway.setValidateOnMigrate(false);
    flyway.setDataSource(
            Configuration.getInstance().getProp().getProperty(ServerConstants.DB_URL),
            Configuration.getInstance().getProp().getProperty(ServerConstants.DB_USER),
            Configuration.getInstance().getProp().getProperty(ServerConstants.DB_PASSWORD));
    flyway.setSchemas("public");
    flyway.setSqlMigrationSeparator("_");
    String dev = System.getProperty(CommonConstants.DEV);
    if (dev != null && CommonConstants.DEV_TRUE.equals(dev)) {
      flyway.setSqlMigrationPrefix("D");
      log.info("services SET DEV MODE");
    } else {
      flyway.setSqlMigrationPrefix("V");
    }

    flyway.migrate();
    log.info("services END");
  }

}
