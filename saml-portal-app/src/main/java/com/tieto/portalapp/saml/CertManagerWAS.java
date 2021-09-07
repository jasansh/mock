package com.tieto.portalapp.saml;

import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

public class CertManagerWAS {

	public static final String PATH_SEP = "/";
	
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
		// create public key (cert) portion of credential
		final String publicKeyPath = PATH_SEP + publicKeyLocation;
		InputStream inStreamPubk = null;
		InputStream inStreamPriK = null;

		inStreamPubk = new ClasspathResource(publicKeyPath).getInputStream(); //new FileInputStream(publicKeyLocation);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate publicKey = (X509Certificate)cf.generateCertificate(inStreamPubk);
		inStreamPubk.close();
		    
		// create private key
		final String privateKeyPath = PATH_SEP + privateKeyLocation;

        /*URL url = this.getClass().getResource(privateKeyPath);
        File file = new File(url.toURI());*/

        File file = new File(getClass().getClassLoader().getResource(privateKeyLocation).getFile());

		RandomAccessFile raf = new RandomAccessFile(file, "r");
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
}
