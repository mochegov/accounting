package mochegov.accounting.operations;

import mochegov.accounting.model.AccountingEntry;
import mochegov.accounting.model.Client;
import mochegov.accounting.model.Currency;
import mochegov.accounting.model.OperationDay;

import java.math.BigDecimal;

@FunctionalInterface
public interface AccountingOperation {
    AccountingEntry create(OperationDay operationDay, Client client, Currency currency, BigDecimal sum);
}