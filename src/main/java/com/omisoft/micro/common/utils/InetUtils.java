package com.omisoft.micro.common.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Various Inet utility methods
 * Created by dido on 22.06.16.
 */
@Slf4j
public class InetUtils {

  public static Integer getPort() {

    String port = System.getProperty("jetty.port");
    if (StringUtils.isEmpty(port)) {
      port = "8080";
    }
    return Integer.parseInt(port);
  }

  public static String getIpAddresAsStrings() {
    try {
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
      while (e.hasMoreElements()) {
        NetworkInterface n = e.nextElement();
        Enumeration ee = n.getInetAddresses();
        while (ee.hasMoreElements()) {

          InetAddress i = (InetAddress) ee.nextElement();
          if (i instanceof Inet4Address && !i.isLoopbackAddress() && i.isSiteLocalAddress()
              && !i.getHostAddress().equals(i.getHostName())) {
            log.info(i.getHostAddress());
            return i.getHostAddress();
          }

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "127.0.0.1";
  }

  public static InetAddress getlIpAddress() {
    try {
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
      while (e.hasMoreElements()) {
        NetworkInterface n = e.nextElement();
        Enumeration ee = n.getInetAddresses();
        while (ee.hasMoreElements()) {

          InetAddress i = (InetAddress) ee.nextElement();
          if (i instanceof Inet4Address && !i.isLoopbackAddress() && i.isSiteLocalAddress()
              && !i.getHostAddress().equals(i.getHostName())) {
            return i;
          }

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Checks availability of tcp service and blocks if not available
   */
  public static boolean checkAvailabilityAndBlock(String hostname, int port)
      throws InterruptedException {
    Socket s = null;
    while (true) {
      try {
        s = new Socket(hostname, port);
        // Give service 1 s to actually get live
        Thread.sleep(1000);
        return true;
      } catch (IOException e) {
        log.info("WAITING FOR " + hostname + ":" + port);
        Thread.sleep(1000);

      } finally {
        if (s != null) {
          try {
            s.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
