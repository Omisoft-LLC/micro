package com.omisoft.server.common.dto;

import lombok.Data;

/**
 * Success DTO, when we have success operation, but no actual data. Message is random string used in
 * debugging Created by dido on 16.12.16.
 */
@Data
public class SuccessDTO {
  private String message;

  public SuccessDTO(String msg) {
    this.message = msg;
  }
}
