package com.omisoft.server.common.dto;

import com.omisoft.server.common.enums.Severity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorDTO, transfers error messages to clients
 * Created by leozhekov on 11/1/16.
 */
@Data
public class ErrorDTO {
  private String detailedMessage;

  private Severity severity;

  private int errorCode;

  private String err;

  private Map<String, String> validationMessages = new HashMap<>();


  /**
   * Constructs new ErrorDto.
   *
   * @param detailedMessage error title
   */
  public ErrorDTO(String err, String detailedMessage) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    // this.severity = Severity.ERROR;

  }

  public ErrorDTO(String err, Throwable exception) {
    this.err = err;
    this.detailedMessage = exception.getMessage();
  }

  public ErrorDTO(String err, String detailedMessage, Map<String, String> validationMessages) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.validationMessages = validationMessages;
  }

  /**
   * Constructs new error dto and set severity.
   *
   * @param detailedMessage  error title
   * @param severity severity
   */
  public ErrorDTO(String err, String detailedMessage, Severity severity) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.severity = severity;
  }

  public ErrorDTO(String err, String detailedMessage, Severity severity, int errorCode) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.severity = severity;
    this.errorCode = errorCode;
  }
}
