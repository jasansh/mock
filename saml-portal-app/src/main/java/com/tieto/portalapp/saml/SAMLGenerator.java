package com.tieto.portalapp.saml;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SAMLGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SAMLGenerator.class);

	private String userGUID = null;
	private String keepAliveURLGenerated = null;

	public String generateSAMLResponse(String userId, String samlIssuerId, HttpServletRequest servletRequest, String userType, String keepAliveURL){
		String encodedResponse = null;
		try {
			HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
			//String issuer = "https://virtual-account-solutions-tieto.com";
			String subject = "d383a3f0-57ac-11e6-8a0c-000704020701";
			String privateKey = "privatekey.pkcs8";
			String publicKey = "publickey.pem";
			Integer samlAssertionExpirationDays = 5;
			String keepAliveLastPart = "/portal-app/keepAlive";
			//String keepAliveURL = "https://www.tieto.com/uims/keepalive/session.jsp;jsessionid=0000n-_2uh6556qhtYYptq3Uz85:13744fmem?strict=true";
			String domain = "tieto.com";

			if(userId != null){
				attributes.put("userId", Arrays.asList(userId));
			}

			// customer GUID
			String uuID = UUID.nameUUIDFromBytes(userId.getBytes()).toString(); //UUID.randomUUID().toString();
			LOGGER.info(String.format("UUID = %s for userId = %s", uuID, userId));
			attributes.put("UserGUID", Arrays.asList(uuID));
			this.userGUID = uuID;

			//Keep Alive URL processing
			//String requestURLStr = servletRequest.getRequestURL().toString();
			//URL requestURL = new URL(requestURLStr);
			//String baseURL =  requestURL.getProtocol() + "://"+ requestURL.getHost() + ":"+ requestURL.getPort();
			LOGGER.info(String.format("Keep Alive URL = %s", keepAliveURL));

			//this.keepAliveURLGenerated = baseURL + keepAliveLastPart;
			//attributes.put("KeepAliveURL", Arrays.asList(this.keepAliveURLGenerated));
			//attributes.put("domain", Arrays.asList(domain));




			SamlAssertionProducer producer = new SamlAssertionProducer();
			producer.setPrivateKeyLocation(privateKey);
			producer.setPublicKeyLocation(publicKey);

			Response responseInitial = producer.createSAMLResponse(subject, new DateTime(), "password", attributes, samlIssuerId, samlAssertionExpirationDays);
			//LOGGER.info("Is SAML Signed = "+responseInitial.getAssertions().get(0).isSigned() );
			ResponseMarshaller marshaller = new ResponseMarshaller();
			Element element = marshaller.marshall(responseInitial);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLHelper.writeNode(element, baos);
			String responseStr = new String(baos.toByteArray());
			// Modified code for to write in file

			//writeToFile(responseStr, "token/PlainSAMLToken.txt");
			byte[] SamlResponseBytes = baos.toByteArray();
			encodedResponse = org.opensaml.xml.util.Base64.encodeBytes(SamlResponseBytes, Base64.DONT_BREAK_LINES);
			//writeToFile(encodedResponse, "token/EncodedSAMLToken.txt");
			LOGGER.info(String.format("Encode SAML Response for userId = %s is encodedSAMLToken =  %s", userId, encodedResponse));
		} catch (Throwable t) {
            LOGGER.info("Error in generateSAMLResponse ",t);
			t.printStackTrace();
		}
		return encodedResponse;
	}

	private static void writeToFile(String encodedResponse, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(encodedResponse);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getUserGUID() {
		return userGUID;
	}

	public String getKeepAliveURL() {
		return keepAliveURLGenerated;
	}
}
