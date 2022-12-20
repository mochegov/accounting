package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.*;
import mochegov.accounting.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class AccountService {
    private AccountRepository accountRepository;
    private CounterAccountService counterAccountService;
    private BalanceService balanceService;
    private LegalService legalService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          CounterAccountService counterAccountService,
                          BalanceService balanceService,
                          LegalService legalService) {
        this.accountRepository = accountRepository;
        this.counterAccountService = counterAccountService;
        this.balanceService = balanceService;
        this.legalService = legalService;
    }

    // Определение ключа для счета
    private String getNumKey(String account){

        String bik = legalService.getBik();
        String firstNum = bik.substring(bik.length() - 3) + account;
        String secondNum = "71371371371371371371371";

        int summa = 0;
        for (int i = 0; i < firstNum.length(); i++) {
            summa += Character.digit(firstNum.charAt(i), 10) * Character.digit(secondNum.charAt(i), 10);
        }

        Integer value = (summa % 10) * 3;
        if (value > 9) {
            value = value % 10;
        }

        return account.substring(0, 8) + value + account.substring(9, 20);
    }

    // Открытие счета для клиента
    @Transactional
    public Account openAccount(Balance balance, Currency currency, Client client, Date dateOpen) {

        // Проверка балансового счета
        if (balance.getLevel() != 3) {
            log.error("Можно открывать счета только для балансовых счетов второго порядка");
            return null;
        }

        // Получение порядкового номера счета в разрезе балансового счета второго порядка
        CounterAccount counterAccount = counterAccountService.getCounterAccountByBalance(balance);
        if (counterAccount == null) {
            log.debug("Ранее еще не были открыты счета для данного балансового счета {}", balance.getAccountNumber());
            counterAccount = counterAccountService.addCounterAccount(balance);

        } else {
            log.debug("Для данного балансового счета {} открывались счета ранее. Текущее значение счетчика: {}",
                    balance.getAccountNumber(), counterAccount.getValue());
            counterAccount.setValue(counterAccount.getValue() + 1);
            counterAccountService.updateCounterAccount(counterAccount);
        }

        // Получение номера счета BBBBB.VVV.K.XXXXXXXXXXX
        String accountNumber = getNumKey(balance.getAccountNumber() + currency.getCode() + "0" +
                String.format("%011d", counterAccount.getValue()));

        // Получение наименования счета
        String accountName = balance.getName() + " " + client.getClientName();

        log.debug("Открытие счета {} {}", accountNumber, accountName);

        return accountRepository.save(new Account(currency, dateOpen, accountNumber, accountName,
                balance, BigDecimal.ZERO, client));
    }

    // Открытие расчетного счета для юридического лица
    public Account openAccountForRKO(Currency currency, Client client, Date dateOpen) {
        if (!(client instanceof Legal)) {
            log.error("Расчетные счета можно открывать только для клиентов юридических лиц");
            return null;
        }

        Balance balance = balanceService.getBalanceByAccountNumber("40702");
        if (balance == null) {
            log.error("Не найден балансовый счет по номеру 40702");
            return null;
        }

        return openAccount(balance, currency, client, dateOpen);
    }

    // Открытие вкладного счета для физического лица
    public Account openAccountForPrivate(Currency currency, Private client, Date dateOpen) {
        if (!(client instanceof Private)) {
            log.error("Вкладные счета можно открывать только для клиентов физических лиц");
            return null;
        }

        Balance balance = balanceService.getBalanceByAccountNumber("40817");
        if (balance == null) {
            log.error("Не найден балансовый счет по номеру 40817");
            return null;
        }

        return openAccount(balance, currency, client, dateOpen);
    }

    // Получить открытый счет клиента по номеру балансового счета
    public Account getClientAccount(Client client, String balanceAccountNumber, Currency currency) {
        if (client == null) {
            log.error("Не задан клиент");
            return null;
        }

        if (balanceAccountNumber == null) {
            log.error("Не задан балансовый счет");
            return null;
        }

        Balance balance = balanceService.getBalanceByAccountNumber(balanceAccountNumber);
        if (balance == null) {
            log.error("Не найден балансовый счет по номеру " + balanceAccountNumber);
            return null;
        }

        // Получение списка счетов клиента
        List<Account> accounts = accountRepository.getAccountsByClientAndBalance(client, balance);
        return accounts.stream()
                .filter( // Валюта не задана или задана и совпадает с валютой счета
                         acc -> ((currency == null) || ((currency != null) && (acc.getCurrency() == currency))) &&
                         // Дата закрытия не заполнена
                        (acc.getDateClose() == null))
                .findFirst()
                .orElse(null);
    }

    // Изменение остатка счета
    public void changeAccountRest(Account account, RestChangeType changeType, BigDecimal sum) {
        if (changeType == RestChangeType.INCREASE) {
            account.setRest(account.getRest().add(sum));
        } else if (changeType == RestChangeType.DECREASE) {
            account.setRest(account.getRest().subtract(sum));
        }
        accountRepository.save(account);
    }


}