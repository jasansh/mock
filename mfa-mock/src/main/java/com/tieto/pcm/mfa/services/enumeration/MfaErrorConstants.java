package com.tieto.pcm.mfa.services.enumeration;

public class MfaErrorConstants {

    private MfaErrorConstants() {}

    private static final int ERROR_CODE_BASE = 10000;

    public static final int MFA_REQUEST_NOT_FOUND = ERROR_CODE_BASE + 100;

    public static final int GENERAL_VALIDATION_ERROR = ERROR_CODE_BASE + 200;
}
