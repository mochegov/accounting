package mochegov.accounting.model;

public enum OperationDayState {
    NEW ("Новый"),
    OPENED ("Открыт"),
    CLOSED ("Закрыт");

    private String name;

    OperationDayState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
