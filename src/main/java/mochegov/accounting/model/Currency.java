package mochegov.accounting.model;

public enum Currency {
    USD ("840"),
    EUR ("978"),
    RUR ("810");

    private String code;

    Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
