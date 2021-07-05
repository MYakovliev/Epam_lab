package com.epam.esm.util;


public enum ErrorCode {
    NOT_FOUND_ID(40401),
    NOT_FOUND_NAME(40402);

    private final int code;

    public int getCode() {
        return code;
    }

    ErrorCode(int code){
        this.code = code;
    }
}
