package com.omisoft.micro.common.providers;

import com.omisoft.micro.common.dto.ErrorDTO;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for Secure Keystore Exception
 * Created by dido on 16.03.17.
 */
@Provider
@Slf4j
public class SecurityExceptionProvider implements ExceptionMapper<SecurityException> {

  @Override
  public Response toResponse(SecurityException e) {
    log.error("Security Exception:", e);

    return Response.status(417).type(MediaType.APPLICATION_JSON)
        .entity(new ErrorDTO("SECURITY ERROR", e.getMessage()))
        .build();
  }
}