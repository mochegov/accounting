package mochegov.accounting.config;

import mochegov.accounting.model.Currency;
import mochegov.accounting.operations.AccountingOperations;
import mochegov.accounting.services.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static mochegov.accounting.load.DataLoader.*;

@Configuration
@PropertySource(value = "classpath:application.yaml", encoding = "UTF-8")
public class AppConfig {

    @Value("${upload.path}")
    private String uploadPath;

    @Bean
    public CommandLineRunner runner(BalanceService balanceService,
                                    AccountService accountService,
                                    LegalService legalService,
                                    PrivateService privateService,
                                    OperationDayService operationDayService,
                                    ExchangeRateService exchangeRateService,
                                    AccountingOperations accountingOperations,
                                    AccountingEntryService accountingEntryService) {
        return args -> {

            // Упрощенная загрузка данных
            simpleLoad(balanceService,
                    legalService,
                    accountService,
                    operationDayService,
                    accountingOperations,
                    accountingEntryService);

            /*

            // Загрузка балансовых счетов
            loadBalance(balanceService, uploadPath);

            // Загрузка клиентов ЮЛ
            loadLegal(legalService, uploadPath);

            // Загрузка клиентов ФЛ
            loadPrivate(privateService, uploadPath);

            // Открытие счетов кассы
            openCashAccounts(legalService, accountService, balanceService);

            // Открытие расчетных счетов для юридических лиц
            openAccountsForRKO(legalService, accountService);

            // Открытие вкладных счетов для физических лиц
            openAccountsForPrivate(privateService, accountService);

            // Генерация операционных дней
            addOperationDays(operationDayService);

            // Загрузка курсов валют (USD)
            loadExchangeRates(Currency.USD, operationDayService, exchangeRateService, uploadPath);

            // Загрузка курсов валют (EUR)
            loadExchangeRates(Currency.EUR, operationDayService, exchangeRateService, uploadPath);

            // Создание проводок
            generateAccountingEntries(operationDayService, legalService, accountingOperations, accountingEntryService);

            */
        };
    }
}
