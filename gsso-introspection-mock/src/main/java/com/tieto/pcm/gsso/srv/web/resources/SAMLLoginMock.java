package com.tieto.pcm.gsso.srv.web.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/SAML")
public interface SAMLLoginMock {

  @POST
  @Path("/Login")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  Response mockSAMLLogin(@Context HttpHeaders headers, @FormParam("SAMLRequest") String samlRequest,
      @FormParam("userId") String userId, @FormParam("RelayState") String relayState, @FormParam("issuer") String issuer, @FormParam("ttl") int ttlSeconds);


}