package com.example.exchangeapplication.exceptions.handler;

import com.example.exchangeapplication.exceptions.InvalidCurrency;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author created by cengizhan on 27.04.2021
 */
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {InvalidCurrency.class})
    public ResponseEntity invalidCurrencyExceptionHandler(InvalidCurrency exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
