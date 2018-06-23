package eu.omisoft.micro.examples.configuration;


/**
 * Created by nslavov on 7/17/17.
 */
public final class ServerConstants {

  private ServerConstants() {
  }

  //Server
  public static final String CONFIG_FILE = "/opt/app/config/config.properties";
  public static final int HTTP_PORT = 8090;
  public static final int HTTPS_PORT = 8443;

  //config.properties
  public static final String DB_URL = "hibernate.hikari.dataSource.url";
  public static final String DB_USER = "hibernate.hikari.dataSource.user";
  public static final String DB_PASSWORD = "hibernate.hikari.dataSource.password";


}
