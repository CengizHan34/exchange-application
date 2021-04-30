package com.example.exchangeapplication.controller;

import com.example.exchangeapplication.controller.base.BaseController;
import com.example.exchangeapplication.dto.CurrencyConversionRequest;
import com.example.exchangeapplication.dto.CurrencyConversionResponse;
import com.example.exchangeapplication.entity.ExchangeTransaction;
import com.example.exchangeapplication.service.ExchangeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author created by cengizhan on 27.04.2021
 */
@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController extends BaseController {
    private final ExchangeService exchangeService;

    @GetMapping("/rate/{currencyPair}")
    @ApiOperation(value = "Get Exchange Rate", notes = "This method return exchange rate")
    public ResponseEntity<Map> exchangeRate(@PathVariable("currencyPair")
                                            @ApiParam(name = "currencyPair", type = "String", value = "Currency Pair",
                                                    example = "USDTRY", required = true) final String currencyPair) {
        return responseEntity(exchangeService.getExchangeRate(currencyPair));
    }

    @PostMapping("/conversion")
    @ApiOperation(value = "Save Exchange Transaction", notes = "This method saves exchange transaction")
    public ResponseEntity<CurrencyConversionResponse> conversion(@Valid @RequestBody final CurrencyConversionRequest request) {
        return responseEntity(exchangeService.currencyConversion(request));
    }

    @GetMapping("/conversion-list")
    @ApiOperation(value = "Get Conversion List", notes = "This method returns conversation list")
    public ResponseEntity<Page<ExchangeTransaction>> conversionList(@RequestParam(value = "pageNumber", defaultValue = "0")
                                                                    @ApiParam(name = "pageNumber", type = "Integer", value = "Page Number", example = "0") int pageNumber,
                                                                    @RequestParam(value = "pageSize", defaultValue = "10")
                                                                    @ApiParam(name = "pageSize", type = "Integer", value = "Page Size", example = "10") int pageSize,
                                                                    @RequestParam(name = "transactionId", required = false)
                                                                    @ApiParam(name = "transactionId", type = "String", value = "Transaction ID", example = "831fd769-0f96-4512-8e54-bd4975c54c63") String transactionId,
                                                                    @RequestParam(name = "transactionDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                                                    @ApiParam(name = "transactionDate", type = "LocalDateTime", value = "Transaction Date", example = "2022-04-28T19:28:02") LocalDateTime transactionDate) {
        return responseEntity(exchangeService.conversionList(pageNumber, pageSize, transactionId, transactionDate));
    }
}
