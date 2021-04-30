package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.enums.CurrencyType;
import com.example.exchangeapplication.exceptions.InvalidCurrencyException;
import com.example.exchangeapplication.repository.ExchangeRepository;
import com.example.exchangeapplication.service.ExchangeService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

/**
 * @author created by cengizhan on 29.04.2021
 */
@WebMvcTest(controllers = {ExchangeController.class})
public class ExchangeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeServiceMock;

    @MockBean
    private ExchangeRepository exchangeRepositoryMock;

    private StringBuilder urlBuilder = new StringBuilder("/api/exchange");

    @Test
    public void exchangeRate_ifCurrencyPairIsCorrect_shouldReturnSuccess() throws Exception {
        String currencyPair = "USDTRY";
        urlBuilder.append("/rate").append("/").append("USDTRY");
        Map<String, BigDecimal> map = Map.of("USD/TRY", new BigDecimal("8.0"));
        when(exchangeServiceMock.getExchangeRate(currencyPair)).thenReturn(map);

        String result = mockMvc.perform(MockMvcRequestBuilders.get(urlBuilder.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        MatcherAssert.assertThat(result, Matchers.allOf(
                containsString("{\"USD/TRY\":8.0}")
        ));
    }

    @Test
    public void exchangeRate_ifCurrencyPairIsNotCorrect_shouldReturnBadRequest() throws Exception {
        urlBuilder.append("/rate").append("/").append(" ");
        when(exchangeServiceMock.getExchangeRate(" ")).thenThrow(new InvalidCurrencyException(""));

        mockMvc.perform(MockMvcRequestBuilders.get(urlBuilder.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void conversion_IfValuesAreEnteredCorrectly_shouldReturnSuccess() throws Exception {
        String jsonInput =
                "{\n" +
                        "        \"sourceCurrency\": \"TRY\",\n" +
                        "        \"targetCurrency\": \"USD\",\n" +
                        "        \"sourceAmount\" : 800\n" +
                        "}";
        urlBuilder.append("/conversion");
        CurrencyConversionRequest request = new CurrencyConversionRequest(CurrencyType.TRY, new BigDecimal("800"), CurrencyType.EUR);
        Map<CurrencyType, BigDecimal> map = Map.of(CurrencyType.TRY, new BigDecimal("8.0"));
        CurrencyConversionResponse response = new CurrencyConversionResponse(UUID.randomUUID(), map);

        when(exchangeServiceMock.currencyConversion(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void conversion_ifSourceCurrencyIsNotCorrectOrEmpty_shouldReturnBadRequest() throws Exception {
        String jsonInput =
                "{\n" +
                        "        \"sourceCurrency\": \"\",\n" +
                        "        \"targetCurrency\": \"USD\",\n" +
                        "        \"sourceAmount\" : 800\n" +
                        "}";
        urlBuilder.append("/conversion");

        mockMvc.perform(MockMvcRequestBuilders.post(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void conversion_ifTargetCurrencyIsNotCorrectOrEmpty_shouldReturnBadRequest() throws Exception {
        String jsonInput =
                "{\n" +
                        "        \"sourceCurrency\": \"TRY\",\n" +
                        "        \"targetCurrency\": \"\",\n" +
                        "        \"sourceAmount\" : 800\n" +
                        "}";
        urlBuilder.append("/conversion");

        mockMvc.perform(MockMvcRequestBuilders.post(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void conversion_ifSourceAmountIsNotCorrectOrEmpty_shouldReturnBadRequest() throws Exception {
        String jsonInput =
                "{\n" +
                        "        \"sourceCurrency\": \"TRY\",\n" +
                        "        \"targetCurrency\": \"USD\",\n" +
                        "        \"sourceAmount\" : -100\n" +
                        "}";
        urlBuilder.append("/conversion");

        mockMvc.perform(MockMvcRequestBuilders.post(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void conversionList_ifTransactionIdIsSent_shouldReturnSuccess() throws Exception {
        urlBuilder.append("/conversion-list").append("?pageNumber=0").append("&")
                .append("pageSize=10").append("&").append("transactionId=")
                .append("831fd769-0f96-4512-8e54-bd4975c54c63");

        mockMvc.perform(MockMvcRequestBuilders.get(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void conversionList_ifTransactionDateIsSent_shouldReturnSuccess() throws Exception {
        urlBuilder.append("/conversion-list").append("?pageNumber=0").append("&")
                .append("pageSize=10").append("&").append("transactionDate=")
                .append("2021-04-28T19:28:02");

        mockMvc.perform(MockMvcRequestBuilders.get(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void conversionList_ifNothingIsSent_shouldReturnSuccess() throws Exception {
        urlBuilder.append("/conversion-list");

        mockMvc.perform(MockMvcRequestBuilders.get(urlBuilder.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
