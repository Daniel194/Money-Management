package com.money.management.account.repository;

import com.money.management.account.AccountApplication;
import com.money.management.account.domain.*;
import com.money.management.account.util.AccountUtil;
import com.money.management.account.util.ItemUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.Assert.assertEquals;
import static org.springframework.util.Assert.notNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountApplication.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    public void shouldFindAccountByName() {
        Account stub = AccountUtil.getAccount(ItemUtil.getGrocery(), ItemUtil.getVacation());
        repository.save(stub);

        Account found = repository.findByName(stub.getName()).orElse(null);

        notNull(found, "The account doesn't exist !");
        assertEquals(stub.getLastSeen(), found.getLastSeen());
        assertEquals(stub.getNote(), found.getNote());
        assertEquals(stub.getIncomes().size(), found.getIncomes().size());
        assertEquals(stub.getExpenses().size(), found.getExpenses().size());
    }
}
