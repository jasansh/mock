package com.tieto.pcm.mfa.services.integration.jms.exception;

public class JmsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 7634387856759899973L;

    public JmsRuntimeException(String message) {
        super(message);
    }

    public JmsRuntimeException(String message, Throwable t) {
        super(message, t);
    }
}
