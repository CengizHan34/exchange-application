package com.example.exchangeapplication.model;

import lombok.Builder;

@Builder
public record ExchangeRateDTO(String baseSymbol, String targetSymbol, Double rate) {

}
