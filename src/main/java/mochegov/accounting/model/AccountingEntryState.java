package mochegov.accounting.model;


public enum AccountingEntryState {
    NEW ("Новая"),
    CONFIRMATION ("На подтверждении"),
    COMPLETED ("Проведена"),
    DELETED ("Ликвидирована");

    private String name;

    AccountingEntryState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
