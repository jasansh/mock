package com.tieto.pcm.gsso.srv.web.resources.saml;

import org.junit.Assert;
import org.junit.Test;

public class SAMLAssertionFactoryTest {

   @Test
   public void testSAMLAssertionFactory() throws Exception {
     String response = SAMLAssertionFactory.createSAMLResponse("UserID", null, 2592000);
     Assert.assertNotNull(response);
   }

   @Test
   public void testSAMLAssertionFactorywithIssuer() throws Exception {
     String response = SAMLAssertionFactory.createSAMLResponse("UserID", "Test4", 31536000);
     Assert.assertTrue(response.indexOf("Test4") > 0);
   }
}
