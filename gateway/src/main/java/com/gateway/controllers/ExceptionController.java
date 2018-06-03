package com.gateway.controllers;

import com.gateway.models.ErrorEntity;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.ServiceUnavailableException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@ControllerAdvice(annotations = {RestController.class})
class ExceptionController {

    private static final Logger log = Logger.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorEntity handleEntityNotFoundException(EntityNotFoundException exc) {
        log.error(HttpStatus.NOT_FOUND.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.NOT_FOUND.value(), exc.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorEntity handleEntityExistsException(EntityExistsException exc) {
        log.error(HttpStatus.CONFLICT.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.CONFLICT.value(), exc.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorEntity handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exc) {
        log.error(HttpStatus.METHOD_NOT_ALLOWED.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.METHOD_NOT_ALLOWED.value(), exc.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class})
    public ErrorEntity handleRequestParamException(MissingServletRequestParameterException exc) {
        log.error(HttpStatus.NOT_FOUND.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), exc.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ErrorEntity handleServiceUnavailableException(ServiceUnavailableException exc) {
        log.error(HttpStatus.SERVICE_UNAVAILABLE.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.SERVICE_UNAVAILABLE.value(), exc.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorEntity handleInternalServerError(Exception exc) {
        log.error(HttpStatus.SERVICE_UNAVAILABLE.toString() + exc.getMessage());
        return new ErrorEntity(HttpStatus.SERVICE_UNAVAILABLE.value(), exc.getMessage());
    }

//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler({NotAuthorizedException.class})
//    public ErrorEntity handleInternalServerError(NotAuthorizedException exc) {
//        log.error(HttpStatus.UNAUTHORIZED.toString() + exc.getMessage());
//        return new ErrorEntity(HttpStatus.UNAUTHORIZED.value(), exc.getMessage());
//    }

}