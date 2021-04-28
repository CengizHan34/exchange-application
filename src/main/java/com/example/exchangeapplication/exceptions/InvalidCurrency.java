package com.example.exchangeapplication.exceptions;

/**
 * @author created by cengizhan on 27.04.2021
 */
public class InvalidCurrency extends RuntimeException{
    public InvalidCurrency(String message){
        super(message);
    }
}
