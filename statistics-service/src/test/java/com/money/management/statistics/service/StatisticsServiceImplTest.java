package com.money.management.statistics.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.money.management.statistics.StatisticsApplication;
import com.money.management.statistics.domain.*;
import com.money.management.statistics.domain.timeseries.DataPoint;
import com.money.management.statistics.domain.timeseries.ItemMetric;
import com.money.management.statistics.domain.timeseries.StatisticMetric;
import com.money.management.statistics.repository.DataPointRepository;
import com.money.management.statistics.service.impl.ExchangeRatesServiceImpl;
import com.money.management.statistics.service.impl.StatisticsServiceImpl;
import com.money.management.statistics.util.AccountUtil;
import com.money.management.statistics.util.ItemUtil;
import com.money.management.statistics.util.SavingUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StatisticsApplication.class)
public class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private ExchangeRatesServiceImpl ratesService;

    @Mock
    private DataPointRepository repository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFindDataPointListByAccountName() {
        List<DataPoint> list = ImmutableList.of(new DataPoint());
        when(repository.findByIdAccount("test")).thenReturn(list);

        List<DataPoint> result = statisticsService.findByAccountName("test");
        assertEquals(list, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindDataPointWhenAccountNameIsNull() {
        statisticsService.findByAccountName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindDataPointWhenAccountNameIsEmpty() {
        statisticsService.findByAccountName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindDataPointsBetweenDatesWhenParametersAreNull() throws ParseException {
        statisticsService.findByAccountNameBetweenDates(null, null, null);
    }

    @Test
    public void shouldFindDataPointsBetweenDates() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = simpleDateFormat.parse("2018-11-11");
        Date endDate = simpleDateFormat.parse("2018-11-15");

        List<DataPoint> list = ImmutableList.of(new DataPoint());
        when(repository.findByIdAccountBetweenDates("test", beginDate, endDate)).thenReturn(list);

        List<DataPoint> result = statisticsService.findByAccountNameBetweenDates("test", "2018-11-11", "2018-11-15");
        assertEquals(list, result);
    }

    @Test
    public void shouldSaveDataPoint() {
        Item salary = ItemUtil.getItemSalary();
        Item grocery = ItemUtil.getItemGrocery();
        Item vacation = ItemUtil.getItemVacation();
        Saving saving = SavingUtil.getSaving();
        Account account = AccountUtil.getAccount(saving, salary, grocery, vacation);

        Map<Currency, BigDecimal> rates = ImmutableMap.of(
                Currency.EUR, new BigDecimal("0.8"),
                Currency.USD, BigDecimal.ONE
        );

        when(ratesService.convert(any(Currency.class), any(Currency.class), any(BigDecimal.class)))
                .then(i -> ((BigDecimal) i.getArgument(2))
                        .divide(rates.get((Currency) i.getArgument(0)), 4, RoundingMode.HALF_UP));

        when(ratesService.getCurrentRates()).thenReturn(rates);

        when(repository.save(any(DataPoint.class))).then(returnsFirstArg());

        DataPoint dataPoint = statisticsService.save("test", account);

        BigDecimal expectedExpensesAmount = new BigDecimal("511.6361");
        BigDecimal expectedIncomesAmount = new BigDecimal("298.9802");
        BigDecimal expectedSavingAmount = new BigDecimal("1250");

        BigDecimal expectedNormalizedSalaryAmount = new BigDecimal("298.9802");
        BigDecimal expectedNormalizedVacationAmount = new BigDecimal("11.6361");
        BigDecimal expectedNormalizedGroceryAmount = new BigDecimal("500.00");

        assertEquals("test", dataPoint.getId().getAccount());
        assertEquals(dataPoint.getId().getDate(), Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        assertEquals(0, expectedExpensesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.EXPENSES_AMOUNT)));
        assertEquals(0, expectedIncomesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.INCOMES_AMOUNT)));
        assertEquals(0, expectedSavingAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.SAVING_AMOUNT)));

        ItemMetric salaryItemMetric = dataPoint.getIncomes().stream()
                .filter(i -> i.getTitle().equals(salary.getTitle()))
                .findFirst().get();

        ItemMetric vacationItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(vacation.getTitle()))
                .findFirst().get();

        ItemMetric groceryItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(grocery.getTitle()))
                .findFirst().get();

        assertEquals(0, expectedNormalizedSalaryAmount.compareTo(salaryItemMetric.getAmount()));
        assertEquals(0, expectedNormalizedVacationAmount.compareTo(vacationItemMetric.getAmount()));
        assertEquals(0, expectedNormalizedGroceryAmount.compareTo(groceryItemMetric.getAmount()));

        assertEquals(rates, dataPoint.getRates());

        verify(repository, times(1)).save(dataPoint);
    }
}