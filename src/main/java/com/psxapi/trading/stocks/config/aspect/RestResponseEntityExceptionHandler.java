package com.psxapi.trading.stocks.config.aspect;


import com.psxapi.trading.stocks.helper.StockNotFoundException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorAttributes errorAttributes;

    @ExceptionHandler({ SocketTimeoutException.class })
    public ResponseEntity<Object> handleSocketTimeOut(Exception ex, WebRequest request) {
        Map<String, Object> errorMap = this.errorAttributes.getErrorAttributes(request, false);
        String message = "Connection timeOut";
        errorMap.put("message", message);
        errorMap.put("status", HttpStatus.BAD_GATEWAY.value());
        errorMap.put("error", "Cannot connect to third party");
        return super.handleExceptionInternal(ex, errorMap, new HttpHeaders(), HttpStatus.BAD_GATEWAY, request);
    }


    @ExceptionHandler({ StockNotFoundException.class })
    public ResponseEntity<Object> handleStockNotFound(Exception ex, WebRequest request) {
        Map<String, Object> errorMap = this.errorAttributes.getErrorAttributes(request, false);
        String message = "Stock symbol not found";
        errorMap.put("message", message);
        errorMap.put("status", HttpStatus.NOT_FOUND.value());
        return super.handleExceptionInternal(ex, errorMap, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
