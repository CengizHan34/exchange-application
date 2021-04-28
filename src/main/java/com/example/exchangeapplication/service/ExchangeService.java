package com.example.exchangeapplication.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeService {
    Map<String, BigDecimal> getExchangeRate(String currencyPair);
}
