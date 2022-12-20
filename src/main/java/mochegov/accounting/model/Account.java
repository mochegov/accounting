package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CURRENCY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "DATE_OPEN", columnDefinition = "DATE NOT NULL")
    private Date dateOpen;

    @Column(name = "DATE_CLOSE", columnDefinition = "DATE")
    private Date dateClose;

    @Column(name = "ACCOUNT_NUMBER", columnDefinition = "VARCHAR(20) NOT NULL")
    private String accountNumber;

    @Column(name = "ACCOUNT_NAME", columnDefinition = "VARCHAR(128) NOT NULL")
    private String accountName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BALANCE_ID", columnDefinition = "INT NOT NULL")
    private Balance balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CLIENT_ID", columnDefinition = "INT NOT NULL")
    private Client client;

    @Column(name = "REST")
    private BigDecimal rest;

    public Account() {}

    public Account(Currency currency,
                   Date dateOpen,
                   String accountNumber,
                   String accountName,
                   Balance balance,
                   BigDecimal rest,
                   Client client) {
        this.currency = currency;
        this.dateOpen = dateOpen;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance;
        this.rest = rest;
        this.client = client;
    }
}
