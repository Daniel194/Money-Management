package com.money.management.account.repository;

import com.money.management.account.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

	Optional<Account> findByName(String name);

}
