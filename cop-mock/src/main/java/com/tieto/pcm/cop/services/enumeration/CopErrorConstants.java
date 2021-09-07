package com.tieto.pcm.cop.services.enumeration;

public class CopErrorConstants {

    private CopErrorConstants() {}

    private static final int ERROR_CODE_BASE = 10000;

    public static final int RBS_REQUEST_NOT_FOUND = ERROR_CODE_BASE + 100;

    public static final int GENERAL_VALIDATION_ERROR = ERROR_CODE_BASE + 200;
}
