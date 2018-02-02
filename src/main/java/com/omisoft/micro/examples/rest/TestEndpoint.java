package com.omisoft.micro.examples.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

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