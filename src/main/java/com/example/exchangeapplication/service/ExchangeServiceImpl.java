package com.example.exchangeapplication.service;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.exception.AccessRestrictException;
import com.example.exchangeapplication.exception.ErrorCodeEnum;
import com.example.exchangeapplication.exception.InvalidCurrencyException;
import com.example.exchangeapplication.feignclient.FixerRateApi;
import com.example.exchangeapplication.feignclient.model.ExchangeRate;
import com.example.exchangeapplication.model.ExchangeCurrencyDTO;
import com.example.exchangeapplication.model.ExchangeRateDTO;
import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import com.example.exchangeapplication.repository.ExchangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public final class ExchangeServiceImpl implements ExchangeService {
    @Value("${access.key}")
    private String accessKey;
    final String regexPattern = "(^[A-Z]{3})/([A-Z]{3})$";
    private FixerRateApi fixerRateApi;

    private final ExchangeRepository exchangeRepository;

    public ExchangeServiceImpl(FixerRateApi fixerRateApi, ExchangeRepository exchangeRepository) {
        this.fixerRateApi = fixerRateApi;
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public ExchangeRateDTO getExchangeRate(String currencyPair) {
        AtomicReference<String> baseSymbol = new AtomicReference<>();
        AtomicReference<String> targetSymbol = new AtomicReference<>();
        ExchangeRate exchangeRate = getCurrencyRateFromForeignService(currencyPair, baseSymbol, targetSymbol);

        if (exchangeRate.isSuccess()) {
            ExchangeRateDTO exchangeRateDTO = ExchangeRateDTO.builder()
                    .baseSymbol(baseSymbol.get())
                    .targetSymbol(targetSymbol.get())
                    .rate(exchangeRate.getRates().get(targetSymbol.get()))
                    .build();
            return exchangeRateDTO;
        }

        return (ExchangeRateDTO) throwException(exchangeRate.getError().getCode(), exchangeRate.getError().getInfo());
    }

    private Object throwException(int code, String message) {
        switch (code) {
            case 202 -> {
                log.info(message);
                throw new InvalidCurrencyException(message);
            }
            case 105 -> {
                log.info(message);
                throw new AccessRestrictException(message);
            }
        }
        throw new RuntimeException(message);
    }

    @Override
    public ExchangeCurrencyDTO exchangeCurrency(String currencyPair, BigDecimal sourceAmount) {
        AtomicReference<String> baseSymbol = new AtomicReference<>();
        AtomicReference<String> targetSymbol = new AtomicReference<>();
        ExchangeRate exchangeRate = getCurrencyRateFromForeignService(currencyPair, baseSymbol, targetSymbol);

        if (exchangeRate.isSuccess()) {
            var rate = exchangeRate.getRates().get(targetSymbol.get());
            var convertedCurrencyAmount = sourceAmount.multiply(BigDecimal.valueOf(rate));
            var exchangeTransaction = ExchangeTransaction.create(sourceAmount, baseSymbol.get(),
                    targetSymbol.get(), convertedCurrencyAmount, BigDecimal.valueOf(rate));

            exchangeRepository.save(exchangeTransaction);

            log.info(String.format("TransactionID : %s  :: BaseSymbol: %s, TargetSymbol: %s, " +
                            "SourceAmount: %f, Rate: %f, convertedCurrencyAmount: %f",
                    exchangeTransaction.getTransactionId().toString(),
                    exchangeTransaction.getSourceCurrency(), exchangeTransaction.getTargetCurrency(),
                    exchangeTransaction.getSourceAmount(), exchangeTransaction.getRate(),
                    exchangeTransaction.getConvertedCurrencyAmount()));

            var exchangeCurrencyDTO = new ExchangeCurrencyDTO(exchangeTransaction.getTransactionId(),
                    currencyPair, exchangeTransaction.getConvertedCurrencyAmount());

            return exchangeCurrencyDTO;
        }
        return (ExchangeCurrencyDTO) throwException(exchangeRate.getError().getCode(), exchangeRate.getError().getInfo());
    }

    @Override
    public Page<ExchangeTransactionDTO> getTransactions(int pageNumber, int pageSize, String transactionId, LocalDateTime transactionDate) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionDate").descending());

        if (Objects.nonNull(transactionId)) {
            Page<ExchangeTransactionDTO> exchangeTransactions = exchangeRepository.findByTransactionIdDto(transactionId, pageable);
            return exchangeTransactions;
        } else if (Objects.nonNull(transactionDate)) {
            Page<ExchangeTransactionDTO> exchangeTransactions = exchangeRepository.findByTransactionDateAfterDTO(transactionDate, pageable);
            return exchangeTransactions;
        }
        return exchangeRepository.findAll(pageable).map(ExchangeTransaction::toDTO);
    }

    private ExchangeRate getCurrencyRateFromForeignService(String currencyPair,
                                                           AtomicReference<String> baseSymbol,
                                                           AtomicReference<String> targetSymbol) {
        checkAndSeparateCurrencyPair(baseSymbol, targetSymbol, Optional.ofNullable(currencyPair));
        ExchangeRate exchangeRate = fixerRateApi.getExchangeRate(accessKey, baseSymbol.get(), targetSymbol.get());
        return exchangeRate;
    }

    private void checkAndSeparateCurrencyPair(AtomicReference<String> baseSymbol, AtomicReference<String> targetSymbol,
                                              Optional<String> currencyPair) {
        if (currencyPair.isPresent()) {
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(currencyPair.get());
            if (matcher.matches()) {
                baseSymbol.set(matcher.group(1));
                targetSymbol.set(matcher.group(2));
            } else {
                throwException(ErrorCodeEnum.INVALID_CURRENCY.getValue(), String.format("Invalid currency! %s", currencyPair));
            }
        } else {
            throwException(ErrorCodeEnum.INVALID_CURRENCY.getValue(), String.format("Currency cant not be null!"));
        }
    }
}
