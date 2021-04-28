package com.example.exchangeapplication.repository;

import com.example.exchangeapplication.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author created by cengizhan on 27.04.2021
 */
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
