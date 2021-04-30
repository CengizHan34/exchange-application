package com.example.exchangeapplication.dto;

import com.example.exchangeapplication.enums.CurrencyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author created by cengizhan on 28.04.2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionRequest {
    @NotNull(message = "Source currency cannot be null!")
    @ApiModelProperty(value = "source currency of the currencyConversionRequest", name = "sourceCurrency", dataType = "String", example = "TRY", required = true)
    private CurrencyType sourceCurrency;
    @Positive(message = "Amount sent cannot be below zero!")
    @ApiModelProperty(value = "source amount of the currencyConversionRequest", name = "sourceAmount", dataType = "BigDecimal", example = "100.0", required = true)
    private BigDecimal sourceAmount;
    @NotNull(message = "Target currency cannot be null!")
    @ApiModelProperty(value = "target currency of the currencyConversionRequest", name = "targetCurrency", dataType = "String", example = "USD", required = true)
    private CurrencyType targetCurrency;
}
