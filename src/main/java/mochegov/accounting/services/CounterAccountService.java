package mochegov.accounting.services;

import mochegov.accounting.model.Balance;
import mochegov.accounting.model.CounterAccount;
import mochegov.accounting.repositories.CounterAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CounterAccountService {
    private CounterAccountRepository counterAccountRepository;

    @Autowired
    public CounterAccountService(CounterAccountRepository counterAccountRepository) {
        this.counterAccountRepository = counterAccountRepository;
    }

    // Получить счетчик балансовых счетов по номеру балансового счета
    public CounterAccount getCounterAccountByBalance(Balance balance) {
        return counterAccountRepository.getCounterAccountByBalance(balance);
    }

    // Создает новый счетчик балансового счета
    public CounterAccount addCounterAccount(Balance balance) {
        return counterAccountRepository.save(new CounterAccount(balance, 1));
    }

    // Обновляет счетчик балансового счета
    public void updateCounterAccount(CounterAccount counterAccount) {
        counterAccountRepository.save(counterAccount);
    }

}
