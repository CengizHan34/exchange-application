package com.example.exchangeapplication.repository;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {
    Page<ExchangeTransaction> findByTransactionId(UUID transactionId, Pageable pageable);

    Page<ExchangeTransaction> findByTransactionDateAfter(LocalDateTime transactionDate, Pageable pageable);

    default Page<ExchangeTransactionDTO> findByTransactionIdDto(String transactionId, Pageable pageable) {
        return findByTransactionId(UUID.fromString(transactionId), pageable).map(ExchangeTransaction::toDTO);
    }

    default Page<ExchangeTransactionDTO> findByTransactionDateAfterDTO(LocalDateTime transactionDate, Pageable pageable) {
        return findByTransactionDateAfter(transactionDate, pageable).map(ExchangeTransaction::toDTO);
    }
}
