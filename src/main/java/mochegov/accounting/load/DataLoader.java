package mochegov.accounting.load;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.*;
import mochegov.accounting.model.Currency;
import mochegov.accounting.operations.AccountingOperations;
import mochegov.accounting.services.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DataLoader {

    private static List<String> POLICE_DEPARTMENTS = new ArrayList<>();
    private static List<String> ADDRESSES = new ArrayList<>();

    private static void loadPoliceDepartments(String uploadPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(uploadPath + "/POLICE_DEPARTMENT.TXT"));

        while (scanner.hasNext()) {
            String policeDepartment = scanner.nextLine();
            POLICE_DEPARTMENTS.add(policeDepartment.substring(0, policeDepartment.indexOf("(") - 1));
        }
        scanner.close();
    }

    private static void loadAddresses(String uploadPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(uploadPath + "/ADDRESSES.TXT"));

        while (scanner.hasNext()) {
            ADDRESSES.add(randInt(100000, 199999) + ", " + scanner.nextLine() + ", кв. " + randInt(1, 300));
        }
        scanner.close();
    }

    // Генерация случайного целого числа в заданных границах
    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    // Генерация случайной даты
    public static Date generateDate(Integer beginYear, Integer endYear) {
        Integer year = randInt(beginYear, endYear);
        Integer month = randInt(0, 11);
        Integer day;
        if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
            day = randInt(1, 30);
        } else if (month == 2) {
            day = randInt(1, 28);
        } else {
            day = randInt(1, 31);
        }

        return new Date(year - 1900, month, day);
    }

    // Получение случайной валюты (рубли должны генерироваться чаще других валют)
    public static Currency getRandomCurrency() {
        Currency currency = null;

        int i = 0;
        while (i < 3) {
            currency = Currency.values()[randInt(0, Currency.values().length - 1)];
            if (currency == Currency.RUR) {
                break;
            }
            i++;
        }

        return currency;
    }

    // Загрузка балансовых счетов
    public static void loadBalance(BalanceService balanceService, String uploadPath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new FileReader(uploadPath + "/BALANCE.TXT"));
        int i = 0;

        int chapterCount = 0;
        int balanceOneCount = 0;
        int balanceTwoCount = 0;

        Balance chapter = null;
        Balance balanceOne = null;

        log.info("Создание плана счетов...");

        while (scanner.hasNext()) {
            String row = scanner.nextLine();
            String[] strings = row.split("\\|");

            if ((strings.length == 3) && strings[0].equals("") && strings[1].equals("") && !strings[2].equals("")) {
                // Раздел балансовых счетов
                chapter = balanceService.addBalance(null, 1, null, strings[2], null);
                chapterCount++;

            } else if ((strings.length == 3) && !strings[0].equals("") && strings[1].equals("") && !strings[2].equals("")) {
                // Балансовый счет первого порядка
                balanceOne = balanceService.addBalance(chapter.getId(), 2, strings[0], strings[2], null);
                balanceOneCount++;

            } else if ((strings.length == 4) &&
                    strings[0].equals("") && !strings[1].equals("") && !strings[2].equals("") && !strings[3].equals("")) {
                // Балансовый счет второго порядка
                balanceService.addBalance(balanceOne.getId(), 3, strings[1], strings[2],
                        TypeAccount.getTypeAccountByCode(strings[3]));
                balanceTwoCount++;

            } else {
                // Некорректная строка!
                log.info("[" + i + "] некорректный формат: " + row);
            }
        }
        scanner.close();

        log.info("Разделов: " + chapterCount);
        log.info("Балансовых счетов первого порядка: " + balanceOneCount);
        log.info("Балансовых счетов второго порядка: " + balanceTwoCount);

        //log.info("Печать плана счетов...");
        //BalanceService.printBalance(balanceService.getBalanceList(null, 1));
    }

    // Загрузка клиентов юридических лиц
    public static void loadLegal(LegalService legalService, String uploadPath) throws FileNotFoundException {

        // Создание клиента для банка
        Legal bank = legalService.createBank();
        log.info("Создана карточка клиента для банка: " + bank.getClientName());

        Scanner scanner = new Scanner(new FileReader(uploadPath + "/LEGAL.TXT"));
        int i = 0;

        int legalCount = 0;

        String rows[] = new String[5];
        while (scanner.hasNext()) {
            rows[i] = scanner.nextLine();

            i++;
            if (i == 5) {
                i = 0;

                String name = rows[0];
                String[] requisites = rows[3].split(" ");
                String inn = requisites[1];
                String ogrn = requisites[3];
                String address = rows[2];

                legalService.addLegal(name, inn, ogrn, address);

                legalCount++;
            }
        }
        scanner.close();

        log.info("Всего создано клиентов юридических лиц: " + legalCount);
    }

    // Загрузка клиентов физических лиц
    public static void loadPrivate(PrivateService privateService, String uploadPath) throws FileNotFoundException {

        loadPoliceDepartments(uploadPath);
        loadAddresses(uploadPath);

        int privateCount = 0;

        Scanner scanner = new Scanner(new FileReader(uploadPath + "/PRIVATE.TXT"));
        int i = -1;

        while (scanner.hasNext()) {
            i++;
            String row = scanner.nextLine();

            if (i == 0) {
                String[] fio = row.split(" ");

                privateService.addPrivate(
                        fio[0], // first name
                        fio[1], // last name
                        fio[2], // patronymic
                        generateDate(1960, 1990), // birth date
                        ADDRESSES.get(randInt(0, ADDRESSES.size() - 1)), // address
                        String.valueOf(randInt(1001, 9999)),        // series
                        String.valueOf(randInt(100001, 999999)),    // number
                        generateDate(2000, 2021),                   // date issue
                        POLICE_DEPARTMENTS.get(randInt(0, POLICE_DEPARTMENTS.size() - 1)) // who issued
                        );

                privateCount++;

            } else if (i == 2) {
                i = -1;
            }
        }
        scanner.close();

        log.info("Всего создано клиентов физических лиц: " + privateCount);
    }

    // Открытие расчетных счетов для юридических лиц
    public static void openAccountsForRKO(LegalService legalService, AccountService accountService) {
        legalService.getAllLegals().forEach(legal -> {
            if (!legal.getInn().equals(legalService.getBankInn())) {
                accountService.openAccountForRKO(getRandomCurrency(), legal, new Date(2022 - 1900, 0, 1));
            }
        });
    }

    // Открытие вкладных счетов для физических лиц
    public static void openAccountsForPrivate(PrivateService privateService, AccountService accountService) {
        privateService.getAllPrivate().forEach(client -> accountService.
                openAccountForPrivate(getRandomCurrency(), client, new Date(2022 - 1900, 0, 1)));
    }

    // Открытие счета кассы
    public static void openCashAccounts(LegalService legalService,
                                        AccountService accountService,
                                        BalanceService balanceService) {
        Legal bank = legalService.getLegalByInn(legalService.getBankInn());
        if (bank == null) {
            log.error("Не получается открыть счет кассы, не найден банк по ИНН " + legalService.getBankInn());
            return;
        }

        Balance balance = balanceService.getBalanceByAccountNumber("20202");
        if (balance == null) {
            log.error("Не найден балансовый счет по номеру 20202");
            return;
        }

        Date date = new Date(2022 - 1900, 0, 1);
        Account accountRUR2022 = accountService.openAccount(balance, Currency.RUR, bank, date);
        log.info("Открыт счет кассы (RUR): " + accountRUR2022.getAccountNumber());

        Account accountUSD2022 = accountService.openAccount(balance, Currency.USD, bank, date);
        log.info("Открыт счет кассы (USD): " + accountUSD2022.getAccountNumber());

        Account accountEUR2022 = accountService.openAccount(balance, Currency.EUR, bank, date);
        log.info("Открыт счет кассы (EUR): " + accountEUR2022.getAccountNumber());
    }

    // Генерация операционных дней с 01.01.2022
    public static void addOperationDays(OperationDayService operationDayService) {
        operationDayService.addNewOperationDay(new Date(2022 - 1900, 0, 1));
        for (int i = 0; i < 364; i++) {
            operationDayService.addNewOperationDay();
        }

        log.info("Созданы операционные дни");

        // Открываем первый операционный день 01.01.2022
        operationDayService.openOperationDay(operationDayService
                .getOperationDayByDate(new Date(2022 - 1900, 0, 1)));

        log.info("Открыт операционный день 01.01.2022");
    }

    // Загрузка курса валюты
    public static void loadExchangeRates(Currency currency,
                                         OperationDayService operationDayService,
                                         ExchangeRateService exchangeRateService,
                                         String uploadPath) throws FileNotFoundException, ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Scanner scanner = new Scanner(new FileReader(uploadPath + "/" + currency.name() + ".TXT"));

        while (scanner.hasNext()) {
            String row = scanner.nextLine();
            String[] data = row.split(" ");

            Date date = simpleDateFormat.parse(data[0]);
            BigDecimal rate = new BigDecimal(data[1]);

            OperationDay operationDay = operationDayService.getOperationDayByDate(date);
            if (operationDay != null) {
                exchangeRateService.addNewExchangeRate(currency, operationDay, rate);
            }
        }
        scanner.close();

        log.info("Загружены курсы валюты " + currency.name());
    }

    // Генерация проводок
    public static void generateAccountingEntries(OperationDayService operationDayService,
                                                 LegalService legalService,
                                                 AccountingOperations accountingOperations,
                                                 AccountingEntryService accountingEntryService) {
        OperationDay operationDay = operationDayService.getFirstOpenedOperationDay();

        log.info("Создание проводок...");
        List<AccountingEntry> accountingEntries = new ArrayList<>();
        accountingEntries.add(accountingOperations.process(AccountingOperationKind.CASH_DEPOSIT,
                operationDay,
                legalService.getLegalByInn("2602007409"),
                Currency.RUR,
                new BigDecimal("1000000")));

        accountingEntries.add(accountingOperations.process(AccountingOperationKind.CASH_DEPOSIT,
                operationDay,
                legalService.getLegalByInn("6922006240"),
                Currency.RUR,
                new BigDecimal("1000000")));

        accountingEntries.add(accountingOperations.process(AccountingOperationKind.WITHDRAWAL_CHECK,
                operationDay,
                legalService.getLegalByInn("6922006240"),
                Currency.RUR,
                new BigDecimal("300000")));

        log.info("Проведение документов...");
        for (AccountingEntry entry : accountingEntries) {
            if (entry != null) {
                log.info(
                        "ДТ " + entry.getDebitAccount().getAccountNumber() +
                        "КТ " + entry.getCreditAccount().getAccountNumber() +
                        ", " + entry.getDebitSum().toString() +
                        ", " + entry.getPurpose()
                );

                ProcessEntryResult processEntryResult = accountingEntryService.processEntry(entry);
                if (processEntryResult == ProcessEntryResult.resultOK()) {
                    log.info("  успешно выполнена проводка документа");
                } else {
                    log.info("  не удалось выполнить проводку документа: " + processEntryResult.getErrorString());
                }
            } else {
                log.error("Проводка не задана (null)");
            }
        }
    }

    // Упрощенная версия первоначальной загрузки данных
    public static void simpleLoad(BalanceService balanceService,
                                  LegalService legalService,
                                  AccountService accountService,
                                  OperationDayService operationDayService,
                                  AccountingOperations accountingOperations,
                                  AccountingEntryService accountingEntryService) {
        // Создание балансовых счетов
        Balance chapterCash = balanceService.addBalance(
                null,
                1,
                null,
                "Денежные средства",
                null);
        Balance balance202 = balanceService.addBalance(
                chapterCash.getId(),
                2,
                "202",
                "Наличная валюта и чеки (в том числе дорожные чеки), номинальная стоимость которых указана в иностранной валюте",
                null);
        Balance balance20202 = balanceService.addBalance(
                balance202.getId(),
                3,
                "20202",
                "Касса кредитных организаций",
                TypeAccount.A);
        log.info("Создан балансовый счет: " + balance20202.getAccountNumber() + " " + balance20202.getName());

        Balance chapterClient = balanceService.addBalance(
                null,
                1,
                null,
                "Средства на счетах",
                null);
        Balance balance407 = balanceService.addBalance(
                chapterClient.getId(),
                2,
                "407",
                "Счета негосударственных организаций",
                null);
        Balance balance40702 = balanceService.addBalance(
                balance407.getId(),
                3,
                "40702",
                "Коммерческие организации",
                TypeAccount.P);
        log.info("Создан балансовый счет: " + balance40702.getAccountNumber() + " " + balance40702.getName());

        // Создание клиента для банка
        Legal bank = legalService.createBank();
        log.info("Создана карточка клиента для банка: " + bank.getClientName());

        // Создание клиента ЮЛ
        Legal legal = legalService.addLegal(
                "ООО \"Мяспрод\"",
                "3528297122",
                "1183525040379",
                "162606, Вологодская область, город Череповец, ул. Металлургов, д. 28");
        log.info("Создана карточка клиента ЮЛ: " + legal.getClientName());

        // Открытие счета кассы банка
        Date date = new Date(2022 - 1900, 0, 1);
        Account accountRUR2022 = accountService.openAccount(balance20202, Currency.RUR, bank, date);
        log.info("Открыт счет кассы (RUR): " + accountRUR2022.getAccountNumber());

        // Открытие расчетного счета ЮЛ
        Account account40702 = accountService.openAccountForRKO(Currency.RUR, legal, new Date(2022 - 1900, 0, 1));
        log.info("Открыт расчетный счет ЮЛ: " + account40702.getAccountNumber());

        // Создание операционного дня 01.01.2022
        OperationDay operationDay = operationDayService.addNewOperationDay(new Date(2022 - 1900, 0, 1));
        log.info("Создан операционный день 01.01.2022");

        // Открываем операционный день 01.01.2022
        operationDayService.openOperationDay(operationDay);
        log.info("Открыт операционный день 01.01.2022");

        // Создаем проводку
        AccountingEntry accountingEntry = accountingOperations.process(AccountingOperationKind.CASH_DEPOSIT,
                operationDay, legal, Currency.RUR, new BigDecimal("1000000"));

        // Проводим документ
        if (accountingEntry != null) {
            log.info("Создана проводка: " +
                    "ДТ " + accountingEntry.getDebitAccount().getAccountNumber() +
                    "КТ " + accountingEntry.getCreditAccount().getAccountNumber() +
                    ", " + accountingEntry.getDebitSum().toString() +
                    ", " + accountingEntry.getPurpose()
                    );

            ProcessEntryResult processEntryResult = accountingEntryService.processEntry(accountingEntry);
            if (processEntryResult.getResultType() == ProcessEntryResultType.OK) {
                log.info("  успешно выполнена проводка документа");
            } else {
                log.info("  не удалось выполнить проводку документа: " + processEntryResult.getErrorString());
            }
        } else {
            log.error("Проводка не задана (null)");
        }
    }
}
