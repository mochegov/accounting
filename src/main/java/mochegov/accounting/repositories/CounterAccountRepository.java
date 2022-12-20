package mochegov.accounting.repositories;

import mochegov.accounting.model.Balance;
import mochegov.accounting.model.CounterAccount;
import org.springframework.data.repository.CrudRepository;

public interface CounterAccountRepository extends CrudRepository <CounterAccount, Long> {
    CounterAccount getCounterAccountByBalance(Balance balance);
}