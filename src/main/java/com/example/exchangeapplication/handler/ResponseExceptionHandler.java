package com.example.exchangeapplication.handler;

import com.example.exchangeapplication.exception.AccessRestrictException;
import com.example.exchangeapplication.exception.ErrorCodeEnum;
import com.example.exchangeapplication.exception.ErrorResponse;
import com.example.exchangeapplication.exception.InvalidCurrencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handeRuntimeException(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(value = {InvalidCurrencyException.class})
    protected ResponseEntity<Object> handeInvalidCurrencyException(InvalidCurrencyException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodeEnum.INVALID_CURRENCY.getValue(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }

    @ExceptionHandler(value = {AccessRestrictException.class})
    protected ResponseEntity<Object> handleAccessRestrictedException(AccessRestrictException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodeEnum.ACCESS_RESTRICTED.getValue(), exception.getMessage());
        return ResponseEntity.ok().body(errorResponse);
    }
}
