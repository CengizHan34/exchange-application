package com.example.exchangeapplication.service;

import com.example.exchangeapplication.modal.CurrencyConversionRequest;
import com.example.exchangeapplication.modal.CurrencyConversionResponse;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeService {
    Map<String, BigDecimal> getExchangeRate(String currencyPair);
    CurrencyConversionResponse currencyConversion(CurrencyConversionRequest request);
}
