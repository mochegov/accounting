package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL})
    private Collection<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL})
    private Collection<Account> accounts = new ArrayList<>();

    public abstract String getClientName();
}

