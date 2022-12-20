package mochegov.accounting.repositories;

import mochegov.accounting.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {}
