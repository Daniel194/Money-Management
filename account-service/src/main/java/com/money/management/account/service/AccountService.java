package com.money.management.account.service;

import com.money.management.account.domain.Account;

import java.security.Principal;

public interface AccountService {

    Account findByName(String accountName);

    Account findByName(Principal principal);

    void saveChanges(String name, Account update);
}
