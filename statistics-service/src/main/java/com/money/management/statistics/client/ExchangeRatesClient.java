package com.money.management.statistics.client;

import com.money.management.statistics.domain.ExchangeRatesContainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(url = "${rates.url}", name = "rates-client")
public interface ExchangeRatesClient {

    @RequestMapping(method = RequestMethod.GET)
    ExchangeRatesContainer getRates();

}
