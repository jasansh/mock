package com.tieto.pcm.gsso.srv.web.resources.saml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.Charsets;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

import com.tieto.pcm.gsso.srv.web.resources.SAMLLoginMock;

@Controller
@PropertySource(value = {"classpath:application.properties"})
public class SAMLLoginMockImpl implements SAMLLoginMock {
  
  @Context HttpHeaders headers;
  
  @Value("${redirect.url:}")
  private String applicationUrl;

  @Value("${redirect.saml.issuer:}")
  private String issuer;
  
  @PostConstruct
  public void initOpenSAML3() {
    try {
      InitializationService.initialize();
    } catch (InitializationException e) {
      System.out.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public Response mockSAMLLogin(HttpHeaders headers, String samlRequest, String userId, String relayState, String issuer, int ttlSeconds) {
 
    if (null == userId) {
      userId = "banksup";
    }
    
    try {
      
      // Let's just bypass this if null saml request is passed in
      if (null != samlRequest) {
        String samlXml = decodeAndInflate(samlRequest);
      }
      
      String authenticationResponse = createAuthenticationResponse(userId, issuer, ttlSeconds);      
      String redirectUrl = resolveRedirectUrl(headers);
      
      MockSAMLResponse response = new MockSAMLResponse(
            Base64.getEncoder().encodeToString(authenticationResponse.getBytes(Charsets.UTF_8))
          , relayState);

      return Response.ok(new GenericEntity<MockSAMLResponse>(response){}, MediaType.APPLICATION_JSON_TYPE).build();    
      
    } catch (Exception e) {
      System.out.println(e);
      return Response.serverError().build();
    }

  }  
  
  private static String decodeAndInflate(String samlRequest) throws IOException {
    
    //String urlDecoded = URLDecoder.decode(samlRequest, Charsets.UTF_8.displayName());
    
    byte[] decodedBytes = Base64.getDecoder().decode(samlRequest);
    
    Inflater inflater = new Inflater(true);
    inflater.setInput(decodedBytes);
    
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream, inflater)) {
      inflaterOutputStream.flush();  
      return new String(byteArrayOutputStream.toByteArray(), Charsets.UTF_8);
    } finally {
      // NOP
    }
  }

  private static String createAuthenticationResponse(String userId, String issuer, int ttlSeconds) {
    String asXML = SAMLAssertionFactory.createSAMLResponse(userId, issuer, ttlSeconds);
    return asXML;
  }

  private String resolveRedirectUrl(HttpHeaders headers) {
    return null == headers.getHeaderString("VAM-REDIRECT-URL") ? applicationUrl : headers.getHeaderString("VAM-REDIRECT-URL");
  }


}
