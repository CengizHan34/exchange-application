package com.example.exchangeapplication.dto;

import com.example.exchangeapplication.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * @author created by cengizhan on 28.04.2021
 */
@Data
@Builder
public class CurrencyConversionResponse {
    private UUID transactionId;
    private Map<CurrencyType, BigDecimal> currencyAmount;
}
