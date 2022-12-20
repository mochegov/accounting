package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Entity
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CURRENCY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATION_DAY_ID")
    private OperationDay operationDay;

    @Column(name = "RATE")
    private BigDecimal rate;

    public ExchangeRate() {}

    public ExchangeRate(Currency currency, OperationDay operationDay, BigDecimal rate) {
        this.currency = currency;
        this.operationDay = operationDay;
        this.rate = rate;
    }
}
