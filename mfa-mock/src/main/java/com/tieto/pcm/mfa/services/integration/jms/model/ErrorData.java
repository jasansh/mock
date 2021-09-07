package com.tieto.pcm.mfa.services.integration.jms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tieto.pcm.core.model.AbstractModel;
import com.tieto.pcm.mfa.services.enumeration.ServiceError;

public class ErrorData extends AbstractModel {

    private static final long serialVersionUID = -5392635185778572435L;

    private int code;
    private String systemMessage;

    @JsonInclude(Include.NON_NULL)
    private String userMessage;

    public ErrorData() {}

    public ErrorData(ServiceError serviceError) {

        this.code = serviceError.getCode();
        this.systemMessage = serviceError.name();
        this.userMessage = serviceError.getMessage();
    }

    public ErrorData(int code, String systemMessage, String userMessage) {

        this.code = code;
        this.systemMessage = systemMessage;
        this.userMessage = userMessage;
    }

    public int getCode() {
        return code;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public ErrorData setUserMessage(String userMessage) {

        this.userMessage = userMessage;
        return this;
    }
}
