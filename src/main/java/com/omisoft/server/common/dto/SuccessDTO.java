package com.omisoft.server.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Success DTO, when we have success operation, but no actual data. Message is random string used in
 * debugging Created by dido on 16.12.16.
 */

@Getter
@Setter
@ToString
public class SuccessDTO {
  private String message;

  public SuccessDTO() {

  }
  public SuccessDTO(String msg) {
    this.message = msg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SuccessDTO that = (SuccessDTO) o;

    return message != null ? message.equals(that.message) : that.message == null;
  }

  @Override
  public int hashCode() {
    return message != null ? message.hashCode() : 0;
  }
}
