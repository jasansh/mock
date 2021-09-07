package com.tieto.pcm.cop.services.enumeration;

public enum ServiceError {

    COP_REQUEST_NOT_FOUND(CopErrorConstants.RBS_REQUEST_NOT_FOUND, "system.cop.request.not.found"),
    GENERAL_VALIDATION_ERROR(CopErrorConstants.GENERAL_VALIDATION_ERROR, "validation.general.error"),
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
