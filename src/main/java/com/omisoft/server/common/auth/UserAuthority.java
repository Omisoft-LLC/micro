package com.omisoft.server.common.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

/**
 * Holds Logged users
 * Created by nslavov on 3/25/16.
 */
public class UserAuthority {

  private static final int TIMEOUT = 7200;
  private static UserAuthority INSTANCE;
  private final ObjectMapper mapper;
  private final Cache<String, String> loggedUsers;

  private UserAuthority() {
    this.mapper = new ObjectMapper();
    loggedUsers = CacheBuilder.newBuilder().expireAfterAccess(TIMEOUT, TimeUnit.SECONDS).build();
  }

  public static UserAuthority getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new UserAuthority();
    }
    return INSTANCE;
  }

  public void addUser(String token, String email, String userId) throws JsonProcessingException {
    LoggedUserInfo loggedUserInfo;
    loggedUserInfo = new LoggedUserInfo(email, userId);

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

  public boolean belongs(String token, String userId) {
    LoggedUserInfo userInfo = getUser(token);
    return userInfo != null ? userInfo.getUserId().equals(userId) : false;
  }


  public void removeUser(String token) {
    loggedUsers.invalidate(token);

  }
}
