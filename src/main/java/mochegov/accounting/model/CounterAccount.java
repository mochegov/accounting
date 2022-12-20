package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Data
@Entity
public class CounterAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BALANCE_ID", columnDefinition = "INT NOT NULL")
    private Balance balance;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer value;

    public CounterAccount() {}

    public CounterAccount(Balance balance, Integer value) {
        this.balance = balance;
        this.value = value;
    }
}
