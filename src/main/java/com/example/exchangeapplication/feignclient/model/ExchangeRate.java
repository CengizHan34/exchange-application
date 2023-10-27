package com.example.exchangeapplication.feignclient.model;


import lombok.Getter;

import java.util.Map;

@Getter
public class ExchangeRate extends BaseResponse {
    private Long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;

    public ExchangeRate(boolean success, ApiError error, Long timestamp, String base, String date, Map<String, Double> rates) {
        super(success, error);
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public static ExchangeRate create(boolean success, ApiError error) {
        return new ExchangeRate(success, error, null, null, null, null);
    }

    public static ExchangeRate create(boolean success, ApiError error, Long timestamp, String base,
                                      String date, Map<String, Double> rates) {
        return new ExchangeRate(success, error, timestamp, base, date, rates);
    }


}


