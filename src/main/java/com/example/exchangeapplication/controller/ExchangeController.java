package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
