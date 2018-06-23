package eu.omisoft.micro.common.filters;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

@Singleton
@Slf4j
public final class LoggingFilter extends HttpFilter {

  private FilterConfig filterConfig = null;

  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain)
      throws IOException, ServletException {

    long start = System.currentTimeMillis();
    String address = request.getRemoteAddr();
    String res = request.getRequestURI();

    chain.doFilter(request, response);
    Enumeration<String> enumeration = request.getHeaderNames();
    log.info(" User IP: " + address + " Resource: " + res + " Milliseconds used: "
        + (System.currentTimeMillis() - start));
    log.info("HEADERS:");
    while (enumeration.hasMoreElements()) {
      String header = enumeration.nextElement();
      Enumeration<String> headerEnum = request.getHeaders(header);
      while (headerEnum.hasMoreElements()) {
        log.info(header + ":" + headerEnum.nextElement());

      }
    }
    // todo fix this to print body
    // if ("POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
    // try {
    // BufferedReader reader = request.getReader();
    // String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
    // log.info("POST BODY: " + body);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // }
  }

  public void destroy() {
  }

  public void init(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }
}
