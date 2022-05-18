package com.example.exchangeapplication.service.impl;

import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.dto.CurrencyRate;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.enums.CurrencyType;
import com.example.exchangeapplication.exceptions.InvalidCurrencyException;
import com.example.exchangeapplication.repository.ExchangeRepository;
import com.example.exchangeapplication.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author created by cengizhan on 27.04.2021
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private final RestTemplate restTemplate;
    private final ExchangeRepository exchangeRepository;
    @Value("${rates.api.url}")
    private String ratesApiUrl;

    @Override
    public Map<String, BigDecimal> getExchangeRate(final String currencyPair) {
        AtomicReference<CurrencyType> base = new AtomicReference<>();
        AtomicReference<CurrencyType> symbols = new AtomicReference<>();

        checkCurrencyPair(base, symbols, currencyPair.toUpperCase(Locale.ROOT));
        CurrencyRate currencyRate = getCurrencyRate(base.get(), symbols.get());

        return Map.of(String.format("%s/%s", base.get(), symbols.get()), currencyRate.getRates().get(symbols.get()));
    }

    @Override
    public CurrencyConversionResponse currencyConversion(final CurrencyConversionRequest request) {
        CurrencyRate currencyRate = getCurrencyRate(request.getSourceCurrency(), request.getTargetCurrency());
        BigDecimal targetAmount = currencyRate.getRates().get(request.getTargetCurrency()).multiply(request.getSourceAmount());

        ExchangeTransaction exchangeTransaction = ExchangeTransaction.builder()
                .transactionId(UUID.randomUUID()).transactionDate(LocalDateTime.now())
                .sourceCurrency(request.getSourceCurrency()).targetCurrency(request.getTargetCurrency())
                .currencyPrice(currencyRate.getRates().get(request.getTargetCurrency()))
                .sourceAmount(request.getSourceAmount()).targetAmount(targetAmount)
                .build();

        ExchangeTransaction exchangeTransactionSaved = exchangeRepository.save(exchangeTransaction);
        log.info(String.format("Transaction successfully saved. transactionId: %s", exchangeTransaction.getTransactionId().toString()));

        Map currencyAmount = Map.of(exchangeTransactionSaved.getTargetCurrency(), exchangeTransactionSaved.getTargetAmount());
        return CurrencyConversionResponse.builder().transactionId(exchangeTransactionSaved.getTransactionId())
                .currencyAmount(currencyAmount).build();
    }

    @Override
    public Page<ExchangeTransaction> conversionList(int pageNumber, int pageSize, String transactionId, LocalDateTime transactionDate) {
        pageSize = pageSize == 0 ? DEFAULT_PAGE_SIZE : pageSize;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionDate").descending());
        if (!ObjectUtils.isEmpty(transactionId)) {
            return exchangeRepository.findByTransactionId(UUID.fromString(transactionId), pageable);
        }
        if (!ObjectUtils.isEmpty(transactionDate)) {
            return exchangeRepository.findAllWithTransactionDateTimeBefore(transactionDate, pageable);
        }
        return exchangeRepository.findAll(pageable);
    }

    private void checkCurrencyPair(AtomicReference<CurrencyType> base, AtomicReference<CurrencyType> symbols, String currencyPair) {
        Pattern pattern = Pattern.compile("([A-Z]{3})([A-Z]{3})");
        Matcher matcher = pattern.matcher(currencyPair);
        if (matcher.find() && currencyPair.length() == 6) {
            try {
                base.set(CurrencyType.valueOf(matcher.group(1)));
                symbols.set(CurrencyType.valueOf(matcher.group(2)));
            } catch (Exception e) {
                log.error(String.format("Invalid currency! %s", e.getMessage()));
                throw new InvalidCurrencyException(String.format("Invalid currency! %s", e.getMessage()));
            }
        } else {
            log.error(String.format("Currency pair not match! %s", currencyPair));
            throw new InvalidCurrencyException(String.format("Currency pair not match! %s", currencyPair));
        }
    }

    private CurrencyRate getCurrencyRate(final CurrencyType base, final CurrencyType symbols) {
        StringBuilder currencyPairBuilder = new StringBuilder(ratesApiUrl)
                .append("?base=").append(base).append("&").append("symbols=").append(symbols);
        URI uri = null;
        try {
            uri = new URI(currencyPairBuilder.toString());
        } catch (URISyntaxException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", "P2PB22n2ROMl7Azw2zOEXmXvuS0XwX5C");

        HttpEntity<CurrencyRate> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<CurrencyRate> result = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, CurrencyRate.class);
        log.info(String.format("Rates were successfully pulled from rates Api. base:%s symbols:%s", base, symbols));
        return result.getBody();
    }
}
