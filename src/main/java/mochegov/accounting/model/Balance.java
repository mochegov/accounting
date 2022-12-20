package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Data
@Entity
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PARENT_ID", columnDefinition = "INT")
    private Long parentId;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer level;

    @Column(name = "ACCOUNT_NUMBER", columnDefinition = "VARCHAR(5)")
    private String accountNumber;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(name = "ACCOUNT_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    private TypeAccount typeAccount;

    // Список дочерних балансовых счетов
    @Transient
    private List<Balance> childBalance;

    public Balance() {}

    public Balance(Long parentId, Integer level, String accountNumber, String name, TypeAccount typeAccount) {
        this.parentId = parentId;
        this.level = level;
        this.accountNumber = accountNumber;
        this.name = name;
        this.typeAccount = typeAccount;
    }
}
