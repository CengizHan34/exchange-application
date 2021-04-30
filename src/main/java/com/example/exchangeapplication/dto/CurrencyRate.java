package com.example.exchangeapplication.dto;

import com.example.exchangeapplication.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    private CurrencyType base;
    private Map<CurrencyType, BigDecimal> rates;
    private LocalDate date;
}
