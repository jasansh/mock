package com.tieto.pcm.mfa.services.integration.jms.model;

import com.tieto.pcm.core.model.AbstractModel;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageStatus;
import com.tieto.pcm.mfa.services.integration.jms.enumeration.MessageType;

public class MessageHeaderData extends AbstractModel {

    private static final long serialVersionUID = 5880415120855673515L;

    public static final String HEADER_PARAM_TYPE = "type";
    public static final String HEADER_PARAM_STATUS = "status";
    public static final String HEADER_PARAM_SENDER_ID = "senderId";

    private Long correlationId;
    private MessageType type;
    private MessageStatus status;
    private String senderId;

    public MessageHeaderData(Long correlationId, MessageType type,  MessageStatus status, String senderId) {

        this.correlationId = correlationId;
        this.type = type;
        this.status = status;
        this.senderId = senderId;
    }

    public Long getCorrelationId() {
        return correlationId;
    }

    public MessageType getType() {
        return type;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public String getSenderId() {
        return senderId;
    }
}
