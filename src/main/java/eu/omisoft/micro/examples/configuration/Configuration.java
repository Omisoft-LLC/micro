package eu.omisoft.micro.examples.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nslavov on 7/17/17.
 */
@Slf4j
public class Configuration {

  private Properties prop = new Properties();

  private static final Configuration instance = new Configuration();

  private Configuration() {
    load();
  }

  public static Configuration getInstance() {
    return instance;
  }


  private void load() {
    log.info("LOAD CONFIGURATION");

    try (FileInputStream input = new FileInputStream(new File(ServerConstants.CONFIG_FILE))) {
      this.prop.load(input);
    } catch (IOException e) {
      log.info(e.getMessage());
    }
  }

  public Properties getProp() {
    return prop;
  }
}
