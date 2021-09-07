package com.tieto.pcm.gsso.srv.web.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.tieto.pcm.gsso.srv.web.resources.jwks.JWKSEndpointMockImpl;
import com.tieto.pcm.gsso.srv.web.resources.login.MockOAuthLoginAndRedirectImpl;
import com.tieto.pcm.gsso.srv.web.resources.mock.IntrospectionMockImpl;
import com.tieto.pcm.gsso.srv.web.resources.saml.SAMLLoginMockImpl;

@Component
public class JaxRsConfig extends ResourceConfig {

  public JaxRsConfig() {
    registerEndpoints();
  }

  private void registerEndpoints() {    
    register(IntrospectionMockImpl.class);
    register(JWKSEndpointMockImpl.class);
    register(MockOAuthLoginAndRedirectImpl.class);
    register(SAMLLoginMockImpl.class);
  }
}