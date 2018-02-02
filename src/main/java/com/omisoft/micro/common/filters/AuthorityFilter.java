package com.omisoft.micro.common.filters;

import static com.omisoft.micro.common.constants.CommonConstants.AUTHORIZATION_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.micro.common.auth.AuthUtils;
import com.omisoft.micro.common.auth.UserAuthority;
import com.omisoft.micro.common.dto.ErrorDTO;
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


/**
 * Authorization filter for REST services. Works with JWT header Created by nslavov on 3/25/16.
 */
@Singleton
@Slf4j
public class AuthorityFilter implements Filter {


  @Inject
  private UserAuthority authority;

  @Inject
  private ObjectMapper mapper;

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
        forbidden(httpResponse);
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
        forbidden(httpResponse);
        return;
      }

      // ensure that the token is not expired
      if (AuthUtils.expired(authHeader)) {
        forbidden(httpResponse);
        return;
      }


    } catch (Throwable e) {
      e.printStackTrace();
      log.info(e.getMessage());
      forbidden(httpResponse);
      return;
    }
    log.info(String.valueOf(httpResponse.getStatus()));
    filterChain.doFilter(request, response);
  }

  private void forbidden(HttpServletResponse response) {
    response.setStatus(403);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    try {
      response.getWriter().write(
          mapper.writeValueAsString(new ErrorDTO("Forbidden", "Please try logging in again")));
    } catch (IOException e) {
      e.printStackTrace();
      log.info(e.getMessage());
    }
  }


  @Override
  public void destroy() {

  }
}
