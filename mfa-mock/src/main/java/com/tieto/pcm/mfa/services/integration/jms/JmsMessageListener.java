package com.tieto.pcm.mfa.services.integration.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageStatus;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageType;
import com.tieto.pcm.mfa.services.integration.jms.exception.UnprocessableMessageException;
import com.tieto.pcm.mfa.services.integration.jms.model.MessageHeaderData;

public class JmsMessageListener<T> implements MessageListener {

    protected static final Logger logger = LoggerFactory.getLogger(JmsMessageListener.class);

    protected JmsMessageProcessor<T> messageProcessor;

    protected Class<T> messageClass;

    protected ObjectMapper mapper;

    public JmsMessageListener(JmsMessageProcessor<T> messageProcessor, Class<T> messageClass, ObjectMapper mapper) {

        this.messageProcessor = Preconditions.checkNotNull(messageProcessor);
        this.messageClass = Preconditions.checkNotNull(messageClass);
        this.mapper = Preconditions.checkNotNull(mapper);
    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof TextMessage) {

            String messageAsString = null;

            try {

                messageAsString = ((TextMessage) message).getText();
                MessageHeaderData header = extractMessageHeaderData(message);
                T messageObject = null;
                if (messageClass.equals(String.class)) {
                    messageObject = messageClass.cast(StringUtils.strip(messageAsString, "\""));
                } else {
                    messageObject = messageClass.cast(mapper.readValue(messageAsString, messageClass));
                }
                messageProcessor.processMessage(header, messageObject);

            } catch (Exception e) {

                logger.error(String.format("Error while processing message: %s", messageAsString), e);
                throw new UnprocessableMessageException(String.format("Bad message: %s", messageAsString));
            }

        } else {
            logger.error("Received a message of wrong type: {}", message.getClass());
            throw new UnprocessableMessageException(String.format("Wrong message type: %s", message.getClass()));
        }
    }

    protected MessageHeaderData extractMessageHeaderData(Message msg) throws JMSException {

        String type = msg.getStringProperty(MessageHeaderData.HEADER_PARAM_TYPE);
        String status = msg.getStringProperty(MessageHeaderData.HEADER_PARAM_STATUS);
        return new MessageHeaderData(new Long(msg.getJMSCorrelationID()),
                type != null ? MessageType.valueOf(type) : MessageType.UNSPECIFIED,
                status != null ? MessageStatus.valueOf(status) : MessageStatus.UNSPECIFIED,
                msg.getStringProperty(MessageHeaderData.HEADER_PARAM_SENDER_ID));
    }
}
