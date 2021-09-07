package com.tieto.pcm.gsso.srv.web.resources.saml.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;

public class SAMLValidator {

  // Certificate type
  public static final String CERT_TYPE = "X.509";

  /** The Constant PATH_SEP. */
  public static final String PATH_SEP = "/";

  /** The cert file name. */
  private String certFileName = "x509Cert.txt";

  private BasicCredential credential = null;

  private Map<String, BasicCredential> credentials = new HashMap<>();
  
  public SAMLValidator() {
    super();
    try {
      InitializationService.initialize();
      loadCertificates();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loading certificates into memory for faster performance.
   */
  private void loadCertificates() {
    credential = loadPublicKeyCertificate(certFileName);
    credentials.put("Test1", loadPublicKeyCertificate("Test1PublicCert.txt"));
    credentials.put("Test2", loadPublicKeyCertificate("Test2PublicCert.txt"));
    credentials.put("Test3", loadPublicKeyCertificate("Test3PublicCert.txt"));
  }

  /**
   * Load public key certificate if certFileName is defined with config.
   *
   * @param key
   *          the key
   * @param filename
   *          the filename
   */
  private BasicCredential loadPublicKeyCertificate(String filename) {
    InputStream input = null;
    try {
      CertificateFactory cf = CertificateFactory.getInstance(CERT_TYPE);
      final String path = PATH_SEP + filename;
      input = new ClassPathResource(path).getInputStream();
      Certificate intuitCert = cf.generateCertificate(input);
      PublicKey pk = intuitCert.getPublicKey();
      return new BasicCredential(pk);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      try {
        if (input != null)
          input.close();
      } catch (IOException e) {
        // Do nothing.
      }
    }
  }

  public Response unmarshallResponse(String samlResponse) throws Exception {

    BasicParserPool parser = new BasicParserPool();
    parser.setNamespaceAware(true);
    parser.initialize();

    StringReader reader = new StringReader(samlResponse);

    Document doc = parser.parse(reader);
    Element samlElement = doc.getDocumentElement();
 
    Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(Response.DEFAULT_ELEMENT_NAME);
    return (Response)unmarshaller.unmarshall(samlElement);
}
  
  /**
   * Validate SAML token using certificate(s).
   *
   * @param assertion
   *          the assertion
   * @throws ValidationException
   *           the validation exception
   * @throws MarshallingException
   *           the marshalling exception
   */
  public void validateSAMLTokenUsingCertificate(Assertion assertion) throws SignatureException, ValidationException, MarshallingException {
    
    assertion.getDOM().setIdAttribute("ID", true); // https://blog.samlsecurity.com/2014/05/exception-apache-xmlsec-idresolver.html

    SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
    
    String issuer = assertion.getIssuer().getValue();
    BasicCredential basicCred = credentials.get(issuer);
    if (null == basicCred) {
      basicCred = credential;
    }
    
    profileValidator.validate(assertion.getSignature());
    SignatureValidator.validate(assertion.getSignature(), basicCred);
  }

}
