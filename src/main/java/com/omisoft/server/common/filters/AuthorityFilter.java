package com.omisoft.server.common.filters;

import static com.omisoft.server.common.constants.CommonConstants.AUTHORIZATION_HEADER;

import com.nimbusds.jwt.JWTClaimsSet;
import com.omisoft.server.common.auth.AuthUtils;
import com.omisoft.server.common.auth.UserAuthority;
import java.io.IOException;
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
    log.info(httpRequest.getRequestURI());
    String method = httpRequest.getMethod();
    Cookie[] cokkies = httpRequest.getCookies();
    Cookie authCookie;
    if (StringUtils.isBlank(authHeader) || authHeader.equals("null")) {
      if (cokkies == null || cokkies.length == 0) {
        httpResponse.sendError(403);
        return;
      }
      for (Cookie c : cokkies) {
        if (AUTHORIZATION_HEADER.equalsIgnoreCase(c.getName())) {
          authCookie = c;
          authHeader = authCookie.getValue();
          break;
        }
      }
    }
    try {

      if (method.equals("OPTIONS")) {
        httpResponse.setStatus(200);
        filterChain.doFilter(request, response);
      }

      if ((StringUtils.isBlank(authHeader) || !authority.isExist(authHeader))) {
        httpResponse.sendError(403);
        return;
      }

//      JWTClaimsSet claimSet;
//      claimSet = AuthUtils.decodeToken(authHeader);
      // ensure that the token is not expired

      if (AuthUtils.expired(authHeader)) {
        httpResponse.sendError(403);
        return;
      }


    } catch (Throwable e) {
      e.printStackTrace();
      log.info(e.getMessage());
      httpResponse.sendError(403);
      return;
    }
    log.info(String.valueOf(httpResponse.getStatus()));
    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }
}
