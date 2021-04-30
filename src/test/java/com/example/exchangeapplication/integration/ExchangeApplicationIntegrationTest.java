package com.example.exchangeapplication.integration;

import com.example.exchangeapplication.tools.PaginatedResponse;
import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.enums.CurrencyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

/**
 * @author created by cengizhan on 29.04.2021
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeApplicationIntegrationTest {
    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void init(){
        url = String.format("http://localhost:%d",port);
    }

    @Test
    public void exchangeRate(){
        StringBuilder stringBuilder = new StringBuilder(url).append("/api/exchange").append("/rate/USDTRY");
        ResponseEntity<Map> response = restTemplate.getForEntity(stringBuilder.toString(),Map.class);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void conversion() throws URISyntaxException {
        URI uri = new URI(url);
        CurrencyConversionRequest conversionRequest = new CurrencyConversionRequest(CurrencyType.USD,new BigDecimal("100"),CurrencyType.TRY);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<CurrencyConversionRequest> request = new HttpEntity<>(conversionRequest, headers);

        StringBuilder stringBuilder = new StringBuilder(url).append("/api/exchange").append("/conversion");
        ResponseEntity<CurrencyConversionResponse> response = this.restTemplate.postForEntity(stringBuilder.toString(), request, CurrencyConversionResponse.class);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void conversionList() {
        ParameterizedTypeReference<PaginatedResponse<ExchangeTransaction>> responseType = new ParameterizedTypeReference<>() {};

        StringBuilder stringBuilder = new StringBuilder(url).append("/api/exchange").append("/conversion-list")
                .append("?pageNumber=0").append("&pageSize=10").append("&transactionId=")
                .append("831fd769-0f96-4512-8e54-bd4975c54c63");

        ResponseEntity<PaginatedResponse<ExchangeTransaction>> response = restTemplate.exchange(stringBuilder.toString(), HttpMethod.GET, null/*httpEntity*/, responseType);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getContent());
        Assertions.assertEquals(response.getBody().getContent().get(0).getTransactionId(), UUID.fromString("831fd769-0f96-4512-8e54-bd4975c54c63"));
    }
}
