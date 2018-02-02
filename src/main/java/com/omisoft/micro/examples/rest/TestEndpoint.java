package com.omisoft.micro.examples.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by dido on 05.04.17.
 */
@Path("/test")
public class TestEndpoint {


  @GET
  public String test() {
    return "Hello World";
  }



}