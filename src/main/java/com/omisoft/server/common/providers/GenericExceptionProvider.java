package com.omisoft.server.common.providers;

import com.omisoft.server.common.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by dido on 16.03.17.
 */
@Slf4j
@Provider
public class GenericExceptionProvider implements ExceptionMapper<Exception> {
  @Override
  public Response toResponse(Exception e) {
    log.info("GENERIC ERROR");
    log.error("EXCEPTION:", e);

    return Response.status(500).type(MediaType.APPLICATION_JSON).entity(new ErrorDTO("SERVER ERROR", e.getMessage())).build();
  }
}
