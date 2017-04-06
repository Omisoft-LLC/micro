package com.omisoft.server.common.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by leozhekov on 11/1/16.
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggedUserInfo {
  private String email;

  public LoggedUserInfo(String email) {
    this.email = email;
  }

  public LoggedUserInfo() {

  }

}
