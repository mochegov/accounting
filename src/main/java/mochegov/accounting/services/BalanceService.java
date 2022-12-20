package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.Balance;
import mochegov.accounting.model.TypeAccount;
import mochegov.accounting.repositories.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BalanceService {
    private BalanceRepository balanceRepository;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    // Печать счетов баланса для проверки
    public static void printBalance(List<Balance> balanceList) {
        if (!balanceList.isEmpty()){
            for (Balance balance: balanceList){
                String shiftLeft = "    ".repeat(balance.getLevel());

                log.info(shiftLeft +
                        (balance.getAccountNumber() == null ? "" : balance.getAccountNumber()) + " " +
                         balance.getName() + " " +
                        (balance.getTypeAccount() == null ? "" : balance.getTypeAccount().name())
                );
                printBalance(balance.getChildBalance());
            }
        }
    }

    // Получение полного списка балансовых счетов вместе с дочерними балансовыми счетами
    public List<Balance> getBalanceList(Long parentId, Integer level) {
        List<Balance> balanceList = balanceRepository.findBalanceByParentIdAndLevel(parentId, level);
        if (!balanceList.isEmpty()) {
            for (Balance balance : balanceList) {
                List<Balance> childBalanceList = getBalanceList(balance.getId(), level + 1);
                balance.setChildBalance(childBalanceList);
            }
        }
        return new ArrayList<>(balanceList);
    }

    // Создает новый балансовый счет
    public Balance addBalance(Long parentId, Integer level, String accountNumber, String name, TypeAccount typeAccount) {
        return balanceRepository.save(new Balance(parentId, level, accountNumber, name, typeAccount));
    }

    // Получение балансового счета по номеру
    public Balance getBalanceByAccountNumber(String accountNumber) {
        return balanceRepository.findBalanceByAccountNumber(accountNumber);
    }

}