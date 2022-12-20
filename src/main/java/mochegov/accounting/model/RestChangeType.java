package mochegov.accounting.model;

public enum RestChangeType {

    INCREASE ("Увеличение остатка"),
    DECREASE ("Уменьшение остатка");

    private String name;

    RestChangeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
