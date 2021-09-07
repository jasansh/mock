package com.tieto.pcm.gsso.srv.web.cert;

import org.junit.Assert;
import org.junit.Test;

public class CertificateHolderTest {

  @Test
  public void testCertificateHolder() {
    Assert.assertNotNull(CertificateHolder.getPrivateKey());
    Assert.assertNotNull(CertificateHolder.getPublicCertificate());
    Assert.assertNotNull(CertificateHolder.getPrivateRSAKey());
    Assert.assertNotNull(CertificateHolder.getPublicRSAkey());
  }
}
