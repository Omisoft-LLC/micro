package com.omisoft.micro.common.microservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;

/**
 * Stop thread for jetty Created by dido on 10.01.17.
 */
@Slf4j
public class ShutdownMonitor extends Thread {

  private final Server server;
  private ServerSocket socket;

  public ShutdownMonitor(Server server, int port) {
    this.server = server;
    setDaemon(true);
    setName(ShutdownMonitor.class.getName());
    try {
      socket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    System.out.println("Running Keepassa 'stop' thread");
    Socket accept;
    try {
      accept = socket.accept();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(accept.getInputStream(), "UTF-8"));
      reader.readLine();
      System.out.println("*** stopping jetty embedded micro");
      server.stop();
      accept.close();
      socket.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

