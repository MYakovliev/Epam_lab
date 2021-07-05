package com.epam.esm.service.exception;



public class ServiceException extends RuntimeException{
    int errorCode;

    public ServiceException(int errorCode){
        super();
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String msg){
        super(msg);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, Throwable throwable){
        super(throwable);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String msg, Throwable throwable){
        super(msg, throwable);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
