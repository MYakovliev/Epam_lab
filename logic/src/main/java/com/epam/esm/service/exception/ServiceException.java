package com.epam.esm.service.exception;

public class ServiceException extends RuntimeException {
    private int errorCode;

    public ServiceException(int code) {
        this.errorCode = code;
    }

    public ServiceException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
