package com.omisoft.server.common.exceptions;

/**
 * Keystore operation exception Ceated by dido on 17.12.16.
 */
public class SecurityException extends Exception {

  public SecurityException(Exception e) {
    super(e);
  }

  public SecurityException() {

  }

  public SecurityException(String s, Exception e) {
    super(s, e);
  }

  public SecurityException(String message) {
    super(message);
  }
}
