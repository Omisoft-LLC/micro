package eu.omisoft.micro.common.providers;


import eu.omisoft.micro.common.dto.ErrorDTO;
import eu.omisoft.micro.common.exceptions.DataBaseException;
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
public class DataBaseExceptionProvider implements ExceptionMapper<DataBaseException> {

  @Override
  public Response toResponse(DataBaseException e) {
    log.info("DB ERROR");
    log.error("EXCEPTION", e);
    return Response.status(500).type(MediaType.APPLICATION_JSON)
        .entity(new ErrorDTO("DATABASE ERROR", e.getMessage())).build();
  }
}
