package com.tieto.pcm.gsso.srv.web.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/oauth2")
public interface MockOAuthLoginAndRedirect {

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("/login")
  Response loginAndRedirect(@Context HttpHeaders headers
                          , @FormParam("userId") String userId
                          , @FormParam("jti") String jti
                          , @FormParam("ttl") Long ttl);

}