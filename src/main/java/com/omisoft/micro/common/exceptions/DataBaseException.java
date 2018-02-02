package com.omisoft.micro.common.exceptions;


/**
 * Created by leozhekov on 11/16/16. Exception that is thrown whenever there's a problem with the
 * database connection or transaction.
 */
public class DataBaseException extends Exception {

  public DataBaseException(String message) {
    super(message);
  }

  public DataBaseException(Throwable t) {
    super(t);
  }

  public DataBaseException(String s, Throwable e) {
    super(s, e);
  }
}
