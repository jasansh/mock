package com.tieto.pcm.gsso.srv.web.resources.mock;

import java.text.ParseException;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

public class LooseJWTTokenParser {

  
  public static final JWT parseToken (String token) {
    try {
      return JWTParser.parse(token);
    } catch (ParseException e) {
      return null;
    }
    
  }
}
