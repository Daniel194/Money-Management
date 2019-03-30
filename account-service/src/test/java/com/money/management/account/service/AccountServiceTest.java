package com.money.management.account.service;

import com.money.management.account.AccountApplication;
import com.money.management.account.client.StatisticsServiceClient;
import com.money.management.account.domain.*;
import com.money.management.account.exception.BadRequestException;
import com.money.management.account.repository.AccountRepository;
import com.money.management.account.util.AccountUtil;
import com.money.management.account.util.ItemUtil;
import com.sun.security.auth.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@WebAppConfiguration
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private StatisticsServiceClient statisticsClient;

    @Mock
    private AccountRepository repository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFindByName() {
        Account account = new Account();
        account.setName("test@test.com");

        when(repository.findByName(account.getName())).thenReturn(Optional.of(account));
        Account found = accountService.findByName(account.getName());

        assertEquals(account, found);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailWhenNameIsEmpty() {
        when(accountService.findByName("")).thenReturn(null);

        accountService.findByName("");
    }

    @Test
    public void shouldFindByPrincipal() {
        String userName = "test@test.com";
        UserPrincipal userPrincipal = new UserPrincipal(userName);

        Account account = new Account();
        account.setName(userName);

        when(repository.findByName(userName)).thenReturn(Optional.of(account));

        Account found = accountService.findByName(userPrincipal);

        assertEquals(account, found);
    }

    @Test
    public void shouldCreateAccount() {
        String userName = "test@test.com";
        UserPrincipal userPrincipal = new UserPrincipal(userName);

        when(repository.findByName(userName)).thenReturn(Optional.empty());

        Account account = accountService.findByName(userPrincipal);

        assertEquals(userName, account.getName());
        assertEquals(0, account.getSaving().getAmount().intValue());
        assertEquals(Currency.getDefault(), account.getSaving().getCurrency());
        assertEquals(0, account.getSaving().getInterest().intValue());
        assertEquals(false, account.getSaving().getDeposit());
        assertEquals(false, account.getSaving().getCapitalization());
        assertNotNull(account.getLastSeen());

        verify(repository, times(1)).save(account);
    }

    @Test
    public void shouldSaveChangesWhenUpdatedAccountGiven() {
        Account update = AccountUtil.getAccount(ItemUtil.getGrocery());
        Account account = new Account();

        when(repository.findByName("test@test.com")).thenReturn(Optional.of(account));
        accountService.saveChanges("test@test.com", update);

        assertEquals(update.getNote(), account.getNote());
        assertNotNull(account.getLastSeen());

        assertEquals(update.getSaving().getAmount(), account.getSaving().getAmount());
        assertEquals(update.getSaving().getCurrency(), account.getSaving().getCurrency());
        assertEquals(update.getSaving().getInterest(), account.getSaving().getInterest());
        assertEquals(update.getSaving().getDeposit(), account.getSaving().getDeposit());
        assertEquals(update.getSaving().getCapitalization(), account.getSaving().getCapitalization());

        assertEquals(update.getExpenses().size(), account.getExpenses().size());
        assertEquals(update.getIncomes().size(), account.getIncomes().size());

        assertEquals(update.getExpenses().get(0).getTitle(), account.getExpenses().get(0).getTitle());
        assertEquals(0, update.getExpenses().get(0).getAmount().compareTo(account.getExpenses().get(0).getAmount()));
        assertEquals(update.getExpenses().get(0).getCurrency(), account.getExpenses().get(0).getCurrency());
        assertEquals(update.getExpenses().get(0).getPeriod(), account.getExpenses().get(0).getPeriod());
        assertEquals(update.getExpenses().get(0).getIcon(), account.getExpenses().get(0).getIcon());

        assertEquals(update.getIncomes().get(0).getTitle(), account.getIncomes().get(0).getTitle());
        assertEquals(0, update.getIncomes().get(0).getAmount().compareTo(account.getIncomes().get(0).getAmount()));
        assertEquals(update.getIncomes().get(0).getCurrency(), account.getIncomes().get(0).getCurrency());
        assertEquals(update.getIncomes().get(0).getPeriod(), account.getIncomes().get(0).getPeriod());
        assertEquals(update.getIncomes().get(0).getIcon(), account.getIncomes().get(0).getIcon());

        verify(repository, times(1)).save(account);
        verify(statisticsClient, times(1)).updateStatistics("test@test.com", account);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailWhenNoAccountsExistedWithGivenName() {
        Account update = new Account();
        update.setIncomes(Collections.singletonList(new Item()));
        update.setExpenses(Collections.singletonList(new Item()));

        when(repository.findByName("test@test.com")).thenReturn(Optional.empty());

        accountService.saveChanges("test@test.com", update);
    }
}
