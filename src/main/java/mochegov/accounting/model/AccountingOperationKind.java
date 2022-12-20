package mochegov.accounting.model;

public enum AccountingOperationKind {
    CASH_DEPOSIT ("Объявление на взнос наличными"),
    WITHDRAWAL_CHECK ("Расход по кассовому чеку");

    private String name;

    AccountingOperationKind(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
