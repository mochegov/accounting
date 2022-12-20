package mochegov.accounting.model;

public enum ProcessEntryResultType {
    OK ("Успешно выполнено"),
    ERROR ("Ошибка выполнения");

    private String name;

    ProcessEntryResultType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
