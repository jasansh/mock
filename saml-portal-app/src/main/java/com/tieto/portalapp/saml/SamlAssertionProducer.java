package com.tieto.portalapp.saml;

import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.*;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.signature.*;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SamlAssertionProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SamlAssertionProducer.class);

	private static final String  AUDIENCE_URI = "https://virtual-account-solutions-tieto.com/vam-webapp/engine";

	private String privateKeyLocation;
	private String publicKeyLocation;
	private CertManager certManager = new CertManager();
	
	public Response createSAMLResponse(final String subjectId, final DateTime authenticationTime,
			                           final String credentialType, final HashMap<String, List<String>> attributes, String issuer, Integer samlAssertionDays) {
		LOGGER.info("In createSAMLResponse");
		try {
			DefaultBootstrap.bootstrap();
			
			Signature signature = createSignature();
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
				subject = createSubject(subjectId, samlAssertionDays);
			}
			
			if (attributes != null && attributes.size() != 0) {
				attributeStatement = createAttributeStatement(attributes);
			}
			
			AuthnStatement authnStatement = createAuthnStatement(authenticationTime);

			//create conditions
			Conditions conditions = createAudienceCondition(AUDIENCE_URI);
			
			Assertion assertion = createAssertion(new DateTime(), subject, assertionIssuer, authnStatement, attributeStatement, conditions);
			assertion.setSignature(signature);
			Response response = createResponse(new DateTime(), responseIssuer, status, assertion);
			//response.setSignature(signature);

			ResponseMarshaller marshaller = new ResponseMarshaller();
			Element element = marshaller.marshall(response);
			
			if (signature != null) {
				Signer.signObject(signature);
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLHelper.writeNode(element, baos);
		
			return response;
			
		} catch (Throwable t) {
			LOGGER.info("Error in createSAMLResponse ", t);
			t.printStackTrace();
			return null;
		}
	}

	private Conditions createAudienceCondition(String audienceUri) {
		AudienceBuilder audienceBuilder =  new AudienceBuilder();
		Audience audience = audienceBuilder.buildObject();
		audience.setAudienceURI(audienceUri);

		AudienceRestrictionBuilder audienceRestrictionBuilder = new AudienceRestrictionBuilder();
		AudienceRestriction audienceRestriction = audienceRestrictionBuilder.buildObject();
		audienceRestriction.getAudiences().add(audience);

		ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
		Conditions conditions = conditionsBuilder.buildObject();
		conditions.getAudienceRestrictions().add(audienceRestriction);

		DateTime dateTime = new DateTime();
		conditions.setNotBefore(dateTime);
		conditions.setNotOnOrAfter(dateTime.plusMinutes(10));
		return conditions;
	}

	public String getPrivateKeyLocation() {
		return privateKeyLocation;
	}

	public void setPrivateKeyLocation(String privateKeyLocation) {
		this.privateKeyLocation = privateKeyLocation;
	}

	public String getPublicKeyLocation() {
		return publicKeyLocation;
	}

	public void setPublicKeyLocation(String publicKeyLocation) {
		this.publicKeyLocation = publicKeyLocation;
	}
	
	private Response createResponse(final DateTime issueDate, Issuer issuer, Status status, Assertion assertion) {
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
	
	private Assertion createAssertion(final DateTime issueDate, Subject subject, Issuer issuer, AuthnStatement authnStatement,
									  AttributeStatement attributeStatement, Conditions conditions) {
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
	
	private Issuer createIssuer(final String issuerName) {
		// create Issuer object
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setValue(issuerName);	
		return issuer;
	}
	
	private Subject createSubject(final String subjectId, final Integer samlAssertionDays) {
		DateTime currentDate = new DateTime();
		if (samlAssertionDays != null)
			currentDate = currentDate.plusDays(samlAssertionDays);
		
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
	
	private AuthnStatement createAuthnStatement(final DateTime issueDate) {
		// create authcontextclassref object
		AuthnContextClassRefBuilder classRefBuilder = new AuthnContextClassRefBuilder();
		AuthnContextClassRef classRef = classRefBuilder.buildObject();
		classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
		
		// create authcontext object
		AuthnContextBuilder authContextBuilder = new AuthnContextBuilder();
		AuthnContext authnContext = authContextBuilder.buildObject();
		authnContext.setAuthnContextClassRef(classRef);
		
		// create authenticationstatement object
		AuthnStatementBuilder authStatementBuilder = new AuthnStatementBuilder();
		AuthnStatement authnStatement = authStatementBuilder.buildObject();
		authnStatement.setAuthnInstant(issueDate);
		authnStatement.setAuthnContext(authnContext);
		
		return authnStatement;
	}
	
	private AttributeStatement createAttributeStatement(HashMap<String, List<String>> attributes) {
		// create authenticationstatement object
		AttributeStatementBuilder attributeStatementBuilder = new AttributeStatementBuilder();
		AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();
		
		AttributeBuilder attributeBuilder = new AttributeBuilder();
		if (attributes != null) {
			for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
				Attribute attribute = attributeBuilder.buildObject();
				attribute.setName(entry.getKey());
				
				for (String value : entry.getValue()) {
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

	private Status createStatus() {
		StatusCodeBuilder statusCodeBuilder = new StatusCodeBuilder();
		StatusCode statusCode = statusCodeBuilder.buildObject();
		statusCode.setValue(StatusCode.SUCCESS_URI);

		StatusBuilder statusBuilder = new StatusBuilder();
		Status status = statusBuilder.buildObject();
		status.setStatusCode(statusCode);

		return status;
	}
	
	private Signature createSignature() throws Throwable {
		if (publicKeyLocation != null && privateKeyLocation != null) {
			SignatureBuilder builder = new SignatureBuilder();
			Signature signature = builder.buildObject();
			Credential cred = certManager.getSigningCredential(publicKeyLocation, privateKeyLocation);
			signature.setSigningCredential(cred);
			signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
			signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
			// Modified
			try {
				KeyInfo keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
				X509Data data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
				org.opensaml.xml.signature.X509Certificate cert =
						(org.opensaml.xml.signature.X509Certificate) buildXMLObject(org.opensaml.xml.signature.X509Certificate.DEFAULT_ELEMENT_NAME);
				String value = org.apache.xml.security.utils.Base64.encode(cred.getPublicKey().getEncoded());
				cert.setValue(value);
				data.getX509Certificates().add(cert);
				keyInfo.getX509Datas().add(data);
				signature.setKeyInfo(keyInfo);
				return signature;

			} catch (Exception ex) {
				throw ex;
			}

			//
			//return signature;
		}
		
		return null;
	}

	private static XMLObject buildXMLObject(QName objectQName)  {
		XMLObjectBuilder builder = org.opensaml.xml.Configuration.getBuilderFactory().getBuilder(objectQName);

		return builder.buildObject(objectQName.getNamespaceURI(), objectQName.getLocalPart(),
				objectQName.getPrefix());
	}


}
