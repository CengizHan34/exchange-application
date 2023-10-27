package com.example.exchangeapplication.feignclient;

import com.example.exchangeapplication.feignclient.model.ExchangeRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "FixerRateApi", url = "${rates.api.url}")
public interface FixerRateApi {

    @GetMapping("/latest")
    ExchangeRate getExchangeRate(@RequestParam("access_key") String accessApi,
                                 @RequestParam("base") String base,
                                 @RequestParam("symbols") String symbols);
}
