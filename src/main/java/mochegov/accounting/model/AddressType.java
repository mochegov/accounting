package mochegov.accounting.model;

public enum AddressType {
    REGISTRATION ("Адрес регистрации"),
    RESIDENCE ("Адрес проживания"),
    LEGAL ("Юридический адрес");

    private String name;

    AddressType(String name) {
        this.name = name;
    }
}