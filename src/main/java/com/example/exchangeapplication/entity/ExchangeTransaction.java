package com.example.exchangeapplication.entity;

import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_transaction_id", columnList = "transactionId"),
        @Index(name = "idx_transaction_date", columnList = "transactionDate")})
public class ExchangeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal convertedCurrencyAmount;
    private BigDecimal rate;
    private UUID transactionId;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime transactionDate;

    private ExchangeTransaction() {
    }

    public ExchangeTransaction(BigDecimal sourceAmount, String sourceCurrency, String targetCurrency,
                               BigDecimal convertedCurrencyAmount, BigDecimal rate) {
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.convertedCurrencyAmount = convertedCurrencyAmount;
        this.rate = rate;
        this.transactionId = UUID.randomUUID();
    }

    public static ExchangeTransaction create(BigDecimal sourceAmount, String sourceCurrency, String targetCurrency,
                                             BigDecimal convertedCurrencyAmount, BigDecimal rate) {
        return new ExchangeTransaction(sourceAmount, sourceCurrency, targetCurrency,
                convertedCurrencyAmount, rate);
    }

    public ExchangeTransactionDTO toDTO() {
        ExchangeTransactionDTO transactionDto = new ExchangeTransactionDTO(this.transactionId,
                this.sourceCurrency, this.targetCurrency, this.rate, this.sourceAmount,
                this.convertedCurrencyAmount, this.transactionDate);
        return transactionDto;
    }
}
