package com.omisoft.server.common.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic http filter
 */
@Slf4j
public abstract class HttpFilter implements Filter {

  private FilterConfig filterConfig;

  public HttpFilter() {
  }

  protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
    return headerContains(request, "Accept-Encoding", name);
  }

  /**
   * Checks if request contains the header value.
   */
  private boolean headerContains(final HttpServletRequest request, final String header,
      final String value) {

    logRequestHeaders(request);

    final Enumeration accepted = request.getHeaders(header);
    while (accepted.hasMoreElements()) {
      final String headerValue = (String) accepted.nextElement();
      if (headerValue.contains(value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Logs the request headers, if debug is enabled.
   */
  protected void logRequestHeaders(final HttpServletRequest request) {
    if (log.isDebugEnabled()) {
      Map headers = new HashMap();
      Enumeration enumeration = request.getHeaderNames();
      StringBuilder logLine = new StringBuilder();
      logLine.append("Request Headers");
      while (enumeration.hasMoreElements()) {
        String name = (String) enumeration.nextElement();
        String headerValue = request.getHeader(name);
        headers.put(name, headerValue);
        logLine.append(": ").append(name).append(" -> ").append(headerValue);
      }
      log.debug(logLine.toString());
    }
  }


  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
    this.init();
  }

  public void init() throws ServletException {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    HttpSession session = httpRequest.getSession(false);
    this.doFilter(httpRequest, httpResponse, session, chain);
  }

  public abstract void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain) throws ServletException, IOException;

  public void destroy() {
    this.filterConfig = null;
  }

  protected FilterConfig getFilterConfig() {
    this.checkFilterConfig();
    return this.filterConfig;
  }

  protected String getInitParameter(String name) {
    this.checkFilterConfig();
    return this.filterConfig.getInitParameter(name);
  }

  protected ServletContext getServletContext() {
    this.checkFilterConfig();
    return this.filterConfig.getServletContext();
  }

  private void checkFilterConfig() {
    if (this.filterConfig == null) {
      throw new IllegalStateException(
          "FilterConfig is not available. It seems that you\'ve overriden HttpFilter#init(FilterConfig). You should be overriding HttpFilter#init() instead, otherwise you have to call super.init(config).");
    }
  }
}
