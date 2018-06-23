package eu.omisoft.micro.common.microservice;

/**
 * Microservice list Created by dido on 30.05.16.
 */
public enum MicroServiceNameEnum {
  KEEPASSA_BACKEND("KEEPASSA-BACKEND");

  private final String hostname;
  private final int port;


  MicroServiceNameEnum(String hostname) {
    this.hostname = hostname;
    // Set Default port
    this.port = 8080;
  }

  MicroServiceNameEnum(String hostname, int port) {

    this.hostname = hostname;
    this.port = port;
  }


  public String getHostname() {
    return hostname;
  }

  public int getPort() {
    return port;
  }
}
