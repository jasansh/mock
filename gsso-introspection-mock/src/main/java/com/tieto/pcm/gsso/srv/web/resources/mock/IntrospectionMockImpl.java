package com.tieto.pcm.gsso.srv.web.resources.mock;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Controller;

import com.nimbusds.jwt.JWT;
import com.tieto.pcm.gsso.srv.web.resources.IntrospectionMock;

@Controller
public class IntrospectionMockImpl implements IntrospectionMock {
  
  String CANNED_RESPONSE = "{\r\n" + 
      "\"iss\": \"https://mock.gsso.com\",\r\n" + 
      "\"active\": true,\r\n" + 
      "\"token_type\": \"Bearer\",\r\n" + 
      "\"exp\": 9999999999,\r\n" + 
      "\"client_id\": \"BankSystem\",\r\n" + 
      "\"jti\": \"wsx2m\"\r\n" + 
      ", \"sub\" : \"banksystem\"" +        
      "}";

  public Response introspect(String token) {
    
    
    try {
      JWT jwt = LooseJWTTokenParser.parseToken(token.replace("Bearer ", "")); // just in case
      if (null == jwt) {
        // parsing failed but we're good
        return returnAsResponse(CANNED_RESPONSE);
      }
      
      if (null == jwt.getJWTClaimsSet().getStringClaim("preferred_username") ) {
        return returnAsResponse(CANNED_RESPONSE);
      }
      
      String response = "{\r\n" + 
          "\"iss\": \"https://mock.gsso.com\",\r\n" + 
          "\"active\": true,\r\n" + 
          "\"token_type\": \"Bearer\",\r\n" + 
          "\"exp\": 9999999999,\r\n" + 
          "\"client_id\": \"BankSystem\",\r\n" + 
          "\"jti\": \"wsx2m\"\r\n" + 
          ", \"sub\" : \"" + jwt.getJWTClaimsSet().getStringClaim("preferred_username") + "\"" +        
          "}";
      return returnAsResponse(response);
    } catch (Exception e) {
      // parsing failed but we're good
      return returnAsResponse(CANNED_RESPONSE);  
    }
    
  }
  
  private static Response returnAsResponse(String json) {
    return Response.ok(new GenericEntity<String>(json){}, MediaType.APPLICATION_JSON_TYPE).build();
  }
}
