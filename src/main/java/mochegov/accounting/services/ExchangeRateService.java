package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.Currency;
import mochegov.accounting.model.ExchangeRate;
import mochegov.accounting.model.OperationDay;
import mochegov.accounting.repositories.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ExchangeRateService {
    ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    // Создание нового курса валюты
    public ExchangeRate addNewExchangeRate(Currency currency, OperationDay operationDay, BigDecimal rate) {
        if (currency == null) {
            log.error("Не задана валюта");
            return null;
        }

        if (currency == Currency.RUR) {
            log.error("Нельзя задавать курс валюты для рублей");
            return null;
        }

        if (operationDay == null) {
            log.error("Не задан операционный день");
            return null;
        }

        if (rate == null) {
            log.error("Не задано значение курса");
            return null;
        }

        return exchangeRateRepository.save(new ExchangeRate(currency, operationDay, rate));
    }

    // Получение курса валюты за операционный день
    public ExchangeRate getExchangeRateByOperationDay(OperationDay operationDay, Currency currency) {
        return exchangeRateRepository.getExchangeRateByOperationDayAndCurrency(operationDay, currency);
    }
}
