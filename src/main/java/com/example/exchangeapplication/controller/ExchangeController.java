package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.modal.CurrencyConversionRequest;
import com.example.exchangeapplication.modal.CurrencyConversionResponse;
import com.example.exchangeapplication.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public ResponseEntity<Map> exchangeRate(@PathVariable("currencyPair") final String currencyPair){
        return ResponseEntity.ok().body(exchangeService.getExchangeRate(currencyPair));
    }

    @PostMapping("/conversion")
    public ResponseEntity<CurrencyConversionResponse> conversion(@Valid @RequestBody final CurrencyConversionRequest request){
        return ResponseEntity.ok().body(exchangeService.currencyConversion(request));
    }
}
