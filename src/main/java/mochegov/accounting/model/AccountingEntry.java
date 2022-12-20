package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Entity
public class AccountingEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATION_DAY_ID", columnDefinition = "INT NOT NULL")
    private OperationDay operationDay;

    @Column(name = "STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountingEntryState entryState;

    @Column(name = "DATE_CREATE", columnDefinition = "DATE NOT NULL")
    private Date dateCreate;

    @Column(name = "DATE_COMPLETE", columnDefinition = "DATE")
    private Date dateComplete;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEBIT_ACCOUNT_ID")
    private Account debitAccount;

    @Column(name = "DEBIT_SUM")
    private BigDecimal debitSum;

    @Column(name = "DEBIT_SUM_RUR")
    private BigDecimal debitSumRur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CREDIT_ACCOUNT_ID")
    private Account creditAccount;

    @Column(name = "CREDIT_SUM")
    private BigDecimal creditSum;

    @Column(name = "CREDIT_SUM_RUR")
    private BigDecimal creditSumRur;

    @Column(name = "PURPOSE", columnDefinition = "VARCHAR(128) NOT NULL")
    private String purpose;

    public AccountingEntry() {}

    public AccountingEntry(OperationDay operationDay,
                           Account debitAccount,
                           BigDecimal debitSum,
                           BigDecimal debitSumRur,
                           Account creditAccount,
                           BigDecimal creditSum,
                           BigDecimal creditSumRur,
                           String purpose) {
        this.operationDay = operationDay;
        this.dateCreate = new Date();
        this.entryState = AccountingEntryState.NEW;
        this.debitAccount = debitAccount;
        this.debitSum = debitSum;
        this.debitSumRur = debitSumRur;
        this.creditAccount = creditAccount;
        this.creditSum = creditSum;
        this.creditSumRur = creditSumRur;
        this.purpose = purpose;
    }
}
