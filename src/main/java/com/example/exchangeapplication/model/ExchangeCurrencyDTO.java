package com.example.exchangeapplication.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ExchangeCurrencyDTO(UUID transactionID, String currencyPair, BigDecimal convertedCurrencyAmount) {
}
