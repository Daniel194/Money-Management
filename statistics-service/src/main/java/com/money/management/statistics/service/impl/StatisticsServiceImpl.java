package com.money.management.statistics.service.impl;

import com.google.common.collect.ImmutableMap;
import com.money.management.statistics.domain.*;
import com.money.management.statistics.domain.timeseries.DataPoint;
import com.money.management.statistics.domain.timeseries.DataPointId;
import com.money.management.statistics.domain.timeseries.ItemMetric;
import com.money.management.statistics.repository.DataPointRepository;
import com.money.management.statistics.domain.timeseries.StatisticMetric;
import com.money.management.statistics.service.ExchangeRatesService;
import com.money.management.statistics.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private DataPointRepository repository;
    private ExchangeRatesService ratesService;

    @Autowired
    public StatisticsServiceImpl(DataPointRepository repository, ExchangeRatesService ratesService) {
        this.repository = repository;
        this.ratesService = ratesService;
    }

    @Override
    public List<DataPoint> findByAccountName(String accountName) {
        Assert.hasLength(accountName, "Account Name can't be empty !");
        return repository.findByIdAccount(accountName);
    }

    @Override
    public List<DataPoint> findByAccountNameBetweenDates(String account, String beginDate, String endDate) throws ParseException {
        Assert.hasLength(account, "Account Name can't be empty !");
        Assert.hasLength(beginDate, "Begin Date can't be empty !");
        Assert.hasLength(endDate, "End Date can't be empty !");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return repository.findByIdAccountBetweenDates(account, sdf.parse(beginDate), sdf.parse(endDate));
    }

    @Override
    public DataPoint save(String accountName, Account account) {
        Instant instant = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        DataPointId pointId = new DataPointId(accountName, Date.from(instant));

        Set<ItemMetric> incomes = account.getIncomes().stream().map(this::createItemMetric).collect(Collectors.toSet());
        Set<ItemMetric> expenses = account.getExpenses().stream().map(this::createItemMetric).collect(Collectors.toSet());
        Map<StatisticMetric, BigDecimal> statistics = createStatisticMetrics(incomes, expenses, account.getSaving());

        DataPoint dataPoint = new DataPoint();
        dataPoint.setId(pointId);
        dataPoint.setIncomes(incomes);
        dataPoint.setExpenses(expenses);
        dataPoint.setStatistics(statistics);
        dataPoint.setRates(ratesService.getCurrentRates());

        log.debug("New datapoint has been created: {}", pointId);

        return repository.save(dataPoint);
    }

    private Map<StatisticMetric, BigDecimal> createStatisticMetrics(Set<ItemMetric> incomes, Set<ItemMetric> expenses, Saving saving) {
        BigDecimal savingAmount = ratesService.convert(saving.getCurrency(), Currency.getBase(), saving.getAmount());
        BigDecimal expensesAmount = expenses.stream().map(ItemMetric::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal incomesAmount = incomes.stream().map(ItemMetric::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ImmutableMap.of(
                StatisticMetric.EXPENSES_AMOUNT, expensesAmount,
                StatisticMetric.INCOMES_AMOUNT, incomesAmount,
                StatisticMetric.SAVING_AMOUNT, savingAmount
        );
    }

    /**
     * Normalizes given item amount to {@link Currency#getBase()} currency with
     * {@link TimePeriod#getBase()} time period
     */
    private ItemMetric createItemMetric(Item item) {
        BigDecimal amount = ratesService.convert(item.getCurrency(), Currency.getBase(), item.getAmount())
                .divide(item.getPeriod().getBaseRatio(), 4, RoundingMode.HALF_UP);

        return new ItemMetric(item.getTitle(), amount);
    }
}
