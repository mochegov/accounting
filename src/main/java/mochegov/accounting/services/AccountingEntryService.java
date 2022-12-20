package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.*;
import mochegov.accounting.repositories.AccountingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class AccountingEntryService {
    private AccountingEntryRepository accountingEntryRepository;
    private ExchangeRateService exchangeRateService;
    private OperationDayService operationDayService;
    private AccountService accountService;

    @Autowired
    public AccountingEntryService(AccountingEntryRepository accountingEntryRepository,
                                  ExchangeRateService exchangeRateService,
                                  OperationDayService operationDayService,
                                  AccountService accountService) {
        this.accountingEntryRepository = accountingEntryRepository;
        this.exchangeRateService = exchangeRateService;
        this.operationDayService = operationDayService;
        this.accountService = accountService;
    }

    // Создание новой проводки
    public AccountingEntry addNewAccountingEntry(OperationDay operationDay,
                                                 Account debitAccount,
                                                 Account creditAccount,
                                                 BigDecimal debitSum,
                                                 BigDecimal creditSum,
                                                 String purpose) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        if (operationDay == null) {
            log.error("Не задан операционный день для создания проводки");
            return null;
        }

        if (operationDay.getState() != OperationDayState.OPENED){
            log.error("Операционный день {} находится в состоянии {}", simpleDateFormat.format(operationDay.getDate()));
            return null;
        }

        if (debitAccount == null) {
            log.error("Не задан счет по дебету");
            return null;
        }

        if (creditAccount == null) {
            log.error("Не задан счет по кредиту");
            return null;
        }

        if (debitAccount.getDateOpen().after(operationDay.getDate())) {
            log.error("Дата открытия счета дебета - после даты операционного дня, в котором создается проводка");
            return null;
        }

        if (creditAccount.getDateOpen().after(operationDay.getDate())) {
            log.error("Дата открытия счета кредита - после даты операционного дня, в котором создается проводка");
            return null;
        }

        if (purpose == null) {
            log.error("Не задано основание проводки");
            return null;
        }

        if ((debitSum == null) && (creditSum == null)) {
            log.error("Не задана сумма проводки");
            return null;
        }

        if ((debitSum != null) && (creditSum != null)) {
            log.error("Нельзя одновременно задавать сумму для дебета и кредита проводки");
            return null;
        }

        if ((debitAccount.getCurrency() != Currency.RUR) && (creditAccount.getCurrency() != Currency.RUR)) {
            log.error("Прямые проводки между счетами в разных иностранных валютах запрещены");
            return null;
        }

        BigDecimal debitSumRur = null;
        BigDecimal creditSumRur = null;

        if (debitSum != null) {
            // Задана сумма проводки по дебету
            if (debitAccount.getCurrency() == Currency.RUR) {
                // Счет по дебету в рублях
                debitSumRur = new BigDecimal(debitSum.toString());

            } else {
                // Счет по дебету в другой валюте
                ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay,
                        debitAccount.getCurrency());
                if (exchangeRate == null) {
                    log.error("Не задан курс валюты {} за {}", debitAccount.getCurrency().name(),
                            simpleDateFormat.format(operationDay.getDate()));
                    return null;
                }
                debitSumRur = debitSum.multiply(exchangeRate.getRate());
            }

        } else {
            // Задана сумма проводки по кредиту
            if (creditAccount.getCurrency() == Currency.RUR) {
                // Счет по кредиту в рублях
                creditSumRur = new BigDecimal(creditSum.toString());
            } else {
                // Счет по кредиту в другой валюте
                ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay,
                        creditAccount.getCurrency());
                if (exchangeRate == null) {
                    log.error("Не задан курс валюты {} за {}", creditAccount.getCurrency().name(),
                            simpleDateFormat.format(operationDay.getDate()));
                    return null;
                }
                creditSumRur = creditSum.multiply(exchangeRate.getRate());
            }
        }

        AccountingEntry accountingEntry =
                new AccountingEntry(operationDay,
                        debitAccount,
                        debitSum,
                        debitSumRur,
                        creditAccount,
                        creditSum,
                        creditSumRur,
                        purpose);

        return accountingEntryRepository.save(accountingEntry);
    }

    // Определение доступного остатка счета
    public BigDecimal getAccountRest(Account account) {
        if (account == null) {
            log.error("Счет не определен");
            return BigDecimal.ZERO;
        }

        return account.getRest().abs();
    }

    // Проводка документа
    @Transactional
    public ProcessEntryResult processEntry(AccountingEntry entry) {
        if (entry == null) {
            return ProcessEntryResult.resultError("Проводка не задана (null)");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        // Проверка операционного дня проводки
        OperationDay operationDay = entry.getOperationDay();
        if (operationDay.getState() != OperationDayState.OPENED) {
            return ProcessEntryResult.resultError("Операционный день " + simpleDateFormat.format(operationDay.getDate()) +
                            " в состоянии " + operationDay.getState().getName());
        }

        // Определение остатка счетов ДТ, КТ и возможности выполнения проводки
        BigDecimal entrySum = entry.getDebitSum() != null ? entry.getDebitSum() : entry.getCreditSum();

        Account debitAccount = entry.getDebitAccount();
        Account creditAccount = entry.getCreditAccount();

        if (debitAccount.getBalance().getTypeAccount() == TypeAccount.P) {
            // По дебету пассивный счет, проверяем достаточность средств на счете ДТ
            if (getAccountRest(entry.getDebitAccount()).compareTo(entrySum) < 0) {
                return ProcessEntryResult.resultError("Не достаточно средств на счете ДТ " +
                        debitAccount.getAccountNumber() + " для выполнения операции");
            }
        }

        if (creditAccount.getBalance().getTypeAccount() == TypeAccount.A) {
            // По кредиту активный счет, проверяем достаточность средств на счете КТ
            if (getAccountRest(entry.getCreditAccount()).compareTo(entrySum) < 0) {
                return ProcessEntryResult.resultError("Не достаточно средств на счете КТ " +
                        creditAccount.getAccountNumber() + " для выполнения операции");
            }
        }

        // Проверки выполнены. Выполняем проведение документа.

        // Изменение статуса документа и даты проводки
        entry.setEntryState(AccountingEntryState.COMPLETED);
        entry.setDateComplete(new Date());

        // Расчет суммы проводки для счета, сумма проводки которого не была заполнена
        if (debitAccount.getCurrency() == creditAccount.getCurrency()) {
            // Валюты счетов ДТ / КТ совпадают
            if ((entry.getDebitSum() != null) && (entry.getCreditSum() == null)) {
                // Задана сумма проводки по дебиту
                entry.setCreditSum(new BigDecimal(entry.getDebitSum().toString()));
                entry.setCreditSumRur(new BigDecimal(entry.getDebitSumRur().toString()));
            } else if ((entry.getDebitSum() == null) && (entry.getCreditSum() != null)) {
                // Задана сумма проводки по кредиту
                entry.setDebitSum(new BigDecimal(entry.getDebitSum().toString()));
                entry.setDebitSumRur(new BigDecimal(entry.getDebitSumRur().toString()));
            }
        } else {
            // Мультивалютная проводка
            ExchangeRate exchangeRate;

            if ((entry.getDebitSum() != null) && (entry.getCreditSum() == null)) {
                // Задана сумма проводки по дебиту
                if ((debitAccount.getCurrency() != Currency.RUR) && (creditAccount.getCurrency() == Currency.RUR)) {
                    // По дебиту - валютный счет, по кредиту - рублевый
                    exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay, debitAccount.getCurrency());
                    if (exchangeRate == null) {
                        log.error("Не задан курс валюты {} за {}", debitAccount.getCurrency().name(),
                                simpleDateFormat.format(operationDay.getDate()));
                        return null;
                    }
                    entry.setCreditSum(entry.getDebitSum().multiply(exchangeRate.getRate()));
                    entry.setCreditSumRur(new BigDecimal(entry.getCreditSum().toString()));

                } else if ((debitAccount.getCurrency() == Currency.RUR) && (creditAccount.getCurrency() != Currency.RUR)) {
                    // По дебету - рублевый счет, по кредиту - валютный
                    exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay, creditAccount.getCurrency());
                    if (exchangeRate == null) {
                        log.error("Не задан курс валюты {} за {}", creditAccount.getCurrency().name(),
                                simpleDateFormat.format(operationDay.getDate()));
                        return null;
                    }
                    entry.setCreditSum(entry.getCreditSum().divide(exchangeRate.getRate()));
                    entry.setCreditSumRur(new BigDecimal(entry.getDebitSum().toString()));
                }
            } else if ((entry.getDebitSum() == null) && (entry.getCreditSum() != null)) {
                // Задана сумма проводки по кредиту
                if ((debitAccount.getCurrency() != Currency.RUR) && (creditAccount.getCurrency() == Currency.RUR)) {
                    // По дебиту - валютный счет, по кредиту - рублевый
                    exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay, debitAccount.getCurrency());
                    if (exchangeRate == null) {
                        log.error("Не задан курс валюты {} за {}", debitAccount.getCurrency().name(),
                                simpleDateFormat.format(operationDay.getDate()));
                        return null;
                    }
                    entry.setDebitSum(entry.getCreditSum().divide(exchangeRate.getRate()));
                    entry.setDebitSumRur(new BigDecimal(entry.getCreditSum().toString()));

                } else if ((debitAccount.getCurrency() == Currency.RUR) && (creditAccount.getCurrency() != Currency.RUR)) {
                    // По дебету - рублевый счет, по кредиту - валютный
                    exchangeRate = exchangeRateService.getExchangeRateByOperationDay(operationDay, creditAccount.getCurrency());
                    if (exchangeRate == null) {
                        log.error("Не задан курс валюты {} за {}", creditAccount.getCurrency().name(),
                                simpleDateFormat.format(operationDay.getDate()));
                        return null;
                    }
                    entry.setDebitSum(entry.getCreditSum().multiply(exchangeRate.getRate()));
                    entry.setDebitSumRur(new BigDecimal(entry.getCreditSum().toString()));
                }
            }
        }

        accountingEntryRepository.save(entry);

        // Изменение текущего остатка счета по дебету
        accountService.changeAccountRest(entry.getDebitAccount(), RestChangeType.DECREASE, entrySum);

        // Изменение текущего остатка счета по кредиту
        accountService.changeAccountRest(entry.getCreditAccount(), RestChangeType.INCREASE, entrySum);

        return ProcessEntryResult.resultOK();
    }

}