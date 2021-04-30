package com.example.exchangeapplication.exceptions;

/**
 * @author created by cengizhan on 27.04.2021
 */
public class InvalidCurrencyException extends RuntimeException {
    public InvalidCurrencyException(String message) {
        super(message);
    }
}
