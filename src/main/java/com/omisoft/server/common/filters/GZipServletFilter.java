/**
 * Copyright 2003-2009 Terracotta, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omisoft.server.common.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides GZIP compression of responses.
 * <p/>
 * See the filter-mappings.xml entry for the gzip filter for the URL patterns which will be gzipped.
 * At present this includes .jsp, .js and .css.
 * <p/>
 *
 * @author <a href="mailto:gluck@thoughtworks.com">Greg Luck</a>
 * @author <a href="mailto:amurdoch@thoughtworks.com">Adam Murdoch</a>
 * @version $Id: GzipFilter.java 744 2008-08-16 20:10:49Z gregluck $
 */
@Singleton
@Slf4j
public class GZipServletFilter extends HttpFilter {

  private static final Logger LOG = LoggerFactory.getLogger(GZipServletFilter.class);
  private static final String VARY_HEADER_PARAM = "varyHeader";
  private static final String RETURN_ON_NOT_OK_PARAM = "returnOnNonOK";

  private boolean setVaryHeader;
  private boolean returnOnNonOk = true;

  /**
   * Performs initialisation.
   */
  protected void doInit(FilterConfig filterConfig) throws Exception {
    String varyParam = filterConfig.getInitParameter(VARY_HEADER_PARAM);
    if (varyParam != null) {
      setVaryHeader = Boolean.valueOf(varyParam);
    }

    String returnOnNotOkParam = filterConfig.getInitParameter(RETURN_ON_NOT_OK_PARAM);
    if (returnOnNotOkParam != null) {
      returnOnNonOk = Boolean.valueOf(returnOnNotOkParam);
    }
  }

  /**
   * A template method that performs any Filter specific destruction tasks. Called from {@link
   * #destroy()}
   */
  protected void doDestroy() {
    // noop
  }

  /**
   * Performs the filtering for a request.
   */
  public void doFilter(final HttpServletRequest request, final HttpServletResponse response,
      final HttpSession httpSession, final FilterChain chain) throws IOException, ServletException {
    if (!isIncluded(request) && acceptsGzipEncoding(request) && !response.isCommitted()) {
      // Client accepts zipped content
      if (LOG.isDebugEnabled()) {
        LOG.debug(request.getRequestURL() + ". Writing with gzip compression");
      }

      // Create a gzip stream
      final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
      final GZIPOutputStream gzout = new GZIPOutputStream(compressed);

      // Handle the request
      chain.doFilter(request, response);
      response.flushBuffer();

      gzout.close();

      // double check one more time before writing out
      // repsonse might have been committed due to error
      if (response.isCommitted()) {
        return;
      }

      // return on these special cases when content is empty or unchanged
      switch (response.getStatus()) {
        case HttpServletResponse.SC_NO_CONTENT:
        case HttpServletResponse.SC_RESET_CONTENT:
        case HttpServletResponse.SC_NOT_MODIFIED:
          return;
        default:
      }

      // Saneness checks
      byte[] compressedBytes = compressed.toByteArray();
      boolean shouldGzippedBodyBeZero = ResponseUtil
          .shouldGzippedBodyBeZero(compressedBytes, request);
      boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, response.getStatus());
      if (shouldGzippedBodyBeZero || shouldBodyBeZero) {
        // No reason to add GZIP headers or write body if no content was written or status code specifies no
        // content
        response.setContentLength(0);
        return;
      }

      // Write the zipped body
      ResponseUtil.addGzipHeader(response);

      response.setContentLength(compressedBytes.length);

      response.getOutputStream().write(compressedBytes);

    } else {
      // Client does not accept zipped content - don't bother zipping
      if (LOG.isDebugEnabled()) {
        LOG.debug(request.getRequestURL()
            + ". Writing without gzip compression because the request does not accept gzip.");
      }
      chain.doFilter(request, response);
    }
  }

  /**
   * Checks if the request uri is an include. These cannot be gzipped.
   */
  private boolean isIncluded(final HttpServletRequest request) {
    final String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
    final boolean includeRequest = !(uri == null);

    if (includeRequest && LOG.isDebugEnabled()) {
      LOG.debug(
          request.getRequestURL() + " resulted in an include request. This is unusable, because"
              + "the response will be assembled into the overrall response. Not gzipping.");
    }
    return includeRequest;
  }

  /**
   * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1. If a
   * browser accepts GZIP encoding it will advertise this by including in its HTTP header:
   * <p/>
   * <code> Accept-Encoding: gzip </code>
   * <p/>
   * Requests which do not accept GZIP encoding fall into the following categories: <ul> <li>Old
   * browsers, notably IE 5 on Macintosh. <li>Internet Explorer through a proxy. By default HTTP1.1
   * is enabled but disabled when going through a proxy. 90% of non gzip requests seen on the
   * Internet are caused by this. </ul> As of September 2004, about 34% of Internet requests do not
   * accept GZIP encoding.
   *
   * @return true, if the User Agent request accepts GZIP encoding
   */
  protected boolean acceptsGzipEncoding(HttpServletRequest request) {
    return acceptsEncoding(request, "gzip");
  }

}

