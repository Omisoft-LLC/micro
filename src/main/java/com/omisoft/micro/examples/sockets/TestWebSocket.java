package com.omisoft.micro.examples.sockets;

import com.google.inject.Inject;
import com.omisoft.micro.common.di.GuiceWebSocketConfigurator;
import com.omisoft.micro.common.interfaces.WebSocket;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * Streams live race statistics
 */
@ServerEndpoint(value = "/testWS", configurator = GuiceWebSocketConfigurator.class)
@Slf4j
public class TestWebSocket implements WebSocket {


  private static final int MAX_IDLE_TIMEOUT = 60 * 1000;

  @Inject
  public TestWebSocket() {

  }

  @OnOpen
  public void onWebSocketConnect(@PathParam("raceId") String raceId, Session sess) {
    log.info(raceId);
    sess.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);
    log.info("Race Stats Socket Connected: " + sess);

  }

  // TODO handle close
  @OnClose
  public void onWebSocketClose(Session sess) {
    log.info("Stat socket closed");

  }

  @OnError
  public void onWebSocketError(Session sess, Throwable throwable) {
    log.error("Stat socket error", throwable);

  }


}
