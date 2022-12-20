package mochegov.accounting.services;

import mochegov.accounting.model.*;
import mochegov.accounting.repositories.AddressRepository;
import mochegov.accounting.repositories.PrivateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PrivateService {
    PrivateRepository privateRepository;
    AddressRepository addressRepository;

    @Autowired
    public PrivateService(PrivateRepository privateRepository, AddressRepository addressRepository) {
        this.privateRepository = privateRepository;
        this.addressRepository = addressRepository;
    }

    public Private addPrivate(String firstName,
                              String lastName,
                              String patronymic,
                              Date birthDate,
                              String address,
                              String series,
                              String number,
                              Date dateIssue,
                              String whoIssued) {
        Document document = new Document(DocumentType.PASSPORT, series, number, dateIssue, whoIssued);
        Private client = new Private(firstName, lastName, patronymic, birthDate, document);
        privateRepository.save(client);
        client.getAddresses().add(new Address(AddressType.REGISTRATION, address, client));
        privateRepository.save(client);

        return client;
    }

    // Получение списка всех физических лиц
    public Iterable<Private> getAllPrivate() {
        return privateRepository.findAll();
    }
}
