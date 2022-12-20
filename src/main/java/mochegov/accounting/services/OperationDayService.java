package mochegov.accounting.services;

import lombok.extern.slf4j.Slf4j;
import mochegov.accounting.model.OperationDay;
import mochegov.accounting.model.OperationDayState;
import mochegov.accounting.repositories.OperationDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OperationDayService {
    private OperationDayRepository operationDayRepository;

    @Autowired
    public OperationDayService(OperationDayRepository operationDayRepository) {
        this.operationDayRepository = operationDayRepository;
    }

    // Получить статус операционного дня
    public OperationDayState getOperationDayState(Date date) {
        OperationDay operationDay = operationDayRepository.getOperationDayByDate(date);
        if (operationDay == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss.MM.yyyy");
            log.info("Не найден операционный день по дате: " + simpleDateFormat.format(date));
            return null;
        }

        return operationDay.getState();
    }

    // Получить самый первый открытый операционный день
    public OperationDay getFirstOpenedOperationDay() {
        List<OperationDay> operationDays = operationDayRepository
                .getOperationDaysByStateOrderByDate(OperationDayState.OPENED);
        if ((operationDays == null) || (operationDays.isEmpty())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss.MM.yyyy");
            log.info("Не найдено ни одного открытого операционного дня");
            return null;
        }

        return operationDays.get(0);
    }

    // Получить все открытые операционные дни
    public List<OperationDay> getAllOpenedOperationDay() {
        return operationDayRepository.getOperationDaysByStateOrderByDate(OperationDayState.OPENED);
    }

    // Создание нового операционного дня для заданной даты
    public OperationDay addNewOperationDay(Date date) {
        OperationDay operationDay = operationDayRepository.getFirstByDateAfter(date);
        if (operationDay != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss.MM.yyyy");
            log.info("Найден операционный день {} после заданной даты {}", simpleDateFormat.format(date));
            return null;
        }

        // Не найдено ни одного операционного дня после заданной даты. Открываем.
        return operationDayRepository.save(new OperationDay(date));
    }

    // Создает новый операционный день, дата операционного дня - следующая после последнего существующего
    public OperationDay addNewOperationDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        OperationDay operationDay = operationDayRepository
                .getFirstByDateBeforeOrderByDateDesc(new Date(9999, 0, 1));
        Date dateNewOperationDay;
        if (operationDay == null) {
            // Не найдено ни одного операционного дня. Создаем новый операционный день, соответствующий текущей дате
            Date today = new Date();
            dateNewOperationDay = new Date(today.getYear(), today.getMonth(), today.getDay());

        } else {
            // Создаем новый операционный день за дату после самого максимального по дате операционного дня
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(operationDay.getDate());
            calendar.add(Calendar.DATE, 1);
            dateNewOperationDay = calendar.getTime();
        }

        return operationDayRepository.save(new OperationDay(dateNewOperationDay));
    }

    // Получение операционного дня по дате
    public OperationDay getOperationDayByDate(Date date) {
        return operationDayRepository.getOperationDayByDate(date);
    }

    // Открытие операционного дня
    public void openOperationDay(OperationDay operationDay) {
        if (operationDay == null) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (operationDay.getState() != OperationDayState.NEW) {
            log.error("Операционный день {} в состоянии: ",
                    simpleDateFormat.format(operationDay), operationDay.getState().getName());
            return;
        }

        operationDay.setState(OperationDayState.OPENED);
        operationDayRepository.save(operationDay);
    }
}
