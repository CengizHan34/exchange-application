package com.example.exchangeapplication.service.impl;

import com.example.exchangeapplication.enums.CurrencyType;
import com.example.exchangeapplication.exceptions.InvalidCurrency;
import com.example.exchangeapplication.modal.CurrencyRate;
import com.example.exchangeapplication.repository.ExchangeRepository;
import com.example.exchangeapplication.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
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
    @Value("${rates.api.url}")
    private String ratesApiUrl;
    private final RestTemplate restTemplate;
    private final ExchangeRepository exchangeRepository;

    @Override
    public Map<String,BigDecimal> getExchangeRate(final String currencyPair) {
        AtomicReference<CurrencyType> base = new AtomicReference<>();
        AtomicReference<CurrencyType> symbols = new AtomicReference<>();

        checkCurrencyPair(base, symbols, currencyPair.toUpperCase(Locale.ROOT));
        CurrencyRate currencyRate = getRate(base.get(), symbols.get());

        return Map.of(String.format("%s/%s",base.get(), symbols.get()), currencyRate.getRates().get(symbols.get().toString()));
    }

    private void checkCurrencyPair(AtomicReference<CurrencyType> base, AtomicReference<CurrencyType> symbols, String currencyPair){
        Pattern pattern = Pattern.compile("([A-Z]{3})([A-Z]{3})");
        Matcher matcher = pattern.matcher(currencyPair);
        if (matcher.find() && currencyPair.length() == 6) {
            try {
                base.set(CurrencyType.valueOf(matcher.group(1)));
                symbols.set(CurrencyType.valueOf(matcher.group(2)));
            } catch (Exception e) {
                log.error(String.format("Invalid currency! %s", e.getMessage()));
                throw new InvalidCurrency(String.format("Invalid currency! %s", e.getMessage()));
            }
        } else {
            log.error(String.format("Currency pair not match! %s",currencyPair));
            throw new InvalidCurrency(String.format("Currency pair not match! %s", currencyPair));
        }
    }

    private CurrencyRate getRate(final CurrencyType base, final CurrencyType symbols) {
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
        HttpEntity<CurrencyRate> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<CurrencyRate> result = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, CurrencyRate.class);
        return result.getBody();
    }
}
