package com.omisoft.micro.common.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omisoft.micro.common.dto.ErrorDTO;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 16.03.17.
 */
@Provider
@Slf4j
public class JsonExceptionProvider implements ExceptionMapper<JsonProcessingException> {

  @Override
  public Response toResponse(JsonProcessingException e) {
    log.error("JSON EXCEPTION:", e);
    return Response.status(403).type(MediaType.APPLICATION_JSON)
        .entity(new ErrorDTO("INPUT  ERROR", e.getMessage()))
        .build();
  }
}
