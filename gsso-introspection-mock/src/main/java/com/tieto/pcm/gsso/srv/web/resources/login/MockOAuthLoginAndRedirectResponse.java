package com.tieto.pcm.gsso.srv.web.resources.login;

import java.io.Serializable;

/**
 * The Class MockOAuthLoginAndRedirectResponse.
 */
public class MockOAuthLoginAndRedirectResponse implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The token. */
  private String token;
  
  /** The redirect url. */
  private String redirectUrl;
  
  /**
   * Instantiates a new mock OAuth login and redirect response.
   *
   * @param token the token
   * @param redirectUrl the redirect url
   */
  public MockOAuthLoginAndRedirectResponse(String token, String redirectUrl) {
    super();
    this.token = token;
    this.redirectUrl = redirectUrl;
  }
  
  /**
   * Gets the token.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }
  
  /**
   * Sets the token.
   *
   * @param token the new token
   */
  public void setToken(String token) {
    this.token = token;
  }
  
  /**
   * Gets the redirect url.
   *
   * @return the redirect url
   */
  public String getRedirectUrl() {
    return redirectUrl;
  }
  
  /**
   * Sets the redirect url.
   *
   * @param redirectUrl the new redirect url
   */
  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }
  
  

}
