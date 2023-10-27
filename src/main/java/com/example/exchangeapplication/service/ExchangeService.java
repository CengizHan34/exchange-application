package com.example.exchangeapplication.service;

import com.example.exchangeapplication.model.ExchangeCurrencyDTO;
import com.example.exchangeapplication.model.ExchangeRateDTO;
import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface ExchangeService permits ExchangeServiceImpl {
    ExchangeRateDTO getExchangeRate(String currencyPair);

    ExchangeCurrencyDTO exchangeCurrency(String currencyPair, BigDecimal sourceAmount);

    Page<ExchangeTransactionDTO> getTransactions(int pageNumber, int pageSize, String transactionId, LocalDateTime transactionDate);
}
