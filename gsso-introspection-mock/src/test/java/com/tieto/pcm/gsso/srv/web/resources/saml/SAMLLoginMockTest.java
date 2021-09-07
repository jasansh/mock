package com.tieto.pcm.gsso.srv.web.resources.saml;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.Charsets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.opensaml.saml.saml2.core.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.tieto.pcm.gsso.srv.web.resources.SAMLLoginMock;
import com.tieto.pcm.gsso.srv.web.resources.saml.validator.SAMLValidator;

@SpringBootTest
public class SAMLLoginMockTest {

  @ClassRule
  public static final SpringClassRule CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();
  
  private String samlRequst ="nVVdc9o6EH3Pr2DMzH1hjIUxX74xHYLThoYQYyDJzUtH2LJRaktEkjHw6yvbOCUt5XaiB89Iszpnd4989vLTNo4qG8Q4psRSGnWgVBDxqI9JaCmL+We1q3zqX1xyGEf62hwkYkVc9JogLioDzhET8t6QEp7EiM0Q22APLdyxpayEWJuatoFxPeSc1j0aKxcVuWx5FRMocr5DVEQ9GK0oF2YP9ICWkWnYXyuVz5R5KCe1FMESVECMbEt5GNx9a7d6zQbQe6phtOXH1z11uezpquG1m6jT9ANodJXKiDuQc7xBlhLAiJcYnCdoRLiARFiKDnSgAkPVG3PQMVu6aYB6q918LmIdRgX1aHSFSdGXhBGTQo65SWCMuCk8cza4G5t6HZjLIoibN/O5o7rIxwx5ogB6KPusZ32WnSfcLDp7HnN9SEDp5zCFGmZeAjuGOY8CS72U/gl1LrVj1AORz80ZDqVaCUP5yfEqiH3+JmOapvW0Wacs1HQAgAZ6mozxOQ6rMvMSDPkjEtBiP4SEEizFx/v8RdwhsaL+b0yDKKQMi1X8B6qG1gAZlYq2nuo1DFJVtJ+EefYfRgZGWYQaU4aqjEOVr6DeapccLgoQkz/N7x0q18IdWUr1r59sATtnkPCAspj/sv8jzf8V804RRDYoomvkq7zsUVnQeaYPiqGdKMnGoXSDQpq/lkJ2uvpegALmAUYJ6k/vxQZMYrENn7ua+6ptG2DRg7up7boOt8PezXaY1iIHowm18pyOLxdZvul52P/6aN/e1OESXuxWQGf7/dO+eUvB47UxNK6fg3ixnSawZtf4dTidj7+i68jjXUCSgHZu/ZdOvAMe6NjTMX16XqRGkgZTH/5T3fr/Xjy2x2EPX/m77oqM0nCDX3BzaAQj40stmgXj2P4+SONu977pTlw7qT22llfI2PHGy3L2ePXqdDT+xLrpZFxzOjcF4n+1O+c+xHz/Zdr0tduH2cCwflZ3XM37w2PDWZsT6Skj26ER9nZSr4imQ4agQMfmnC1p2zEU590oO8G+GuShpsheBkZEKJWZk9HI1kU4wIidHiXa+8QOIwn5+ayQ80igragMabyGDPPMcWNMcJzEBwc9ctHjG8NIeqSU/0OemoUFyEcsdzITSlxZjzS3fJvKsUBTXrrsKdZDSdq5mjJ9Tszi/sUP";
    
  @Autowired
  private SAMLLoginMock samlLogin;
  
  @Test
  public void testLogin() throws Exception {
    
    Response response = samlLogin.mockSAMLLogin(createHttpHeaders(), samlRequst, "userId", null, "FAKE-ISSUER", 60);
    
    Assert.assertNotNull(response);
    Assert.assertEquals(200, response.getStatus());
    
    MockSAMLResponse body = (MockSAMLResponse)response.getEntity();
    Assert.assertNotNull(body);
    String samlResponse = body.getSAMLResponse(); // SAMLResponse=....
    byte[] xmlBytes = Base64.getDecoder().decode(samlResponse.getBytes(Charsets.UTF_8));
    String xml = new String(xmlBytes, Charsets.UTF_8);
    
    SAMLValidator validator = new SAMLValidator();
    
    org.opensaml.saml.saml2.core.Response samlResp = validator.unmarshallResponse(xml);
    
    Assertion assertion = samlResp.getAssertions().get(0);
    validator.validateSAMLTokenUsingCertificate(assertion);
    
    System.out.println(samlResp);
  
  }
  
  @Test
  public void testLogin2() throws Exception {
    
    Response response = samlLogin.mockSAMLLogin(createHttpHeaders(), samlRequst, "userId", null, "Test1", 60);
   
    Assert.assertNotNull(response);
    Assert.assertEquals(200, response.getStatus());
    
    MockSAMLResponse body = (MockSAMLResponse)response.getEntity();
    Assert.assertNotNull(body);
    String samlResponse = body.getSAMLResponse(); // SAMLResponse=....
    byte[] xmlBytes = Base64.getDecoder().decode(samlResponse.getBytes(Charsets.UTF_8));
    String xml = new String(xmlBytes, Charsets.UTF_8);
    
    SAMLValidator validator = new SAMLValidator();
    
    org.opensaml.saml.saml2.core.Response samlResp = validator.unmarshallResponse(xml);
    
    Assertion assertion = samlResp.getAssertions().get(0);
    validator.validateSAMLTokenUsingCertificate(assertion);
    
    System.out.println(samlResp);
  
  }

  @Test
  public void testLogin3() throws Exception {
    
    Response response = samlLogin.mockSAMLLogin(createHttpHeaders(), samlRequst, "userId", null, "Test1", 60);
   
    Assert.assertNotNull(response);
    Assert.assertEquals(200, response.getStatus());
    
    MockSAMLResponse body = (MockSAMLResponse)response.getEntity();
    Assert.assertNotNull(body);
    String samlResponse = body.getSAMLResponse(); // SAMLResponse=....
    byte[] xmlBytes = Base64.getDecoder().decode(samlResponse.getBytes(Charsets.UTF_8));
    String xml = new String(xmlBytes, Charsets.UTF_8);
    
    SAMLValidator validator = new SAMLValidator();
    
    org.opensaml.saml.saml2.core.Response samlResp = validator.unmarshallResponse(xml);
    
    Assertion assertion = samlResp.getAssertions().get(0);
    validator.validateSAMLTokenUsingCertificate(assertion);
    
    System.out.println(samlResp);
  
  }

  private HttpHeaders createHttpHeaders() {
    return new HttpHeaders() {

      @Override
      public List<String> getRequestHeader(String name) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getHeaderString(String name) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getRequestHeaders() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public List<MediaType> getAcceptableMediaTypes() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public List<Locale> getAcceptableLanguages() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public MediaType getMediaType() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Locale getLanguage() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Map<String, Cookie> getCookies() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Date getDate() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public int getLength() {
        // TODO Auto-generated method stub
        return 0;
      }
      
    };
  }
}
