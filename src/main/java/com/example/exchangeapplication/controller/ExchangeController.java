package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.model.ExchangeCurrencyDTO;
import com.example.exchangeapplication.model.ExchangeRateDTO;
import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import com.example.exchangeapplication.service.ExchangeService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@Validated
public class ExchangeController {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateDTO> getRate(@RequestParam("currency-pair") @NotBlank String currencyPair) {
        return ResponseEntity.ok().body(exchangeService.getExchangeRate(currencyPair));
    }

    @PostMapping("/exchange")
    public ResponseEntity<ExchangeCurrencyDTO> exchangeCurrency(@RequestParam("currency-pair") @NotBlank String currencyPair,
                                                                @RequestParam("source-amount") BigDecimal sourceAmount) {
        return ResponseEntity.ok().body(exchangeService.exchangeCurrency(currencyPair, sourceAmount));
    }

    @GetMapping("/conversion-list")
    public ResponseEntity<Page<ExchangeTransactionDTO>> getTransactions(
            @RequestParam(value = "page-number", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "page-size", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @RequestParam(value = "transaction-date", required = false) LocalDateTime transactionDate) {
        return ResponseEntity.ok().body(exchangeService.getTransactions(pageNumber, pageSize, transactionId, transactionDate));
    }
}
