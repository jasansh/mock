package com.tieto.portalapp.saml;

import java.io.*;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class CertManager {

	private static final String EXT_CONFIG_FILE_NAME = "ext-config.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(CertManager.class);

	/**
	 * gets credential used to sign saml assertionts that are produced. This method
	 * assumes the cert and pkcs formatted primary key are on file system. this data
	 * could be stored elsewhere e.g keystore
	 * 
	 * a credential is used to sign saml response, and includes the private key
	 * as well as a cert for the public key
	 * 
	 * @return
	 * @throws Throwable
	 */
	public Credential getSigningCredential(String publicKeyLocation, String privateKeyLocation) throws Throwable {
		Properties properties = loadExternalConfigProperty();
		String configPathPublicKey = properties.getProperty("path.public.key");
		String pathPublicKey =  configPathPublicKey + File.separator +publicKeyLocation;
		LOGGER.info(String.format("Loading public key from location =%s", pathPublicKey));

		// create public key (cert) portion of credential
		InputStream inStream = new FileInputStream(pathPublicKey);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate publicKey = (X509Certificate)cf.generateCertificate(inStream);
		inStream.close();

		String configPathPrivateKey = properties.getProperty("path.private.key");
		String pathPrivateKey =  configPathPrivateKey + File.separator +privateKeyLocation;
		LOGGER.info(String.format("Loading public key from location =%s", pathPrivateKey));

		// create private key
		RandomAccessFile raf = new RandomAccessFile(pathPrivateKey, "r");
		byte[] buf = new byte[(int)raf.length()];
		raf.readFully(buf);
		raf.close();
		
		PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = kf.generatePrivate(kspec);
		
		// create credential and initialize
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(publicKey);
		credential.setPrivateKey(privateKey);
		
		return credential;
	}

	private Properties loadExternalConfigProperty(){
		Properties defaultProps = null;

		URL propURL = loadURL(EXT_CONFIG_FILE_NAME);
		if (propURL == null) {
			LOGGER.info("External Configuration "+EXT_CONFIG_FILE_NAME+" File not found ");
		}

		InputStream inStream = null;
		try {
			inStream = propURL.openStream();

			defaultProps = new Properties();
			defaultProps.load(inStream);

		} catch (IOException e) {
			LOGGER.info("External Configuration"+ EXT_CONFIG_FILE_NAME +" File read error ", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return defaultProps;
	}

	private URL loadURL(String filename) {
		URL propURL = getClass().getClassLoader().getResource(filename);
		return propURL;
	}
}
