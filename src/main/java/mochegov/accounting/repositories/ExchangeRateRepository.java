package mochegov.accounting.repositories;

import mochegov.accounting.model.Currency;
import mochegov.accounting.model.ExchangeRate;
import mochegov.accounting.model.OperationDay;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRateRepository extends CrudRepository <ExchangeRate, Long> {

    // Получение курса валюты для операционного дня
    ExchangeRate getExchangeRateByOperationDayAndCurrency(OperationDay operationDay, Currency currency);
}
