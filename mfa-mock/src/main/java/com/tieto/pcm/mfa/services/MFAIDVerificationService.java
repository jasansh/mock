package com.tieto.pcm.mfa.services;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tieto.pcm.mfa.services.integration.jms.JmsSubsystem;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.Direction;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.ExternalModule;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageStatus;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageType;
import com.tieto.pcm.mfa.services.integration.jms.helper.JmsIntegrationHelper;
import com.tieto.pcm.mfa.services.integration.jms.model.BankMfaLookupRequest;
import com.tieto.pcm.mfa.services.integration.jms.model.BankMfaLookupResponse;
import com.tieto.pcm.mfa.services.integration.jms.model.BankMfaResultMessage;
import com.tieto.pcm.mfa.services.integration.jms.model.MessageHeaderData;

@RestController
public class MFAIDVerificationService {
  
  private static final String MFA_FAILURE = "FAILURE";

  private static final String SUCCESS = "SUCCESS";

  private static final Logger LOGGER = LoggerFactory.getLogger(MFAIDVerificationService.class);

  
  @Value("${mfa.perform.lookup:false}")
  private boolean performLookup; 
  
  @Autowired
  private Environment env;
  
  @Autowired
  private GenericApplicationContext context;
  
  private JmsSubsystem jmsSubsystem;
  private JmsIntegrationHelper jmsIntegrationHelper;
  
  private ConcurrentHashMap<String, CacheData> cache = new ConcurrentHashMap<>();
  
  private static class CacheData {
    public BankMfaLookupResponse message;
    public MessageHeaderData header;
    public CacheData(BankMfaLookupResponse message, MessageHeaderData header) {
      super();
      this.message = message;
      this.header = header;
    }    
  }
  
  
  @PostConstruct
  private void initialize() {
    jmsIntegrationHelper = new JmsIntegrationHelper(env, context);
    jmsSubsystem = new JmsSubsystem(jmsIntegrationHelper, new ObjectMapper());
    
    // Create JMS Listener for incoming messages
    jmsSubsystem.attachIncomingMessageProcessor(ExternalModule.MFA_FROM_BANK, Direction.INCOMING, String.class, (header, message) -> {
      
      ObjectMapper mapper = new ObjectMapper();
      
      BankMfaLookupResponse response;
      try {
        response = mapper.readValue(message, BankMfaLookupResponse.class);
        
        LOGGER.info(" ---> incoming message = {}", message);
        if (null != response.getErrorData()) {
          LOGGER.info("          ignoring message key = {}", response.getRequestKey());
          LOGGER.info("          ignoring message = {}", message);
        } else {
          cache.put(response.getRequestKey(), new CacheData(response, header));
        }        
        LOGGER.info(" <--- incoming message done key= {}", response.getRequestKey());
        
      } catch (JsonMappingException e) {
        LOGGER.error("Ignoring invalid message received : " + message, e);
      } catch (JsonProcessingException e) {
        LOGGER.error("Ignoring invalid message received : " + message, e);
      }
    });

  }
  
  /**
   * for testing purposes...
   * @return
   */
  public JmsSubsystem getJmsSubsystem() {
    return jmsSubsystem;
  }
  public JmsIntegrationHelper getJmsIntegrationHelper() {
    return jmsIntegrationHelper;
  }
  
  /**
   * Verify MFA.
   *
   * @param mfaid the mfaid, required
   * @param otp the otp, included just in case it will be needed later
   * @param generated the generated, included just in case it will be needed later
   * @param destination the destination, included just in case it will be needed later
   * @return the response entity
   */
  @PostMapping(path = "/mfa/verify", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<Object> verifyMFA(String mfaid, String otp, String generated, String destination) {

    LOGGER.info(" ---> verifyMFA key={}", mfaid);

    if (null == mfaid || "".equals(mfaid) || null == otp || "".equals(otp) || null == generated || "".equals(generated) ) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }    

    if (performLookup) {
      verifyThatMfaIdCanBeFoundFromQueue(mfaid);
    }
    
    if (sendOTPResponseToQueue(mfaid, otp, generated)) {
      LOGGER.info(" <--- verifyMFA key={} Status OK", mfaid);      
      return new ResponseEntity<>("OK", HttpStatus.OK);      
    }
    LOGGER.info(" <--- verifyMFA key={} Status NOT FOUND", mfaid);      
    return new ResponseEntity<>("NOT Found", HttpStatus.NOT_FOUND);      

  }

  private void verifyThatMfaIdCanBeFoundFromQueue(String mfaid) {
   
    BankMfaLookupRequest bankMfaLookupRequest = new BankMfaLookupRequest(mfaid); 
    Long correlationId = Long.valueOf(System.currentTimeMillis());
    LOGGER.info(" ---> verifyThatMfaIdCanBeFoundFromQueue key={}, correlationID={}", mfaid, correlationId);
    
    jmsSubsystem.sendMessage(ExternalModule.MFA_FROM_BANK, Direction.OUTGOING, correlationId, MessageType.MFA_LOOKUP_REQUEST,
        MessageStatus.UNSPECIFIED, bankMfaLookupRequest, ExternalModule.BANK.name());

    LOGGER.info(" <--- verifyThatMfaIdCanBeFoundFromQueue key={}", mfaid);
    
  }

  private boolean sendOTPResponseToQueue(String mfaid, String otp, String generated) {

    LOGGER.info(" ---> sendOTPResponseToQueue key={}", mfaid);
    
    int retryCount = 3;
    while (retryCount > 0) {
      // check if response has arrived
      if (performLookup && null == cache.get(mfaid)) {
        LOGGER.info("          sendOTPResponseToQueue response not found key={}", mfaid);
        retryCount--;
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        CacheData msgData = cache.remove(mfaid);
        String status = otp.equals(generated) ? SUCCESS : MFA_FAILURE;
        String authCode = otp.equals(generated) ? "200" : "401";
        String scaId = generateRandomScaId();
        BankMfaResultMessage bankMfaResultMessage = new BankMfaResultMessage(mfaid, authCode, scaId, status);

        LOGGER.info("          sendOTPResponseToQueue key={}, correlationID={}"
            , mfaid
            , null == msgData ? "1313" : msgData.header.getCorrelationId());

        jmsSubsystem.sendMessage(ExternalModule.MFA_FROM_BANK
            , Direction.OUTGOING
            , null == msgData ? Long.valueOf(1313L) : msgData.header.getCorrelationId()
            , MessageType.MFA_RESULT_MESSAGE
            , MessageStatus.UNSPECIFIED
            , bankMfaResultMessage
            , ExternalModule.BANK.name());
        LOGGER.info(" <--- sendOTPResponseToQueue key={}", mfaid);
        return true;    
      }
    }

    LOGGER.info(" <--- sendOTPResponseToQueue response not found GIVING UP key={}", mfaid);
    return false;
  }

  private String generateRandomScaId() {
    String uuid = UUID.randomUUID().toString();
    return uuid.replace("-", "").toUpperCase();
  }

}
