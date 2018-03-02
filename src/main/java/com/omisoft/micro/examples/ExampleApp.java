package com.omisoft.micro.examples;

import static com.omisoft.micro.common.constants.CommonConstants.DEV;
import static com.omisoft.micro.common.constants.CommonConstants.DEV_TRUE;

import com.omisoft.micro.common.di.CommonModule;
import com.omisoft.micro.common.di.DbModule;
import com.omisoft.micro.common.microservice.MicroServiceApp;
import com.omisoft.micro.examples.configuration.Configuration;
import com.omisoft.micro.examples.services.DbMigration;
import com.omisoft.micro.examples.sockets.TestWebSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleApp extends MicroServiceApp {

  public static void main(String[] args) throws Exception {
    System.setProperty(DEV, DEV_TRUE);
    int port = 8010;
    if (args.length > 0) {
      log.info(args[0]);
      port = Integer.parseInt(args[0]);
    }
//    DbMigration dbMigration = new DbMigration();
//    dbMigration.start();
    ExampleApp mainApp = new ExampleApp();
    mainApp.setShutdownPort(8179).addHttp(port).addJmxSupport()
        .addDISupport(new CommonModule(), new DependencyModule(),
            new DbModule("testPersistenceUnit",
                Configuration.getInstance().getProp()),
            new ApplicationServletModule(), new RestModule())
        .addWebSockets(RestUrl.WS_PATH, TestWebSocket.class)
        .addJmxSupport();

    mainApp.start("Example App", "/var/log/exampleapp");

  }

  public void preSetup() {
    System.setProperty("jetty.port", String.valueOf(8080));
  }

}
