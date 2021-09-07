package com.tieto.pcm.gsso.srv.web.resources.saml;

public class MockSAMLResponse {

  private String SAMLResponse;
  private String RelayState;

  public MockSAMLResponse(String sAMLResponse, String relayState) {
    super();
    SAMLResponse = sAMLResponse;
    RelayState = relayState;
  }
  public String getSAMLResponse() {
    return SAMLResponse;
  }
  public void setSAMLResponse(String sAMLResponse) {
    SAMLResponse = sAMLResponse;
  }
  public String getRelayState() {
    return RelayState;
  }
  public void setRelayState(String relayState) {
    RelayState = relayState;
  }
  
  public String toPostMessageBody () {
    
    StringBuilder builder = new StringBuilder(512);
    
    builder.append("SAMLResponse=").append(SAMLResponse);
    if (null != RelayState) {
      builder.append("&RelayState=").append(RelayState);
    }    
    return builder.toString();
  }
  
  @Override
  public String toString() {
    return "MockSAMLResponse [SAMLResponse=" + SAMLResponse + ", RelayState=" + RelayState + "]";
  }
  
}
