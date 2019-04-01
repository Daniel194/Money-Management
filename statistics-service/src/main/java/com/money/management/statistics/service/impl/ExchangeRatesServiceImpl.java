package com.money.management.statistics.service.impl;

import com.google.common.collect.ImmutableMap;
import com.money.management.statistics.client.ExchangeRatesClient;
import com.money.management.statistics.domain.Currency;
import com.money.management.statistics.domain.ExchangeRatesContainer;
import com.money.management.statistics.service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRatesServiceImpl.class);

    private ExchangeRatesContainer container;
    private ExchangeRatesClient client;

    @Autowired
    public ExchangeRatesServiceImpl(ExchangeRatesClient client) {
        this.client = client;
    }

    @Override
    public Map<Currency, BigDecimal> getCurrentRates() {
        if (container == null || !container.getDate().equals(LocalDate.now())) {
            container = client.getRates();
            log.info("exchange rates has been updated: {}", container);
        }

        return ImmutableMap.of(
                Currency.EUR, container.getRates().get(Currency.EUR.name()),
                Currency.USD, BigDecimal.ONE
        );
    }

    @Override
    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        Assert.notNull(amount, "Amount can't be null");

        Map<Currency, BigDecimal> rates = getCurrentRates();
        BigDecimal ratio = rates.get(to).divide(rates.get(from), 4, RoundingMode.HALF_UP);

        return amount.multiply(ratio);
    }
}
