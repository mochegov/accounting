package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Data
@Entity(name = "ADDRESS")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private AddressType addressType;

    @Column(name = "ADDRESS", columnDefinition = "VARCHAR(255) NOT NULL")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID", columnDefinition = "INT NOT NULL")
    private Client client;

    public Address() {}

    public Address(AddressType addressType, String address, Client client) {
        this.addressType = addressType;
        this.address = address;
        this.client = client;
    }
}