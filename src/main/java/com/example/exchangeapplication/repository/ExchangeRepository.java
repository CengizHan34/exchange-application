package com.example.exchangeapplication.repository;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {
}
