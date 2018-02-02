package com.omisoft.micro.common.dto;

import com.omisoft.micro.common.enums.Severity;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ErrorDTO, transfers error messages to clients Created by leozhekov on 11/1/16.
 */
@Getter
@Setter
@ToString
public class ErrorDTO {

  private String detailedMessage;

  private Severity severity;

  private int errorCode;

  private String err;

  private Map<String, String> validationMessages = new HashMap<>();

  public ErrorDTO() {

  }

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
   * @param detailedMessage error title
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ErrorDTO errorDTO = (ErrorDTO) o;

    if (errorCode != errorDTO.errorCode) {
      return false;
    }
    if (detailedMessage != null ? !detailedMessage.equals(errorDTO.detailedMessage)
        : errorDTO.detailedMessage != null) {
      return false;
    }
    if (severity != errorDTO.severity) {
      return false;
    }
    if (err != null ? !err.equals(errorDTO.err) : errorDTO.err != null) {
      return false;
    }
    return validationMessages != null ? validationMessages.equals(errorDTO.validationMessages)
        : errorDTO.validationMessages == null;
  }

  @Override
  public int hashCode() {
    int result = detailedMessage != null ? detailedMessage.hashCode() : 0;
    result = 31 * result + (severity != null ? severity.hashCode() : 0);
    result = 31 * result + errorCode;
    result = 31 * result + (err != null ? err.hashCode() : 0);
    result = 31 * result + (validationMessages != null ? validationMessages.hashCode() : 0);
    return result;
  }

}
