package eu.omisoft.micro.common.providers;


import eu.omisoft.micro.common.dto.ErrorDTO;
import eu.omisoft.micro.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by dido on 16.03.17.
 */
@Provider
@Slf4j
public class NotFoundExceptionProvider implements ExceptionMapper<NotFoundException> {

  @Override
  public Response toResponse(NotFoundException e) {
    log.error("NOT FOUND EXCEPTION:", e);

    return Response.status(404).type(MediaType.APPLICATION_JSON)
        .entity(new ErrorDTO("NOT FOUND", e.getMessage())).build();
  }
}
