package com.tieto.pcm.gsso.srv.web.cert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

public class CertificateHolder {

  private static String publicCert;
  private static String privateKey;
  private static RSAKey privateRSAKey;
  private static RSAKey publicRSAkey;
  
  static {
    initialize();
  }
  
  private static void initialize() {
    publicCert = readFromFile("/x509Cert.txt");
    privateKey = readFromFile("/privateKey.txt");
    try {
      privateRSAKey = new RSAKeyGenerator(2048)
          .keyID("123")
          .generate();
      publicRSAkey = privateRSAKey.toPublicJWK();
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static String readFromFile(String filename) {
    StringBuilder resultStringBuilder = new StringBuilder(254);
    try (InputStream input = CertificateHolder.class.getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(input)) ) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
      return resultStringBuilder.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
  }
  
  public static String getPublicCertificate() {
    return publicCert;
  }

  public static String getPrivateKey() {
    return privateKey;
  }

  public static RSAKey getPrivateRSAKey() {
    return privateRSAKey;
  }

  public static RSAKey getPublicRSAkey() {
    return publicRSAkey;
  }
  
  
  
}
