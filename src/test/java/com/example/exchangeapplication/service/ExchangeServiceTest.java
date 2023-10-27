package com.example.exchangeapplication.service;

import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.exception.AccessRestrictException;
import com.example.exchangeapplication.exception.InvalidCurrencyException;
import com.example.exchangeapplication.feignclient.FixerRateApi;
import com.example.exchangeapplication.feignclient.model.ApiError;
import com.example.exchangeapplication.feignclient.model.ExchangeRate;
import com.example.exchangeapplication.model.ExchangeCurrencyDTO;
import com.example.exchangeapplication.model.ExchangeRateDTO;
import com.example.exchangeapplication.model.ExchangeTransactionDTO;
import com.example.exchangeapplication.repository.ExchangeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExchangeServiceTest {
    @MockBean
    private FixerRateApi fixerRateApi;
    @MockBean
    private ExchangeRepository exchangeRepository;
    private ExchangeService target;

    @BeforeAll
    public void init() {
        target = new ExchangeServiceImpl(fixerRateApi, exchangeRepository);
        ReflectionTestUtils.setField(target, "accessKey", "7d77dfe584947c2afc1ac847e3fb00a2");
    }

    @Test
    public void testGetExchangeRate_ifItMeetsTheRequirements_ShouldWorkSuccessfully() {
        String currencyPair = "USD/TRY";
        ExchangeRate exchangeRate = ExchangeRate.create(true, null, 12321323123l,
                "USD", "TRY", Map.of("TRY", 29.22));
        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);
        ExchangeRateDTO result = target.getExchangeRate(currencyPair);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("USD", result.baseSymbol());
        Assertions.assertEquals("TRY", result.targetSymbol());
    }

    @Test
    public void testGetExchangeRate_ifErrorMessageReturnedFromExternalService1_ShouldThrowException() {
        String currencyPair = "USD/TRY";
        ApiError apiError = new ApiError(105, "error", null);

        ExchangeRate exchangeRate = ExchangeRate.create(false, apiError);

        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);
        Assertions.assertThrows(AccessRestrictException.class, () -> {
            target.getExchangeRate(currencyPair);
        });

    }

    @Test
    public void testGetExchangeRate_ifErrorMessageReturnedFromExternalService2_ShouldThrowException() {
        String currencyPair = "USD/TRY";
        ApiError apiError = new ApiError(99, "error", null);

        ExchangeRate exchangeRate = ExchangeRate.create(false, apiError);

        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);
        Assertions.assertThrows(RuntimeException.class, () -> {
            target.getExchangeRate(currencyPair);
        });

    }

    @Test
    public void testGetExchangeRate_ifCurrencyPairIsInvalid_ShouldThrowException() {
        String currencyPair = "USD/TRYY";
        Assertions.assertThrows(InvalidCurrencyException.class, () -> {
            target.getExchangeRate(currencyPair);
        });
    }

    @Test
    public void testGetExchangeRate_ifCurrencyPairIsNull_ShouldThrowException() {
        String currencyPair = null;
        Assertions.assertThrows(InvalidCurrencyException.class, () -> {
            target.getExchangeRate(currencyPair);
        });
    }

    @Test
    public void testExchangeCurrency_ifItMeetsTheRequirements_ShouldWorkSuccessfully() {
        String currencyPair = "USD/TRY";
        ExchangeRate exchangeRate = ExchangeRate.create(true, null, 12321323123l,
                "USD", "TRY", Map.of("TRY", 29.00));

        ExchangeTransaction exchangeTransaction = new ExchangeTransaction(BigDecimal.valueOf(100), "USD",
                "TRY", BigDecimal.valueOf(2900.00), BigDecimal.valueOf(29));

        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);
        when(exchangeRepository.save(any(ExchangeTransaction.class))).thenReturn(exchangeTransaction);
        ExchangeCurrencyDTO exchangeCurrencyDTO = target.exchangeCurrency(currencyPair, BigDecimal.valueOf(100));

        Assertions.assertNotNull(exchangeCurrencyDTO);
        Assertions.assertEquals(BigDecimal.valueOf(2900.0), exchangeCurrencyDTO.convertedCurrencyAmount());
        Assertions.assertNotEquals(exchangeTransaction.getTransactionId(), exchangeCurrencyDTO.transactionID());
    }

    @Test
    public void testExchangeCurrency_ifCurrencyPairIsInvalid_ShouldThrowException() {
        String currencyPair = "USD/TRY";
        ApiError apiError = new ApiError(202, "error", null);

        ExchangeRate exchangeRate = ExchangeRate.create(false, apiError);

        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);

        Assertions.assertThrows(InvalidCurrencyException.class, () -> {
            target.exchangeCurrency(currencyPair, BigDecimal.valueOf(100));
        });
    }

    @Test
    public void testExchangeCurrency_ifAccessRestricted_ShouldThrowException() {
        String currencyPair = "USD/TRY";
        ApiError apiError = new ApiError(105, "error", null);

        ExchangeRate exchangeRate = ExchangeRate.create(false, apiError);

        when(fixerRateApi.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(exchangeRate);

        Assertions.assertThrows(AccessRestrictException.class, () -> {
            target.exchangeCurrency(currencyPair, BigDecimal.valueOf(100));
        });
    }

    @Test
    public void testGetTransactions_ifSendTransactionId_ShouldWorkSuccessfully() {
        ExchangeTransaction exchangeTransaction1 = new ExchangeTransaction(BigDecimal.valueOf(100), "USD",
                "TRY", BigDecimal.valueOf(2900.00), BigDecimal.valueOf(29));

        Page<ExchangeTransactionDTO> exchangeTransactionDTOS = new PageImpl(Arrays.asList(exchangeTransaction1));

        when(exchangeRepository.findByTransactionIdDto(anyString(), any(Pageable.class))).thenReturn(exchangeTransactionDTOS);
        Page<ExchangeTransactionDTO> result = target.getTransactions(0, 10, "213123-12321-213", null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Arrays.asList(exchangeTransaction1), result.getContent());
    }

    @Test
    public void testGetTransactions_ifSendTransactionDate1_ShouldWorkSuccessfully() {
        ExchangeTransaction exchangeTransaction1 = new ExchangeTransaction(BigDecimal.valueOf(100), "USD",
                "TRY", BigDecimal.valueOf(2900.00), BigDecimal.valueOf(29));
        exchangeTransaction1.setTransactionDate(LocalDateTime.now());

        ExchangeTransaction exchangeTransaction2 = new ExchangeTransaction(BigDecimal.valueOf(1000), "USD",
                "TRY", BigDecimal.valueOf(29000.00), BigDecimal.valueOf(29));
        exchangeTransaction2.setTransactionDate(LocalDateTime.of(2023, 10, 20, 00, 00, 00));

        Page<ExchangeTransactionDTO> exchangeTransactionDTOS = new PageImpl(Arrays.asList(exchangeTransaction1, exchangeTransaction2));

        when(exchangeRepository.findByTransactionDateAfterDTO(any(LocalDateTime.class), any(Pageable.class))).thenReturn(exchangeTransactionDTOS);
        Page<ExchangeTransactionDTO> result = target.getTransactions(0, 10, null,
                LocalDateTime.of(2023, 10, 20, 00, 00, 00));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Arrays.asList(exchangeTransaction1, exchangeTransaction2), result.getContent());
    }

    @Test
    public void testGetTransactions_ifSendTransactionDate2_ShouldWorkSuccessfully() {
        ExchangeTransaction exchangeTransaction1 = new ExchangeTransaction(BigDecimal.valueOf(100), "USD",
                "TRY", BigDecimal.valueOf(2900.00), BigDecimal.valueOf(29));
        exchangeTransaction1.setTransactionDate(LocalDateTime.now());

        Page<ExchangeTransactionDTO> exchangeTransactionDTOS = new PageImpl(Arrays.asList(exchangeTransaction1));

        when(exchangeRepository.findByTransactionDateAfterDTO(any(LocalDateTime.class), any(Pageable.class))).thenReturn(exchangeTransactionDTOS);
        Page<ExchangeTransactionDTO> result = target.getTransactions(0, 10, null, LocalDateTime.of(2023, 10, 25, 00, 00, 00));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Arrays.asList(exchangeTransaction1), result.getContent());
    }

    @Test
    public void testGetTransactions_ifSendNull_ShouldWorkSuccessfully() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ExchangeTransaction exchangeTransaction1 = new ExchangeTransaction(BigDecimal.valueOf(100), "USD",
                "TRY", BigDecimal.valueOf(2900.00), BigDecimal.valueOf(29));
        exchangeTransaction1.setTransactionDate(LocalDateTime.now());

        ExchangeTransaction exchangeTransaction2 = new ExchangeTransaction(BigDecimal.valueOf(1000), "USD",
                "TRY", BigDecimal.valueOf(29000.00), BigDecimal.valueOf(29));
        exchangeTransaction2.setTransactionDate(LocalDateTime.of(2023, 10, 20, 00, 00, 00));
        ExchangeTransactionDTO exchangeTransactionDTO1 = null;
        ExchangeTransactionDTO exchangeTransactionDTO2 = null;
        try {
            exchangeTransactionDTO1 = objectMapper.readValue(objectMapper.writeValueAsString(exchangeTransaction1), ExchangeTransactionDTO.class);
            exchangeTransactionDTO2 = objectMapper.readValue(objectMapper.writeValueAsString(exchangeTransaction2), ExchangeTransactionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Page<ExchangeTransaction> exchangeTransactionDTOS = new PageImpl(Arrays.asList(exchangeTransaction1, exchangeTransaction2));

        when(exchangeRepository.findAll(any(Pageable.class))).thenReturn(exchangeTransactionDTOS);

        Page<ExchangeTransactionDTO> result = target.getTransactions(0, 10, null, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Arrays.asList(exchangeTransactionDTO1, exchangeTransactionDTO2), result.getContent());
    }
}
