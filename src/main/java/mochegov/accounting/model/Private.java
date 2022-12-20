package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Slf4j
@Data
@Entity
public class Private extends Client {

    @Column(name = "FIRST_NAME", columnDefinition = "VARCHAR(30) NOT NULL")
    private String firstName;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(30) NOT NULL")
    private String lastName;

    @Column(name = "PATRONYMIC", columnDefinition = "VARCHAR(30) NOT NULL")
    private String patronymic;

    @Column(name = "BIRTH_DATE", columnDefinition = "DATE NOT NULL")
    private Date birthDate;

    private Document document;

    public Private() {}

    public Private(String firstName, String lastName, String patronymic, Date birthDate, Document document) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.document = document;
    }

    @Override
    public String getClientName() {
        return firstName + " " + lastName + " " + patronymic;
    }
}