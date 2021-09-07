package com.tieto.pcm.gsso.srv.web.resources.saml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.KeyInfoBuilder;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.impl.X509CertificateBuilder;
import org.opensaml.xmlsec.signature.impl.X509DataBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;

public class SAMLAssertionFactory {

  private static final String AUDIENCE_URI = "https://virtual-account-solutions-tieto.com/vam";

  /** The credential. */
  private static BasicX509Credential credential;

  private static Map<String, BasicX509Credential> credentials = new HashMap<>();

  static {
    initialize();
  }

  private SAMLAssertionFactory() {
    super();
  }
  
  private static void initialize() {
    try {
      Security.addProvider(new BouncyCastleProvider());
      InitializationService.initialize();      
      
      // Load default cert
      credential = new BasicX509Credential(getCertificate("x509Cert.txt"));
      JcaPEMKeyConverter kc = new JcaPEMKeyConverter().setProvider("BC");
      // Load default certs private key
      credential.setPrivateKey(kc.getPrivateKey(getPrivateKeyInfo("privateKey.txt")));
      
      // Load Test1 cert
      BasicX509Credential tmpCredential = new BasicX509Credential(getCertificate("Test1PublicCert.txt"));
      // Load default Test1 private key
      tmpCredential.setPrivateKey(kc.getPrivateKey(getPrivateKeyInfo("Test1PrivateKey.txt")));
      KeyInfo tmpKeyInfo = createKeyInfo(tmpCredential);
      credentials.put("Test1", tmpCredential);
      
      // Load Test2 cert
      tmpCredential = new BasicX509Credential(getCertificate("Test2PublicCert.txt"));
      // Load default Test2 private key
      tmpCredential.setPrivateKey(kc.getPrivateKey(getPrivateKeyInfo("Test2PrivateKey.txt")));
      tmpKeyInfo = createKeyInfo(tmpCredential);
      credentials.put("Test2", tmpCredential);
      
      // Load Test3 cert
      tmpCredential = new BasicX509Credential(getCertificate("Test3PublicCert.txt"));
      // Load default Test3 private key
      tmpCredential.setPrivateKey(kc.getPrivateKey(getPrivateKeyInfo("Test3PrivateKey.txt")));
      tmpKeyInfo = createKeyInfo(tmpCredential);
      credentials.put("Test3", tmpCredential);
      
      // Load Test4 cert
      tmpCredential = new BasicX509Credential(getCertificate("Test4PublicCert.txt"));
      // Load default Test3 private key
      tmpCredential.setPrivateKey(kc.getPrivateKey(getPrivateKeyInfo("Test4PrivateKey.txt")));
      tmpKeyInfo = createKeyInfo(tmpCredential);
      credentials.put("Test4", tmpCredential);

    } catch (Exception e) {
      throw new RuntimeException("initialization failed", e);
    }
  }

  
  private static KeyInfo createKeyInfo(BasicX509Credential credential) {
    
    KeyInfoBuilder kiBuilder = new KeyInfoBuilder();
    kiBuilder.buildObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    KeyInfo keyInfo=kiBuilder.buildObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    X509DataBuilder dataBuilder = new X509DataBuilder();
    X509Data data = dataBuilder.buildObject(X509Data.DEFAULT_ELEMENT_NAME);
    X509CertificateBuilder certBuilder = new X509CertificateBuilder();
    org.opensaml.xmlsec.signature.X509Certificate cert =
        certBuilder.buildObject(org.opensaml.xmlsec.signature.X509Certificate.DEFAULT_ELEMENT_NAME);
    String value =
        org.apache.xml.security.utils.Base64.encode(credential.getPublicKey().getEncoded());
    cert.setValue(value);
    data.getX509Certificates().add(cert);
    keyInfo.getX509Datas().add(data);
    return keyInfo;
    
  }
  
