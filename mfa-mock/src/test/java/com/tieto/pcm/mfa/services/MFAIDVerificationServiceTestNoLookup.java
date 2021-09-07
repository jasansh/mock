package com.tieto.pcm.mfa.services;

import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@SpringBootTest(properties = {"mfa.perform.lookup=false"})
public class MFAIDVerificationServiceTestNoLookup {

  @ClassRule
  public static final SpringClassRule CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private MFAIDVerificationService service;
    
  private static BrokerService broker;
  
  @BeforeClass
  public static void initActiveMQBroker() throws Exception {    
    broker = new BrokerService();
    broker.addConnector("tcp://localhost:61616");
    broker.setPersistent(false);
    broker.start();
  }
    
  @Test
  public void testMFAIDNotFound() {
    ResponseEntity<Object> response = service.verifyMFA("WillNotBeFound", "otp", "otp", "destination");
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}
