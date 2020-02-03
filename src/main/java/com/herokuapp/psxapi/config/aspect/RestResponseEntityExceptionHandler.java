package com.herokuapp.psxapi.config.aspect;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;
import java.util.Map;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ErrorAttributes errorAttributes;

    @ExceptionHandler({ SocketTimeoutException.class })
    public ResponseEntity<Object> handleSocketTimeOut(Exception ex, WebRequest request) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(request, false);
        String message = "Connection timeOut";
        errorAttributes.put("message", message);
        errorAttributes.put("status", HttpStatus.BAD_GATEWAY.value());
        errorAttributes.put("error", "Cannot connect to third party");
        return super.handleExceptionInternal(ex, errorAttributes, new HttpHeaders(), HttpStatus.BAD_GATEWAY, request);
    }
}
