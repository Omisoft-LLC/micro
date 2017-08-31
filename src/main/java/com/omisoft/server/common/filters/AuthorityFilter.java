package com.omisoft.server.common.filters;

import static com.omisoft.server.common.constants.CommonConstants.AUTHORIZATION_HEADER;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.omisoft.server.common.auth.AuthUtils;
import com.omisoft.server.common.auth.LoggedUserInfo;
import com.omisoft.server.common.auth.UserAuthority;
import com.omisoft.server.common.exceptions.SecurityException;
import java.io.IOException;
import java.text.ParseException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;


/**
 * Authorization filter for REST services.
 * Works with JWT header
 * Created by nslavov on 3/25/16.
 */
@Singleton
@Slf4j
public class AuthorityFilter implements Filter {


  @Inject
  private UserAuthority authority;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  // TODO Refactor
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
    log.info(authHeader);
    String method = httpRequest.getMethod();
    Cookie[] cokkies = httpRequest.getCookies();
    Cookie authCookie;
    if (StringUtils.isBlank(authHeader) || authHeader.equals("null")) {
      for (Cookie c : cokkies) {
        if (AUTHORIZATION_HEADER.equalsIgnoreCase(c.getName())) {
          authCookie = c;
          authHeader = authCookie.getValue();
          break;
        }
      }
    } else {
      log.info("AUTHORIZATION_HEADER NOT FOUND !!");
    }

    if (method.equals("OPTIONS")) {
      httpResponse.setStatus(200);
    } else {
      if ((StringUtils.isBlank(authHeader) || !authority.isExist(authHeader))) {
        httpResponse.setStatus(401);
      } else {
        JWTClaimsSet claimSet;
        try {
          claimSet = (JWTClaimsSet) AuthUtils.decodeToken(authHeader);
        } catch (ParseException | JOSEException | SecurityException e) {
          httpResponse.setStatus(401);
          return;
        }
        // ensure that the token is not expired
        if (new DateTime(claimSet.getExpirationTime()).isBefore(DateTime.now())) {
          httpResponse.setStatus(401);
        } else {
          LoggedUserInfo redisDTO = authority.getUser(authHeader);
//          request.setAttribute(CommonConstants.LOGGED_USER, redisDTO);
        }
      }
    }
    filterChain.doFilter(request, response);

  }

  @Override
  public void destroy() {

  }
}
