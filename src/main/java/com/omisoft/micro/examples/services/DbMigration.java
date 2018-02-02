package com.omisoft.micro.examples.services;



import static com.omisoft.micro.common.constants.CommonConstants.DEV;
import static com.omisoft.micro.common.constants.CommonConstants.DEV_TRUE;
import static com.omisoft.micro.examples.configuration.ServerConstants.DB_PASSWORD;
import static com.omisoft.micro.examples.configuration.ServerConstants.DB_URL;
import static com.omisoft.micro.examples.configuration.ServerConstants.DB_USER;

import com.omisoft.micro.examples.configuration.Configuration;
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
        Configuration.getInstance().getProp().getProperty(DB_URL),Configuration.getInstance().getProp().getProperty(DB_USER),
        Configuration.getInstance().getProp().getProperty(DB_PASSWORD));
    flyway.setSchemas("public");
    flyway.setSqlMigrationSeparator("_");
    String dev = System.getProperty(DEV);
    if(dev != null && DEV_TRUE.equals(dev)){
      flyway.setSqlMigrationPrefix("D");
      log.info("services SET DEV MODE");
    } else {
      flyway.setSqlMigrationPrefix("V");
    }

    flyway.migrate();
    log.info("services END");
  }

}
