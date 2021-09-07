package com.tieto.pcm.mfa.services.integration.jms.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tieto.pcm.core.model.AbstractModel;

public class BankMfaLookupResponse extends  AbstractModel implements Serializable {

    private static final long serialVersionUID = -7960983245781985149L;

    private String requestKey;
    private String bankId;
    private String initiatorUserId;
    private String initiatorUserName;

    @JsonInclude(Include.NON_NULL)
    private ErrorData errorData;

    public BankMfaLookupResponse() {}

    public BankMfaLookupResponse(String requestKey, ErrorData errorData) {

        this.requestKey = requestKey;
        this.errorData = errorData;
    }

    public BankMfaLookupResponse(String requestKey, String bankId, String initiatorUserId, String initiatorUserName) {

        this.requestKey = requestKey;
        this.bankId = bankId;
        this.initiatorUserId = initiatorUserId;
        this.initiatorUserName = initiatorUserName;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public String getBankId() {
        return bankId;
    }

    public String getInitiatorUserId() {
        return initiatorUserId;
    }

    public String getInitiatorUserName() {
        return initiatorUserName;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public BankMfaLookupResponse setErrorData(ErrorData errorData) {

        this.errorData = errorData;
        return this;
    }
}
