package com.example.exchangeapplication.modal;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
@Data
public class CurrencyRate {
    private String base;
    private Map<String, BigDecimal> rates;
    private LocalDate date;
}
