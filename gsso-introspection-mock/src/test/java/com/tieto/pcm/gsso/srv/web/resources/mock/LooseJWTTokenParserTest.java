package com.tieto.pcm.gsso.srv.web.resources.mock;

import java.text.ParseException;

import org.junit.Test;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

public class LooseJWTTokenParserTest {

  private static String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJEWEp2R1BfTlBXU251TVBCRExJX3AzRnN5a3E4bXhWZ3dBcGNPSjNiT2ZBIn0"
      + ".eyJqdGkiOiI4OTMwYzkxNy1mM2JhLTRiZGMtOTBhOS1jMmUwZTUwZmJiMWMiLCJleHAiOjE1NjE5ODQzNjIsIm5iZiI6MCwiaWF0IjoxNTYxOTg0MDYyLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvRGVtby1SZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI0MDRhM2E2OS0yNDQzLTRlNTktYWU2MS1iNzNlNmViY2I0NTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0dXRvcmlhbC1mcm9udGVuZCIsIm5vbmNlIjoiMjEzOTViZjQtYjEyOC00MGU3LTg0MWQtYzJjNjRhZTY5MDM5IiwiYXV0aF90aW1lIjoxNTYxOTgzMDc5LCJzZXNzaW9uX3N0YXRlIjoiNGE0MjE5Y2QtMzI5OS00YmRmLThkOWUtYmNlMmQ4OThlNTU5IiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm1hbmFnZXIiLCJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJiYW5rc3VwIn0"
      + ".WHa_qwHWrE6y0DfFXLosAIObvVirN-M85K2e6MHznolLiFPIX71XB_IvWNyNJQg7Nfh6m6c5Va3j9rqsUFrjWrGoQhMroazwPxehlI8ssQomKYmSpYxJPmrDGl2rjBJgROmkNyYs4Yc95eUkRFwzeV5HdVfvGJnnMXSt9Te315Yg1dkO6rd4JUN3pAhpq9jPGiVMWEI3hhTushv-WvTi-GXzJ-YpQoTxP12RHFspWI4HL9ukjKSdSxNUAJJZFstm-4ze7IvQlfIDbqimrzGQzw5wsXOduNYlPqeTOxVmwiDksL6O6ICTriKNstChEZsGr0TVxwGP5V372o5nJux4Cw";

  @Test
  public void testParser() {
    JWT jwt = LooseJWTTokenParser.parseToken(TOKEN);
    String subject = null;
    try {
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      subject = claims.getStringClaim("preferred_username");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    System.out.println(subject);
  }
}
