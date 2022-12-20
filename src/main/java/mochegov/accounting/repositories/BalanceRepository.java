package mochegov.accounting.repositories;

import mochegov.accounting.model.Balance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BalanceRepository extends CrudRepository<Balance, Long>  {

    // Поиск балансовых счетов по ID родительского балансового счета и уровню
    List<Balance> findBalanceByParentIdAndLevel(Long parentId, Integer level);

    // Поиск балансового счета по его номеру
    Balance findBalanceByAccountNumber(String accountNumber);
}
