package mochegov.accounting.repositories;

import mochegov.accounting.model.Legal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LegalRepository extends CrudRepository<Legal, Long> {
    // Получение списка юридических лиц по ИНН
    List<Legal> getLegalsByInn(String inn);
}