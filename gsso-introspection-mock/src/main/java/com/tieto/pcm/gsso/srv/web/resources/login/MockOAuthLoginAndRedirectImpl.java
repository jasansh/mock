package com.tieto.pcm.gsso.srv.web.resources.login;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.tieto.pcm.gsso.srv.web.cert.CertificateHolder;
import com.tieto.pcm.gsso.srv.web.resources.MockOAuthLoginAndRedirect;
@PropertySource(value = {"classpath:application.properties"})
@Controller
public class MockOAuthLoginAndRedirectImpl implements MockOAuthLoginAndRedirect {
  
  
  @Value("${redirect.url:}")
  private String applicationUrl;
  
  @Override
  public Response loginAndRedirect(HttpHeaders headers, String userId, String jti, Long ttl)
 {
    
    try {
      
      // 0.
      if (null == userId) {
        userId = "banksup";
      }
      
      // 1. if X-Redirect header is found redirect there otherwise to applicationUrl
      String redirectUrl = resolveRedirectUrl(headers);
      
      // 2. create access token for given userId
      String accessToken = createAccessToken(userId, jti, ttl);
      
      MockOAuthLoginAndRedirectResponse retval = new MockOAuthLoginAndRedirectResponse(accessToken, redirectUrl);
      return Response.ok(new GenericEntity<MockOAuthLoginAndRedirectResponse>(retval){}, MediaType.APPLICATION_JSON_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.serverError().build();   
    }
    
  }

  private String resolveRedirectUrl(HttpHeaders headers) {
    return null == headers.getHeaderString("VAM-REDIRECT-URL") ? applicationUrl : headers.getHeaderString("VAM-REDIRECT-URL");
  }

  private static String createAccessToken(String userId, String jti, Long ttl) {

    try {
      JWSObject jwsObject = new JWSObject(
          new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(CertificateHolder.getPrivateRSAKey().getKeyID()).type(JOSEObjectType.JWT).build(),
          new Payload(createPayload(userId, jti, ttl)));
      
      jwsObject.sign(new RSASSASigner(CertificateHolder.getPrivateRSAKey()));
      
      return jwsObject.serialize();
      
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  private static String createPayload(String userId, String jti, Long ttl) {
    String payload = "{ "
        + " \"sub\" : \"" + userId + "\","
        + " \"preferred_username\" : \"" + userId + "\",";
    if (jti != null && jti.length() > 0) {
      payload = payload + " \"jti\" : \"" + jti +"\",";
    }

    if (null != ttl) {
      long expiracy = System.currentTimeMillis() + ttl.longValue() * 1000;
      payload = payload + " \"exp\" : " + expiracy/1000;
    } else {
      payload = payload + " \"exp\" : " + "9999999999";
    }
    
    payload = payload + " }";
    
    return payload;
  }
  
  

}
