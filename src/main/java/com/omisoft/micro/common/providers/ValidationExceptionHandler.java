package com.omisoft.micro.common.providers;

import com.omisoft.micro.common.dto.ErrorDTO;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Validation Exception provider Created by dido on 06.04.17.
 */
@Provider
@Slf4j
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {

  @Override
  public Response toResponse(ValidationException exception) {
    log.error(exception.getMessage());
    log.error("ValidationExceptionHandler !!");
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ErrorDTO("Wrong field validation", exception.getMessage())).build();
  }
}
