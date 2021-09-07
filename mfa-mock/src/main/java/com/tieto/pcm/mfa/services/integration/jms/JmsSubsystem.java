package com.tieto.pcm.mfa.services.integration.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.Direction;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.ExternalModule;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageStatus;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageType;
import com.tieto.pcm.mfa.services.integration.jms.exception.JmsRuntimeException;
import com.tieto.pcm.mfa.services.integration.jms.helper.JmsIntegrationHelper;
import com.tieto.pcm.mfa.services.integration.jms.model.MessageHeaderData;

public class JmsSubsystem {

    protected JmsIntegrationHelper jmsIntegrationHelper;

    protected ObjectMapper mapper;

    public JmsSubsystem(JmsIntegrationHelper jmsIntegrationHelper, ObjectMapper mapper) {
        this.jmsIntegrationHelper = jmsIntegrationHelper;
        this.mapper = mapper;
    }

    public void sendMessage(ExternalModule module, Direction direction, Long correlationId, MessageType messageType, MessageStatus messageStatus,
            Object message, String senderId) {

        Preconditions.checkNotNull(module, "Method parameter 'module' is missing!");
        sendMessage(module.name().toLowerCase(), direction, correlationId, messageType, messageStatus, message, senderId);
    }

    public void sendMessage(String moduleName, Direction direction, Long correlationId, MessageType messageType, MessageStatus messageStatus,
            Object message, String senderId) {

        Preconditions.checkNotNull(moduleName, "Method parameter 'moduleName' is missing!");
        Preconditions.checkNotNull(correlationId, "Method parameter 'correlationId' is missing!");
        Preconditions.checkNotNull(messageType, "Method parameter 'messageType' is missing!");
        Preconditions.checkNotNull(message, "Method parameter 'message' is missing!");
        Preconditions.checkNotNull(senderId, "Method parameter 'senderId' is missing!");
        try {
            JmsTemplate jmsTemplate = jmsIntegrationHelper.getOrCreateJmsTemplate(moduleName, direction.name().toLowerCase());
            jmsTemplate.convertAndSend(mapper.writeValueAsString(message), messagePostProcessor-> {
                messagePostProcessor.setJMSCorrelationID(String.valueOf(correlationId));
                messagePostProcessor.setStringProperty(MessageHeaderData.HEADER_PARAM_TYPE, messageType.name());
                messagePostProcessor.setStringProperty(MessageHeaderData.HEADER_PARAM_STATUS, messageStatus != null ?
                        messageStatus.name() : MessageStatus.UNSPECIFIED.name());
                messagePostProcessor.setStringProperty(MessageHeaderData.HEADER_PARAM_SENDER_ID, senderId);
                return messagePostProcessor;
            });
        } catch (Exception e) {
            throw new JmsRuntimeException(String.format("Error while sending JMS message! (correlationId=%d)", correlationId), e);
        }
    }

    public <T> void attachIncomingMessageProcessor(ExternalModule module, Direction direction, Class<T> messageClass,
            JmsMessageProcessor<T> messageProcessor) {

        Preconditions.checkNotNull(module, "Parameter module is missing!");
        Preconditions.checkNotNull(messageClass, "Parameter messageClass is missing!");
        try {
            DefaultMessageListenerContainer messageListenerContainer = jmsIntegrationHelper.getOrCreateMessageListenerContainer(
                    module.name().toLowerCase(), direction.name().toLowerCase());
            messageListenerContainer.setMessageListener(new JmsMessageListener<T>(messageProcessor, messageClass, mapper));
        } catch (Exception e) {
            throw new JmsRuntimeException("Error while attaching incoming message processor!", e);
        }
    }
}
