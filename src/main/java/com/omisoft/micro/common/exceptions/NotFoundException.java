package com.omisoft.micro.common.exceptions;

/**
 * Created by leozhekov on 11/18/16. Exception that is thrown whenever the query to the db returns
 * no value, null value or empty result list.
 */
public class NotFoundException extends Exception {

  public NotFoundException(String message) {
    super(message);
  }
}
