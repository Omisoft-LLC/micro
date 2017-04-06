package com.omisoft.server.common.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Holds Logged users
 * Created by nslavov on 3/25/16.
 */
@Singleton
public class UserAuthority {
  private static final int TIMEOUT = 7200;
  private final ObjectMapper mapper;
  private final Cache<String, String> loggedUsers;

  @Inject
  public UserAuthority(ObjectMapper mapper) {
    this.mapper = mapper;
    loggedUsers = CacheBuilder.newBuilder().expireAfterAccess(TIMEOUT, TimeUnit.SECONDS).build();
  }

  public void addUser(String token, String email) throws JsonProcessingException {
    LoggedUserInfo loggedUserInfo;
    loggedUserInfo = new LoggedUserInfo(email);

    loggedUsers.put(token, mapper.writeValueAsString(loggedUserInfo));

  }

  public LoggedUserInfo getUser(String token) {

    String user = loggedUsers.getIfPresent(token);
    if (!StringUtils.isBlank(user)) {
      try {
        return mapper.readValue(user, LoggedUserInfo.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      return null;
    }
    return null;
  }

  public boolean isExist(String token) {
    String user = loggedUsers.getIfPresent(token);
    return !StringUtils.isBlank(user);
  }


  public void removeUser(String token) {
    loggedUsers.invalidate(token);

  }
}
