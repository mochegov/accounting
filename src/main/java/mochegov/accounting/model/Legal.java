package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

@Slf4j
@Data
@Entity
public class Legal extends Client{

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String inn;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL")
    private String ogrn;

    public Legal() {}

    public Legal(String name, String inn, String ogrn) {
        this.name = name;
        this.inn = inn;
        this.ogrn = ogrn;
    }

    @Override
    public String getClientName() {
        return this.name;
    }
}