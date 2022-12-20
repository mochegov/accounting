package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.Address;
import mochegov.accounting.model.AddressType;
import mochegov.accounting.model.Legal;
import mochegov.accounting.repositories.AddressRepository;
import mochegov.accounting.repositories.LegalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LegalService {
    private LegalRepository legalRepository;
    private AddressRepository addressRepository;

    // Реквизиты банка
    private String bankName;
    private String bankInn;
    private String bankOgrn;
    private String bankAddress;
    private String bik;

    @Autowired
    public LegalService(LegalRepository legalRepository,
                        AddressRepository addressRepository,
                        @Value("${bank.name}") String bankName,
                        @Value("${bank.inn}") String bankInn,
                        @Value("${bank.ogrn}") String bankOgrn,
                        @Value("${bank.address}") String bankAddress,
                        @Value("${bank.requisites.bik}") String bik) {
        this.legalRepository = legalRepository;
        this.addressRepository = addressRepository;
        this.bankName = bankName;
        this.bankInn = bankInn;
        this.bankOgrn = bankOgrn;
        this.bankAddress = bankAddress;
        this.bik = bik;
    }

    // Создание юридического лица
    public Legal addLegal(String name, String inn, String ogrn, String address) {
        Legal legal = new Legal(name, inn, ogrn);
        legalRepository.save(legal);
        legal.getAddresses().add(addressRepository.save(new Address(AddressType.LEGAL, address, legal)));
        legalRepository.save(legal);

        return legal;
    }

    // Получение списка всех юридических лиц
    public Iterable<Legal> getAllLegals() {
        return legalRepository.findAll();
    }

    // Поиск юридического лица по ИНН
    public Legal getLegalByInn(String inn) {
        List<Legal> legals = legalRepository.getLegalsByInn(inn);
        if ((legals == null) || legals.isEmpty()) {
            log.info("Не найден клиент ЮЛ по ИНН " + inn);
            return null;
        }
        return legals.get(0);
    }

    // Создание специализированного клиента для банка
    public Legal createBank() {
        return addLegal(this.bankName, this.bankInn, this.bankOgrn, this.bankAddress);
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankInn() {
        return bankInn;
    }

    public String getBankOgrn() {
        return bankOgrn;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public String getBik() {
        return bik;
    }
}