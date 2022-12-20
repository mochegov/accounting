package mochegov.accounting.model;

public enum TypeAccount {
    A ("Активный"),
    P ("Пассивный"),
    O ("Активно-пассивный");

    private String name;

    TypeAccount(String name) {
        this.name = name;
    }

    public static TypeAccount getTypeAccountByCode(String code) {
        TypeAccount typeAccount = null;

        if (code.equals("А")) {
            typeAccount = TypeAccount.A;
        } else if (code.equals("П")) {
            typeAccount = TypeAccount.P;
        } else if (code.equals("-")) {
            typeAccount = TypeAccount.O;
        }
        return typeAccount;
    }
}
