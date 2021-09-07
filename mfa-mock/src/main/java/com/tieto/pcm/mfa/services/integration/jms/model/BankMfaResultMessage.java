package com.tieto.pcm.mfa.services.integration.jms.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tieto.pcm.core.model.AbstractModel;

public class BankMfaResultMessage extends  AbstractModel implements Serializable {

    private static final long serialVersionUID = 6297298652778849498L;

    private String mfaId;

    private String authCode;

    private String scaId;

    private String status;

    @JsonInclude(Include.NON_NULL)
    private ErrorData errorData;

    public BankMfaResultMessage() {}

    public BankMfaResultMessage(String mfaId, String authCode, String scaId, String status) {

        this.mfaId = mfaId;
        this.authCode = authCode;
        this.scaId = scaId;
        this.status = status;
    }

    public BankMfaResultMessage(String mfaId, String authCode, String scaId, String status, ErrorData errorData) {

        this.mfaId = mfaId;
        this.authCode = authCode;
        this.scaId = scaId;
        this.status = status;
        this.errorData = errorData;
    }

    public String getMfaId() {
        return mfaId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public String getScaId() {
        return scaId;
    }

    public String getStatus() {
        return status;
    }
}
