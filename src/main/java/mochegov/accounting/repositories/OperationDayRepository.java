package mochegov.accounting.repositories;

import mochegov.accounting.model.OperationDay;
import mochegov.accounting.model.OperationDayState;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OperationDayRepository extends CrudRepository <OperationDay, Long> {

    // Получение операционного дня по дате
    OperationDay getOperationDayByDate(Date date);

    // Получение последнего операционного дня перед заданной датой
    OperationDay getFirstByDateBeforeOrderByDateDesc(Date date);

    // Получение первого операционного дня после заданной даты
    OperationDay getFirstByDateAfter(Date date);

    // Получение упорядоченного по возрастанию списка операционных дней
    List<OperationDay> getOperationDaysByStateOrderByDate(OperationDayState state);
}
