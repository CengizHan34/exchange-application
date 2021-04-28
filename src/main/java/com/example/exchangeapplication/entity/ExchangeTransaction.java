package com.example.exchangeapplication.entity;

import com.example.exchangeapplication.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author created by cengizhan on 28.04.2021
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal sourceAmount;
    @Enumerated(EnumType.STRING)
    private CurrencyType sourceCurrency;
    @Enumerated(EnumType.STRING)
    private CurrencyType targetCurrency;
    private BigDecimal targetAmount;
    private BigDecimal currencyPrice;
    private UUID transactionId;
    private LocalDateTime transactionDate;
}
