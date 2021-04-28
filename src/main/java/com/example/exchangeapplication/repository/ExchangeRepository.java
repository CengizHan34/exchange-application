package com.example.exchangeapplication.repository;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.UUID;


/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {
    Page<ExchangeTransaction> findByTransactionId(UUID transactionId, Pageable pageable);

    @Query("from ExchangeTransaction et where et.transactionDate <= :transactionDate")
    Page<ExchangeTransaction> findAllWithTransactionDateTimeBefore(LocalDateTime transactionDate, Pageable pageable);
}
