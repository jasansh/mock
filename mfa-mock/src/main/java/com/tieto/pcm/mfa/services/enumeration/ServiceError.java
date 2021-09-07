package com.tieto.pcm.mfa.services.enumeration;

public enum ServiceError {

    MFA_REQUEST_NOT_FOUND(MfaErrorConstants.MFA_REQUEST_NOT_FOUND, "system.mfa.request.not.found"),
    GENERAL_VALIDATION_ERROR(MfaErrorConstants.GENERAL_VALIDATION_ERROR, "validation.general.error"),
    ;

    private int code;

    private String message;

    private ServiceError(int code, String message) {

        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
