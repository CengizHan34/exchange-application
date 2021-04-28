package com.example.exchangeapplication.dto;

import com.example.exchangeapplication.enums.CurrencyType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author created by cengizhan on 28.04.2021
 */
@Data
public class CurrencyConversionRequest {
    @NotNull(message = "Source currency cannot be null!")
    private CurrencyType sourceCurrency;
    @Positive(message = "Amount sent cannot be below zero!")
    private BigDecimal  sourceAmount;
    @NotNull(message = "Target currency cannot be null!")
    private CurrencyType targetCurrency;
}
