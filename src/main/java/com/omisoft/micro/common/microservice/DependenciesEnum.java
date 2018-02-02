package com.omisoft.micro.common.microservice;

/**
 * Holds dependencies for microservices Created by dido on 10.08.16.
 */
public enum DependenciesEnum {
  DB("omibox-db", 5432), RABBITMQ("omibox-rabbitmq", 5672), REDIS("omibox-redis", 6379), REGISTRY(
      "omibox-registry", 8081), OMIBOX_WS("omibox-ws", 10080), OMIBOX_GATEWAY("omibox-gateway",
      8080), OMIBOX_VIDEOPIPELINE("omibox-videopipeline", 8080);
  String host;
  int port;

  DependenciesEnum(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

}
