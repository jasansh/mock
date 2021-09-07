package com.tieto.pcm.mfa.services.integration.jms.model;

import java.io.Serializable;

import com.tieto.pcm.core.model.AbstractModel;

public class ApprovalMfaResultMessage extends  AbstractModel implements Serializable {

    private static final long serialVersionUID = -7960983245781985149L;

    private String requestKey;
    private String mfaStatus;

    private String bankId;
    private String initiatorUserId;

    public ApprovalMfaResultMessage() {}

    public ApprovalMfaResultMessage(String requestKey, String mfaStatus, String bankId, String initiatorUserId) {

        this.requestKey = requestKey;
        this.mfaStatus = mfaStatus;
        this.bankId = bankId;
        this.initiatorUserId = initiatorUserId;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public String getMfaStatus() {
        return mfaStatus;
    }

    public String getBankId() {
        return bankId;
    }

    public String getInitiatorUserId() {
        return initiatorUserId;
    }
}
