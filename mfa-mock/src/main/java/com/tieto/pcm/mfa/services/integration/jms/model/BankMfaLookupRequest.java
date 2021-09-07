package com.tieto.pcm.mfa.services.integration.jms.model;

import java.io.Serializable;

import com.tieto.pcm.core.model.AbstractModel;

public class BankMfaLookupRequest extends  AbstractModel implements Serializable {

    private static final long serialVersionUID = 6297298652778849498L;

    private String requestKey;

    public BankMfaLookupRequest() {}

    public BankMfaLookupRequest(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getRequestKey() {
        return requestKey;
    }
}
