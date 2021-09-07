package com.tieto.pcm.gsso.srv.web.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/introspect")
public interface IntrospectionMock {

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("/oauth2")
  public Response introspect(@FormParam("token") String token);

}