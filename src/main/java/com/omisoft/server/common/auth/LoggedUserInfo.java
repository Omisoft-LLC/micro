package com.omisoft.server.common.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by leozhekov on 11/1/16.
 */
@Getter
@Setter
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggedUserInfo {

  private String email;
  private String userId;

  public LoggedUserInfo(String email, String userId) {
    this.email = email;
    this.userId = userId;
  }

  public LoggedUserInfo() {
  }
}
