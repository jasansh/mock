package com.tieto.pcm.mfa.services.integration.jms.exception;

public class UnprocessableMessageException extends RuntimeException {

    private static final long serialVersionUID = 5017939585649131569L;

    public UnprocessableMessageException(String error) {
        super(error);
    }
}
