package com.tieto.pcm.gsso.srv.web.resources.jwks;

import javax.ws.rs.core.Response;

import org.springframework.stereotype.Controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.tieto.pcm.gsso.srv.web.cert.CertificateHolder;
import com.tieto.pcm.gsso.srv.web.resources.JWKSEndpointMock;

@Controller
public class JWKSEndpointMockImpl implements JWKSEndpointMock {
  
  public Response mockJWKSEndpoint() {    
    return Response.ok(createEntity()).build();
  }

  private static String createEntity() {    
    JWKSet set = new JWKSet(CertificateHolder.getPublicRSAkey());    
    return set.toString();
  }

}
