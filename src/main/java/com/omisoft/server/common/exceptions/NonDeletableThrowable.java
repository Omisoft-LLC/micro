package com.omisoft.server.common.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Thrown where we can't delete a row from the DB. Must be handled in UI code Created by dido on
 * 11/23/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)

public class NonDeletableThrowable extends Exception {
  private Throwable originalException;
  private String message;

  public NonDeletableThrowable(String msg, Throwable e) {
    message = msg;
    originalException = e;
  }
}
