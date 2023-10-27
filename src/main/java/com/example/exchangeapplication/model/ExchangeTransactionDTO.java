package com.example.exchangeapplication.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExchangeTransactionDTO(UUID transactionId, String sourceCurrency, String targetCurrency,
                                     BigDecimal rate, BigDecimal sourceAmount, BigDecimal convertedCurrencyAmount,
                                     LocalDateTime transactionDate) {
}