final class ResponseUtil {


  private static final Logger LOG = LoggerFactory.getLogger(ResponseUtil.class);


  /**
   * Gzipping an empty file or stream always results in a 20 byte output
   * This is in java or elsewhere.
   * <p/>
   * On a unix system to reproduce do <code>gzip -n empty_file</code>. -n tells gzip to not
   * include the file name. The resulting file size is 20 bytes.
   * <p/>
   * Therefore 20 bytes can be used indicate that the gzip byte[] will be empty when ungzipped.
   */
  private static final int EMPTY_GZIPPED_CONTENT_SIZE = 20;

  /**
   * Utility class. No public constructor.
   */
  private ResponseUtil() {
    //noop
  }


  /**
   * Checks whether a gzipped body is actually empty and should just be zero.
   * When the compressedBytes is {@link #EMPTY_GZIPPED_CONTENT_SIZE} it should be zero.
   *
   * @param compressedBytes the gzipped response body
   * @param request the client HTTP request
   * @return true if the response should be 0, even if it is isn't.
   */
  public static boolean shouldGzippedBodyBeZero(byte[] compressedBytes,
      HttpServletRequest request) {

    //Check for 0 length body
    if (compressedBytes.length == EMPTY_GZIPPED_CONTENT_SIZE) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(request.getRequestURL() + " resulted in an empty response.");
      }
      return true;
    } else {
      return false;
    }
  }


  /**
   * Performs a number of checks to ensure response saneness according to the rules of RFC2616: <ol>
   * <li>If the response code is {@link javax.servlet.http.HttpServletResponse#SC_NO_CONTENT} then
   * it is illegal for the body to contain anything. See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5
   * <li>If the response code is {@link javax.servlet.http.HttpServletResponse#SC_NOT_MODIFIED} then
   * it is illegal for the body to contain anything. See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
   * </ol>
   *
   * @param request the client HTTP request
   * @param responseStatus the responseStatus
   * @return true if the response should be 0, even if it is isn't.
   */
  public static boolean shouldBodyBeZero(HttpServletRequest request, int responseStatus) {

    //Check for NO_CONTENT
    if (responseStatus == HttpServletResponse.SC_NO_CONTENT) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(request.getRequestURL() + " resulted in a " + HttpServletResponse.SC_NO_CONTENT
            + " response. Removing detailedMessage body in accordance with RFC2616.");
      }
      return true;
    }

    //Check for NOT_MODIFIED
    if (responseStatus == HttpServletResponse.SC_NOT_MODIFIED) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(request.getRequestURL() + " resulted in a " + HttpServletResponse.SC_NOT_MODIFIED
            + " response. Removing detailedMessage body in accordance with RFC2616.");
      }
      return true;
    }

    return false;
  }

  /**
   * Adds the gzip HTTP header to the response. This is need when a gzipped body is returned so that
   * browsers can properly decompress it.
   * <p/>
   *
   * @param response the response which will have a header added to it. I.e this method changes its
   * parameter from a {@link javax.servlet.RequestDispatcher#include(javax.servlet.ServletRequest,
   * javax.servlet.ServletResponse)} method and the set set header is ignored.
   */
  public static void addGzipHeader(final HttpServletResponse response) {
    response.setHeader("Content-Encoding", "gzip");

  }


}