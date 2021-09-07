package com.tieto.pcm.gsso.srv.web.resources.jwks;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.tieto.pcm.gsso.srv.web.resources.MockOAuthLoginAndRedirect;
import com.tieto.pcm.gsso.srv.web.resources.login.MockOAuthLoginAndRedirectResponse;

@SpringBootTest
public class JWKSEndpointMockTest {

  @ClassRule
  public static final SpringClassRule CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private JWKSEndpointMockImpl jwks;  
  
  @Autowired
  private MockOAuthLoginAndRedirect loginAndRedirect;

  
  @Test
  public void testLoginAndJWKS() throws Exception {

    Response response = loginAndRedirect.loginAndRedirect(createHttpHeaders(), "BANKSUP", null, null);
    
    Assert.assertNotNull(response);
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    MockOAuthLoginAndRedirectResponse mockResponse = (MockOAuthLoginAndRedirectResponse)response.getEntity();
    
    String authHeader = mockResponse.getToken();
    Assert.assertNotNull(authHeader);
    
    JWKSet jwksSet = JWKSet.parse(testJWKSEndpointMock());     
    SignedJWT jws = (SignedJWT)JWTParser.parse(authHeader);
    
    JWSVerifier verifier = findJWSVerifier(jws.getHeader(), jwksSet.getKeyByKeyId("123"));
    
    Assert.assertTrue(jws.verify(verifier));
  }
  
  public String testJWKSEndpointMock() throws Exception {
    Assert.assertNotNull(jwks);
    
    Response response = jwks.mockJWKSEndpoint();
    
    Assert.assertNotNull(response);
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    
    String jwkset = (String)response.getEntity();
    
    Assert.assertNotNull(jwkset);
    return jwkset;
  }
 
  private static JWSVerifier findJWSVerifier(JWSHeader header, JWK key) throws JOSEException {
    PublicKey publicKey = null;
    if (key instanceof RSAKey) {
      publicKey = ((RSAKey)key).toPublicKey();
    } else if (key instanceof ECKey){
      publicKey = ((ECKey)key).toPublicKey();
    } else {
      throw new RuntimeException("unknown JWK key type " + key.toJSONObject().toJSONString());
    }   
    return new DefaultJWSVerifierFactory().createJWSVerifier(header, publicKey);
  }
  
  private HttpHeaders createHttpHeaders() {
    return new HttpHeaders() {

      @Override
      public List<String> getRequestHeader(String name) {
        return null;
      }

      @Override
      public String getHeaderString(String name) {
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getRequestHeaders() {

        return null;
      }

      @Override
      public List<MediaType> getAcceptableMediaTypes() {

        return null;
      }

      @Override
      public List<Locale> getAcceptableLanguages() {

        return null;
      }

      @Override
      public MediaType getMediaType() {

        return null;
      }

      @Override
      public Locale getLanguage() {

        return null;
      }

      @Override
      public Map<String, Cookie> getCookies() {

        return null;
      }

      @Override
      public Date getDate() {

        return null;
      }

      @Override
      public int getLength() {

        return 0;
      }
      
    };
  }  
}
