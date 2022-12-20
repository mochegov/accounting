package mochegov.accounting.operations;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.*;
import mochegov.accounting.services.AccountService;
import mochegov.accounting.services.AccountingEntryService;
import mochegov.accounting.services.LegalService;
import mochegov.accounting.services.PrivateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AccountingOperations {
    private Map<AccountingOperationKind, AccountingOperation> operations = new HashMap();

    private AccountingEntryService accountingEntryService;
    private AccountService accountService;
    private LegalService legalService;
    private PrivateService privateService;

    @Autowired
    public AccountingOperations(AccountingEntryService accountingEntryService,
                                AccountService accountService,
                                LegalService legalService,
                                PrivateService privateService) {
        this.accountingEntryService = accountingEntryService;
        this.accountService = accountService;
        this.legalService = legalService;
        this.privateService = privateService;

        loadOperations();
    }

    // Реализация типовых операций
    private void loadOperations() {

        // Объявление на взнос наличными
        this.operations.put(AccountingOperationKind.CASH_DEPOSIT, (operationDay, client, currency, sum) -> {

            // Поиск счета кассы банка в нужной валюте
            Legal bank = legalService.getLegalByInn(legalService.getBankInn());
            Account debitAccount = accountService.getClientAccount(bank, "20202", currency);

            // Поиск расчетного счета клиента
            Account creditAccount = accountService.getClientAccount(client, "40702", currency);

            return accountingEntryService.
                    addNewAccountingEntry(operationDay, debitAccount, creditAccount, sum,null,
                            "Объявление на взнос наличными " + client.getClientName());
        });

        // Расход наличных с расчетного счета по чеку
        this.operations.put(AccountingOperationKind.WITHDRAWAL_CHECK, (operationDay, client, currency, sum) -> {

            // Поиск расчетного счета клиента
            Account debitAccount = accountService.getClientAccount(client, "40702", currency);

            // Поиск счета кассы банка в нужной валюте
            Legal bank = legalService.getLegalByInn(legalService.getBankInn());
            Account creditAccount = accountService.getClientAccount(bank, "20202", currency);

            return accountingEntryService.
                    addNewAccountingEntry(operationDay, debitAccount, creditAccount, sum,null,
                            "Расход по чеку " + client.getClientName());
        });
    }

    // Выполнение операции по созданию типовой проводки
    public AccountingEntry process(AccountingOperationKind operationKind,
                                   OperationDay operationDay,
                                   Client client,
                                   Currency currency,
                                   BigDecimal sum) {
        AccountingOperation accountingOperation = operations.get(operationKind);
        if (accountingOperation == null) {
            log.error("Не найдена операция по коду");
            return null;
        }

        return accountingOperation.create(operationDay, client, currency, sum);
    }
}
