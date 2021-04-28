package com.example.exchangeapplication.entity;

import com.example.exchangeapplication.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Exchange implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal sourceAmount;
    private CurrencyType sourceCurrency;
    private CurrencyType targetCurrency;
    private BigDecimal targetAmount;
    private BigDecimal currencyPrice;
    private UUID transactionId;
    private LocalDateTime transactionDate;
}
