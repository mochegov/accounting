package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Data
@Entity
public class OperationDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DAY_DATE", columnDefinition = "DATE NOT NULL")
    private Date date;

    @Column(name = "DAY_STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationDayState state;

    @OneToMany(mappedBy = "operationDay", cascade = {CascadeType.ALL})
    private Collection<ExchangeRate> exchangeRates = new ArrayList<>();

    public OperationDay() {}

    public OperationDay(Date date) {
        this.date = date;
        this.state = OperationDayState.NEW;
    }
}
