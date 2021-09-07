package com.tieto.pcm.gsso.srv.web.resources.login;

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

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tieto.pcm.gsso.srv.web.cert.CertificateHolder;
import com.tieto.pcm.gsso.srv.web.resources.MockOAuthLoginAndRedirect;

import net.minidev.json.JSONObject;

@SpringBootTest
public class MockLoginAndRedirectTest {

  @ClassRule
  public static final SpringClassRule CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private MockOAuthLoginAndRedirect loginAndRedirect;
  
  @Test
  public void testMockLoginAndRedirect() throws Exception {
    Assert.assertNotNull(loginAndRedirect);
    
    Response response = loginAndRedirect.loginAndRedirect(createHttpHeaders(), "BANKSUP", null, null);
    
    Assert.assertNotNull(response);
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    MockOAuthLoginAndRedirectResponse mockResponse = (MockOAuthLoginAndRedirectResponse)response.getEntity();
    
    Assert.assertNotNull(mockResponse.getToken());
    
    String jws = mockResponse.getToken();    
    JWSObject jwsObject = JWSObject.parse(jws);    
    JWSVerifier verifier = new RSASSAVerifier(CertificateHolder.getPublicRSAkey());
    
    Assert.assertTrue(jwsObject.verify(verifier));
  }

  @Test
  public void testMockLoginAndRedirectWithJTI() throws Exception {
    Assert.assertNotNull(loginAndRedirect);
    
    String jti = "This shold come back";
    
    Response response = loginAndRedirect.loginAndRedirect(createHttpHeaders(), "BANKSUP", jti, null);
    
    Assert.assertNotNull(response);
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    MockOAuthLoginAndRedirectResponse mockResponse = (MockOAuthLoginAndRedirectResponse)response.getEntity();
    
    Assert.assertNotNull(mockResponse.getToken());
    
    String jws = mockResponse.getToken();    
    JWSObject jwsObject = JWSObject.parse(jws);
    JWSVerifier verifier = new RSASSAVerifier(CertificateHolder.getPublicRSAkey());    
    Assert.assertTrue(jwsObject.verify(verifier));
    
    JWTClaimsSet set = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
    String id = set.getJWTID();
    
    Assert.assertEquals(id, jti);
    
  }

  @Test
  public void testMockLoginAndRedirectWithExpiracy() throws Exception {
    Assert.assertNotNull(loginAndRedirect);
    
    long rightNow = System.currentTimeMillis();
    
    Response response = loginAndRedirect.loginAndRedirect(createHttpHeaders(), "BANKSUP", null, Long.valueOf(60L));
    
    Assert.assertNotNull(response);
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    MockOAuthLoginAndRedirectResponse mockResponse = (MockOAuthLoginAndRedirectResponse)response.getEntity();
    
    Assert.assertNotNull(mockResponse.getToken());
    
    String jws = mockResponse.getToken();    
    JWSObject jwsObject = JWSObject.parse(jws);
    JWTClaimsSet set = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
    
    Date expiracy = set.getExpirationTime();
    
    long duration = expiracy.getTime() - rightNow;
    
    Assert.assertTrue(duration > 0);
    
    JWSVerifier verifier = new RSASSAVerifier(CertificateHolder.getPublicRSAkey());
    
    Assert.assertTrue(jwsObject.verify(verifier));
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
