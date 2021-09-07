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

import com.tieto.pcm.mfa.services.integration.jms.JmsSubsystem;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.Direction;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.ExternalModule;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageStatus;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageType;
import com.tieto.pcm.mfa.services.integration.jms.model.BankMfaLookupResponse;
import com.tieto.pcm.mfa.services.integration.jms.model.BankMfaResultMessage;

@SpringBootTest
public class MFAIDVerificationServiceTest {

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
    Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testHappyCase() throws Exception {
    
    JmsSubsystem jmsSubsystem = service.getJmsSubsystem();
        

    BankMfaLookupResponse bankMfaLookupResponse = new BankMfaLookupResponse("testHappyCase", "bankid", "jdoe", "john doe");
    jmsSubsystem.sendMessage(ExternalModule.MFA_FROM_BANK, Direction.INCOMING, Long.valueOf(1L), MessageType.MFA_LOOKUP_RESPONSE, MessageStatus.UNSPECIFIED, bankMfaLookupResponse, "senderId");
    Thread.sleep(1000);
    ResponseEntity<Object> response = service.verifyMFA("testHappyCase", "otp", "otp", "destination");
    jmsSubsystem.attachIncomingMessageProcessor(ExternalModule.MFA_FROM_BANK, Direction.OUTGOING, BankMfaResultMessage.class, ((header, message) -> {
      if ("testHappyCase".equals(message.getMfaId()) && "200".equals(message.getAuthCode()) && "SUCCESS".equals(message.getStatus())) {
        System.out.println("**************** testHappyCase finished ****************");
      }      
    }));

    Thread.sleep(2000);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());    
    
  }

  
  @Test
  public void testBadInput() throws Exception {
    
    
    ResponseEntity<Object> response =  service.verifyMFA(null, "otp", "generated", "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response =  service.verifyMFA("", "otp", "generated", "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response =  service.verifyMFA("bad", null, "generated", "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response =  service.verifyMFA("bad", "", "generated", "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response =  service.verifyMFA("bad", "otp", null, "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response =  service.verifyMFA("bad", "otp", "", "destination");
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
