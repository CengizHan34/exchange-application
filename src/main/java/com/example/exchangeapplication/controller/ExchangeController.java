package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/rate/{currencyPair}")
    public ResponseEntity<Map> exchangeRate(@PathVariable("currencyPair") final String currencyPair) {
        return ResponseEntity.ok().body(exchangeService.getExchangeRate(currencyPair));
    }

    @PostMapping("/conversion")
    public ResponseEntity<CurrencyConversionResponse> conversion(@Valid @RequestBody final CurrencyConversionRequest request) {
        return ResponseEntity.ok().body(exchangeService.currencyConversion(request));
    }

    @GetMapping("/conversion-list")
    public ResponseEntity<Page<ExchangeTransaction>> conversionList(@RequestParam(value = "pageNumber") int pageNumber,
                                                                    @RequestParam("pageSize") int pageSize,
                                                                    @RequestParam(name = "transactionId", required = false) String transactionId,
                                                                    @RequestParam(name = "transactionDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime transactionDate) {

        return ResponseEntity.ok().body(exchangeService.conversionList(pageNumber, pageSize, transactionId, transactionDate));
    }
}