  public static Response createSAMLResponse(final String subjectId, final HashMap<String, List<String>> attributes, String issuer, int ttlSeconds) {

    try {

      Signature signature = createSignature(issuer);
      Status status = createStatus();
      Issuer responseIssuer = null;
      Issuer assertionIssuer = null;
      Subject subject = null;
      AttributeStatement attributeStatement = null;

      if (issuer != null) {
        responseIssuer = createIssuer(issuer);
        assertionIssuer = createIssuer(issuer);
      }

      if (subjectId != null) {
        subject = createSubject(subjectId, ttlSeconds);
      }

      if (attributes != null && attributes.size() != 0) {
        Map<String, List<String>> userIdAttr = new HashMap<>();
        attributeStatement = createAttributeStatement(attributes);
      } else {
        HashMap<String, List<String>> userIdAttr = new HashMap<>();
        List<String> test = new ArrayList<>();
        test.add(subjectId);
        userIdAttr.put("UserId", test);
        attributeStatement = createAttributeStatement(userIdAttr);        
      }

      AuthnStatement authnStatement = createAuthnStatement();

      // create conditions
      Conditions conditions = createAudienceCondition(AUDIENCE_URI, ttlSeconds);

      Assertion assertion = createAssertion(new DateTime(), subject, assertionIssuer, authnStatement,
          attributeStatement, conditions);
      assertion.setSignature(signature);
      Response response = createResponse(new DateTime(), responseIssuer, status, assertion);

      ResponseMarshaller marshaller = new ResponseMarshaller();
      Element element = marshaller.marshall(response);

      if (signature != null) {
        Signer.signObject(signature);
      }
      
//      ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      SerializeSupport.writeNode(element, baos);

      return response;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String createSAMLResponse(String userId, String issuer, int ttlSeconds) {
    try {
      Response response = createSAMLResponse(userId, null, issuer, ttlSeconds);
      
      ResponseMarshaller marshaller = new ResponseMarshaller();
      Element element = marshaller.marshall(response);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();    
      SerializeSupport.writeNode(element, baos);
      String responseStr = new String(baos.toByteArray());
      return responseStr;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }          
 }
  
  private static Subject createSubject(final String subjectId, final int ttlSeconds) {
    DateTime currentDate = new DateTime();
    if (ttlSeconds >= 0) {
      currentDate = currentDate.plusSeconds(ttlSeconds);
    } else {
      currentDate = currentDate.minusSeconds(ttlSeconds*-1);
    }

    // create name element
    NameIDBuilder nameIdBuilder = new NameIDBuilder();
    NameID nameId = nameIdBuilder.buildObject();
    nameId.setValue(subjectId);
    nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");

    SubjectConfirmationDataBuilder dataBuilder = new SubjectConfirmationDataBuilder();
    SubjectConfirmationData subjectConfirmationData = dataBuilder.buildObject();
    subjectConfirmationData.setNotOnOrAfter(currentDate);

    SubjectConfirmationBuilder subjectConfirmationBuilder = new SubjectConfirmationBuilder();
    SubjectConfirmation subjectConfirmation = subjectConfirmationBuilder.buildObject();
    subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
    subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

    // create subject element
    SubjectBuilder subjectBuilder = new SubjectBuilder();
    Subject subject = subjectBuilder.buildObject();
    subject.setNameID(nameId);
    subject.getSubjectConfirmations().add(subjectConfirmation);

    return subject;
  }

  private static AttributeStatement createAttributeStatement(HashMap<String, List<String>> attributes) {
    // create authenticationstatement object
    AttributeStatementBuilder attributeStatementBuilder = new AttributeStatementBuilder();
    AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();

    AttributeBuilder attributeBuilder = new AttributeBuilder();
    if (attributes != null) {
      for (Map.Entry<String, List<String>> entry: attributes.entrySet()) {
        Attribute attribute = attributeBuilder.buildObject();
        attribute.setName(entry.getKey());

        for (String value: entry.getValue()) {
          XSStringBuilder stringBuilder = new XSStringBuilder();
          XSString attributeValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
          attributeValue.setValue(value);
          attribute.getAttributeValues().add(attributeValue);
        }

        attributeStatement.getAttributes().add(attribute);
      }
    }
    return attributeStatement;
  }

  private static Conditions createAudienceCondition(String audienceUri, int ttlSeconds) {
    AudienceBuilder audienceBuilder = new AudienceBuilder();
    Audience audience = audienceBuilder.buildObject();
    audience.setAudienceURI(audienceUri);

    AudienceRestrictionBuilder audienceRestrictionBuilder = new AudienceRestrictionBuilder();
    AudienceRestriction audienceRestriction = audienceRestrictionBuilder.buildObject();
    audienceRestriction.getAudiences().add(audience);

    ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
    Conditions conditions = conditionsBuilder.buildObject();
    conditions.getAudienceRestrictions().add(audienceRestriction);

    DateTime dateTime = new DateTime();
    DateTime expiracy = dateTime.plusMinutes(30);
    if (ttlSeconds > 0) {
      expiracy = dateTime.plusSeconds(ttlSeconds);
    } else if (ttlSeconds < 0){
      expiracy = dateTime.minusSeconds(ttlSeconds*-1);
      dateTime = dateTime.minusSeconds((ttlSeconds*-1) + 60);
    } 
    
    conditions.setNotBefore(dateTime);
    conditions.setNotOnOrAfter(expiracy);
    return conditions;
  }

  private static Assertion createAssertion(final DateTime issueDate, Subject subject, Issuer issuer,
      AuthnStatement authnStatement, AttributeStatement attributeStatement, Conditions conditions) {
    AssertionBuilder assertionBuilder = new AssertionBuilder();
    Assertion assertion = assertionBuilder.buildObject();
    assertion.setID(UUID.randomUUID().toString());
    assertion.setIssueInstant(issueDate);
    assertion.setSubject(subject);
    assertion.setConditions(conditions);
    assertion.setIssuer(issuer);

    if (authnStatement != null)
      assertion.getAuthnStatements().add(authnStatement);

    if (attributeStatement != null)
      assertion.getAttributeStatements().add(attributeStatement);

    return assertion;
  }

  private static Response createResponse(final DateTime issueDate, Issuer issuer, Status status, Assertion assertion) {
    ResponseBuilder responseBuilder = new ResponseBuilder();
    Response response = responseBuilder.buildObject();
    response.setID(UUID.randomUUID().toString());
    response.setIssueInstant(issueDate);
    response.setVersion(SAMLVersion.VERSION_20);
    response.setIssuer(issuer);
    response.setStatus(status);
    response.getAssertions().add(assertion);
    return response;
  }

  private static Issuer createIssuer(final String issuerName) {
    // create Issuer object
    IssuerBuilder issuerBuilder = new IssuerBuilder();
    Issuer issuer = issuerBuilder.buildObject();
    issuer.setValue(issuerName);
    return issuer;
  }

  private static AuthnStatement createAuthnStatement() {

    AuthnContextClassRefBuilder classRefBuilder = new AuthnContextClassRefBuilder();
    AuthnContextClassRef classRef = classRefBuilder.buildObject();
    classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

    AuthnContextBuilder authContextBuilder = new AuthnContextBuilder();
    AuthnContext authnContext = authContextBuilder.buildObject();
    authnContext.setAuthnContextClassRef(classRef);

    AuthnStatementBuilder authStatementBuilder = new AuthnStatementBuilder();
    AuthnStatement authnStatement = authStatementBuilder.buildObject();
    authnStatement.setAuthnInstant(new DateTime());
    authnStatement.setAuthnContext(authnContext);

    return authnStatement;
  }

  private static Status createStatus() {
    StatusCodeBuilder statusCodeBuilder = new StatusCodeBuilder();
    StatusCode statusCode = statusCodeBuilder.buildObject();
    statusCode.setValue(StatusCode.SUCCESS);

    StatusBuilder statusBuilder = new StatusBuilder();
    Status status = statusBuilder.buildObject();
    status.setStatusCode(statusCode);

    return status;
  }

  private static Signature createSignature(String issuer) {

    try {
      
      BasicX509Credential publicCert = credentials.get(issuer);
      if (null == publicCert) {
        publicCert = credential;
      }
      
      KeyInfo privateKey = createKeyInfo(publicCert);
      
      XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
      SignatureBuilder signatureBuilder = (SignatureBuilder) builderFactory.getBuilder(Signature.DEFAULT_ELEMENT_NAME);
      Signature signature = signatureBuilder.buildObject();
      
      signature.setSigningCredential(publicCert);
      // get this from configuration, see values from
      // org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256
      signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
      signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
      signature.setKeyInfo(privateKey);

      return signature;

    } catch (Exception e) {
      System.out.println( "createSignature failed = " + e);
      throw new RuntimeException("createSignature failed", e);
    }

  }

  private static X509Certificate getCertificate(String path) {
    try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
      CertificateFactory fact = CertificateFactory.getInstance("X.509");
      return (X509Certificate) fact.generateCertificate(input);
    } catch (Exception e) {
      throw new RuntimeException("getCertificate failed : " + path, e);
    }

  }

  /**
   * Gets the private key info when initialized.
   *
   * @return the private key info
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static PrivateKeyInfo getPrivateKeyInfo(String path) {
    try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
      PEMParser pemParser = new PEMParser(new InputStreamReader(input));
      return (PrivateKeyInfo) pemParser.readObject();
    } catch (Exception e) {
      throw new RuntimeException("getPrivateKeyInfo failed : " + path, e);
    }
  }

}
