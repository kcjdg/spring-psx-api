package com.herokuapp.psxapi.config.aspect;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ SocketTimeoutException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>("Cannot connect to server", new HttpHeaders(), HttpStatus.BAD_GATEWAY);
    }
}
