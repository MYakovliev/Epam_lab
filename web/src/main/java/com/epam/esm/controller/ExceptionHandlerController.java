package com.epam.esm.controller;

import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(ServiceException exception){
        return new ErrorMessage(exception.getErrorCode(), exception.getMessage());
    }
}
