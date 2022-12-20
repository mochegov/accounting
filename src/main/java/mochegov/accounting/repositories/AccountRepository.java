package mochegov.accounting.repositories;

import mochegov.accounting.model.Account;
import mochegov.accounting.model.Balance;
import mochegov.accounting.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    // Получение счетов клиента по номеру балансового счета
    List<Account> getAccountsByClientAndBalance(Client client, Balance balance);
}
