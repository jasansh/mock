package com.tieto.pcm.mfa.services.integration.jms.exception;

public class InvalidMessageTypeException extends RuntimeException {

    private static final long serialVersionUID = 6919627431293990487L;

    public InvalidMessageTypeException(String error) {
        super(error);
    }
}
