package mochegov.accounting.repositories;

import mochegov.accounting.model.AccountingEntry;
import org.springframework.data.repository.CrudRepository;

public interface AccountingEntryRepository extends CrudRepository <AccountingEntry, Long> {}