package com.example.exchangeapplication;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.enums.CurrencyType;
import com.example.exchangeapplication.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class ExchangeApplication implements CommandLineRunner {

    @Autowired
    private ExchangeRepository exchangeRepository;

    public static void main(String[] args) {
        SpringApplication.run(ExchangeApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        ExchangeTransaction exchangeTransaction = ExchangeTransaction.builder()
                .transactionId(UUID.fromString("831fd769-0f96-4512-8e54-bd4975c54c63"))
                .transactionDate(LocalDateTime.now()).sourceAmount(new BigDecimal("100"))
                .targetAmount(new BigDecimal("800")).sourceCurrency(CurrencyType.USD)
                .targetCurrency(CurrencyType.TRY).currencyPrice(new BigDecimal("8"))
                .build();

        exchangeRepository.save(exchangeTransaction);
    }
}
