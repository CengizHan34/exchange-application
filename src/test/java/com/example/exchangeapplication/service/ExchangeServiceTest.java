package com.example.exchangeapplication.service;

import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.dto.CurrencyRate;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.enums.CurrencyType;
import com.example.exchangeapplication.exceptions.InvalidCurrencyException;
import com.example.exchangeapplication.repository.ExchangeRepository;
import com.example.exchangeapplication.service.impl.ExchangeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * @author created by cengizhan on 28.04.2021
 */
@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {
    private ExchangeService target;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ExchangeRepository exchangeRepository;

    private static final String RATE_API = "https://api.ratesapi.io/api/latest?base=USD&symbols=TRY";

    @BeforeEach
    void init() {
        target = new ExchangeServiceImpl(restTemplate, exchangeRepository);
        ReflectionTestUtils.setField(target, "ratesApiUrl", "https://api.ratesapi.io/api/latest");
    }

    private int pageNumber = 0;
    private int pageSize = 10;
    private String transactionId1 = "831fd769-0f96-4512-8e54-bd4975c54c63";
    private String transactionId2 = "12312322-0f96-4512-8e54-b332975c1341";

    private ExchangeTransaction exchangeTransaction1 = ExchangeTransaction.builder()
            .transactionId(UUID.fromString(transactionId1))
            .transactionDate(LocalDateTime.of(2021, 04, 15, 18, 00, 00)).sourceAmount(new BigDecimal("100"))
            .targetAmount(new BigDecimal("800")).sourceCurrency(CurrencyType.USD)
            .targetCurrency(CurrencyType.TRY).currencyPrice(new BigDecimal("8"))
            .build();

    private ExchangeTransaction exchangeTransaction2 = ExchangeTransaction.builder()
            .transactionId(UUID.fromString(transactionId2))
            .transactionDate(LocalDateTime.of(2021, 05, 15, 18, 00, 00)).sourceAmount(new BigDecimal("250"))
            .targetAmount(new BigDecimal("2000")).sourceCurrency(CurrencyType.USD)
            .targetCurrency(CurrencyType.TRY).currencyPrice(new BigDecimal("8"))
            .build();

    private ExchangeTransaction exchangeTransaction3 = ExchangeTransaction.builder()
            .transactionId(UUID.fromString(transactionId2))
            .transactionDate(LocalDateTime.of(2021, 06, 15, 18, 00, 00)).sourceAmount(new BigDecimal("1000"))
            .targetAmount(new BigDecimal("8000")).sourceCurrency(CurrencyType.USD)
            .targetCurrency(CurrencyType.TRY).currencyPrice(new BigDecimal("8"))
            .build();


    @Test
    public void getExchangeRate_ifDesiredConditionsAreMet_shouldReturnExchangeRate() throws URISyntaxException {
        URI uri = new URI(RATE_API);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CurrencyRate> requestEntity = new HttpEntity<>(null, headers);
        Map<CurrencyType, BigDecimal> currencyRateMap = Map.of(CurrencyType.TRY, new BigDecimal("8.0"));
        CurrencyRate currencyRate = new CurrencyRate(CurrencyType.USD, currencyRateMap, LocalDate.now());
        Map<String, BigDecimal> currencyMap = Map.of(String.format("%s/%s", CurrencyType.USD, CurrencyType.TRY), new BigDecimal("8.0"));
        when(restTemplate.exchange(uri, HttpMethod.GET, requestEntity, CurrencyRate.class))
                .thenReturn(new ResponseEntity(currencyRate, HttpStatus.OK));

        Map<String, BigDecimal> response = target.getExchangeRate("USDTRY");
        Assertions.assertEquals(response, currencyMap);
    }

    @Test()
    public void getExchangeRate_ifWrongCurrencyPairIsSent_shouldThrowInvalidCurrencyException() {
        InvalidCurrencyException exception = Assertions.assertThrows(InvalidCurrencyException.class, () -> {
            target.getExchangeRate("TRSUSD");
        });
        Assertions.assertEquals(exception.getMessage(), "Invalid currency! No enum constant com.example.exchangeapplication.enums.CurrencyType.TRS");
    }

    @Test()
    public void getExchangeRate_ifEmptyCurrencyPairIsSent_shouldThrowInvalidCurrencyException() {
        InvalidCurrencyException exception = Assertions.assertThrows(InvalidCurrencyException.class, () -> {
            target.getExchangeRate("");
        });
        Assertions.assertEquals(exception.getMessage(), "Currency pair not match! ");
    }

    @Test
    public void currencyConversion_ifDesiredConditionsAreMet_shouldReturnCurrencyConversionResponse() throws URISyntaxException {
        URI uri = new URI(RATE_API);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CurrencyRate> requestEntity = new HttpEntity<>(null, headers);
        Map<CurrencyType, BigDecimal> currencyRateMap = Map.of(CurrencyType.TRY, new BigDecimal("8.0"));
        CurrencyRate currencyRate = new CurrencyRate(CurrencyType.USD, currencyRateMap, LocalDate.now());
        CurrencyConversionRequest request = new CurrencyConversionRequest(CurrencyType.USD, new BigDecimal("100"), CurrencyType.TRY);

        Map<CurrencyType, BigDecimal> currencyAmountMap = Map.of(CurrencyType.TRY, new BigDecimal("800.0"));

        when(restTemplate.exchange(uri, HttpMethod.GET, requestEntity, CurrencyRate.class))
                .thenReturn(new ResponseEntity(currencyRate, HttpStatus.OK));

        when(exchangeRepository.save(Mockito.any(ExchangeTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        CurrencyConversionResponse result = target.currencyConversion(request);
        Assertions.assertNotNull(result.getTransactionId());
        Assertions.assertEquals(result.getCurrencyAmount(), currencyAmountMap);
    }

    @Test
    public void conversionList_ifTransactionIdIsSent_shouldReturnAnExchangeTransaction() {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionDate").descending());

        Page<ExchangeTransaction> pageExchangeTransaction = new PageImpl(Arrays.asList(exchangeTransaction1));
        when(exchangeRepository.findByTransactionId(UUID.fromString(transactionId1), pageable)).thenReturn(pageExchangeTransaction);

        Page<ExchangeTransaction> result = target.conversionList(pageNumber, pageSize, transactionId1, null);

        Assertions.assertEquals(result.getContent(), Arrays.asList(exchangeTransaction1));
        Assertions.assertEquals(result.getTotalElements(), 1l);
    }

    @Test
    public void conversionList_ifTransactionIdIsSent_multipleExchangeTransactionsMustReturn() {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionDate").descending());

        Page<ExchangeTransaction> pageExchangeTransaction = new PageImpl(Arrays.asList(exchangeTransaction2, exchangeTransaction1));
        when(exchangeRepository.findAllWithTransactionDateTimeBefore(LocalDateTime.of(2021, 05, 15, 18, 00, 00), pageable)).thenReturn(pageExchangeTransaction);

        Page<ExchangeTransaction> result = target.conversionList(pageNumber, pageSize, null, LocalDateTime.of(2021, 05, 15, 18, 00, 00));

        Assertions.assertEquals(result.getContent(), Arrays.asList(exchangeTransaction2, exchangeTransaction1));
        Assertions.assertEquals(result.getContent().get(0), exchangeTransaction2);
        Assertions.assertEquals(result.getContent().get(1), exchangeTransaction1);
        Assertions.assertEquals(result.getTotalElements(), 2l);
    }

    @Test
    public void conversionList_IfTransactionIDAndTransactionDateAreEmpty_multipleExchangeTransactionsMustReturn() {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionDate").descending());
        Page<ExchangeTransaction> pageExchangeTransaction = new PageImpl(Arrays.asList(exchangeTransaction2, exchangeTransaction1, exchangeTransaction3));

        when(exchangeRepository.findAll(pageable)).thenReturn(pageExchangeTransaction);
        Page<ExchangeTransaction> result = target.conversionList(pageNumber, 0, null, null);

        Assertions.assertEquals(result.getContent(), Arrays.asList(exchangeTransaction2, exchangeTransaction1, exchangeTransaction3));
        Assertions.assertEquals(result.getContent().get(0), exchangeTransaction2);
        Assertions.assertEquals(result.getContent().get(1), exchangeTransaction1);
        Assertions.assertEquals(result.getContent().get(2), exchangeTransaction3);
        Assertions.assertEquals(result.getTotalElements(), 3l);
    }


}
