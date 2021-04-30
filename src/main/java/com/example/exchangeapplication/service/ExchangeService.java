package com.example.exchangeapplication.service;

import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeService {
    Map<String, BigDecimal> getExchangeRate(String currencyPair);

    CurrencyConversionResponse currencyConversion(CurrencyConversionRequest request);

    Page<ExchangeTransaction> conversionList(int pageNumber, int pageSize, String transactionId, LocalDateTime transactionDate);
}
