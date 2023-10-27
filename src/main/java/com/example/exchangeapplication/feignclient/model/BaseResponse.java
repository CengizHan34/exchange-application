package com.example.exchangeapplication.feignclient.model;

import lombok.Getter;

@Getter
public abstract class BaseResponse {
    protected boolean success;
    protected ApiError error;

    protected BaseResponse(boolean success, ApiError error) {
        this.success = success;
        this.error = error;
    }
}
