package mochegov.accounting.model;

public enum DocumentType {
    PASSPORT ("Паспорт"),
    DRIVING_LICENCE ("Водительское удостоверение");

    private String name;

    DocumentType(String name) {
        this.name = name;
    }
}