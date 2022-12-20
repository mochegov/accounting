package mochegov.accounting.repositories;

import mochegov.accounting.model.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository <Client, Long> {}
