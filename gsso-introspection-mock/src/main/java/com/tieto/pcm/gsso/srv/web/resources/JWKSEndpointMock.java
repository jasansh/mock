package com.tieto.pcm.gsso.srv.web.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/mock")
public interface JWKSEndpointMock {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/JWKS")
  Response mockJWKSEndpoint();

}