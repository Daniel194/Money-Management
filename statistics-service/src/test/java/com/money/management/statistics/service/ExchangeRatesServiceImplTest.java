package com.money.management.statistics.service;

import com.google.common.collect.ImmutableMap;
import com.money.management.statistics.StatisticsApplication;
import com.money.management.statistics.client.ExchangeRatesClient;
import com.money.management.statistics.domain.Currency;
import com.money.management.statistics.domain.ExchangeRatesContainer;
import com.money.management.statistics.service.impl.ExchangeRatesServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StatisticsApplication.class)
public class ExchangeRatesServiceImplTest {

    @InjectMocks
    private ExchangeRatesServiceImpl ratesService;

    @Mock
    private ExchangeRatesClient client;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldReturnCurrentRatesWhenContainerIsEmptySoFar() {
        ExchangeRatesContainer container = getExchangeRatesContainer();

        when(client.getRates()).thenReturn(container);

        Map<Currency, BigDecimal> result = ratesService.getCurrentRates();
        verify(client, times(1)).getRates();

        assertEquals(container.getRates().get(Currency.EUR.name()), result.get(Currency.EUR));
        assertEquals(BigDecimal.ONE, result.get(Currency.USD));
    }

    @Test
    public void shouldNotRequestRatesWhenTodayContainerAlreadyExists() {
        when(client.getRates()).thenReturn(getExchangeRatesContainer());

        ratesService.getCurrentRates();

        verify(client, times(1)).getRates();
    }

    @Test
    public void shouldConvertCurrency() {
        when(client.getRates()).thenReturn(getExchangeRatesContainer());

        BigDecimal amount = new BigDecimal(100);
        BigDecimal expectedConversion = new BigDecimal("80.00");

        BigDecimal result = ratesService.convert(Currency.USD, Currency.EUR, amount);

        assertEquals(0, expectedConversion.compareTo(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConvertWhenAmountIsNull() {
        ratesService.convert(Currency.EUR, Currency.USD, null);
    }

    private ExchangeRatesContainer getExchangeRatesContainer() {
        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(
                Currency.EUR.name(), new BigDecimal("0.8"),
                Currency.USD.name(), new BigDecimal("80")
        ));
        container.setDate(LocalDate.now());
        container.setBase(Currency.EUR);
        return container;
    }

}