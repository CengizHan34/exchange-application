package com.example.exchangeapplication.exception;

public enum ErrorCodeEnum {
    ACCESS_RESTRICTED(105, "Access restricted!"),
    INVALID_CURRENCY(202, "Invalid currency!");

    private final int value;
    private final String reasonPhrase;

    ErrorCodeEnum(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }


}
