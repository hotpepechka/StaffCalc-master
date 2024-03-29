package com.example.StaffCalc.service;

import com.example.StaffCalc.dto.PeriodDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class PeriodUtils {


    private static final int START_PERIOD_DATE_FOR_CALCULATE_INCOME = 15;

    public static PeriodDTO getPeriodForCalculateIncome(int providedMonth, int providedYear) {

        LocalDate startDate = LocalDate.of(providedYear, providedMonth, START_PERIOD_DATE_FOR_CALCULATE_INCOME).minusMonths(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        PeriodDTO periodDTO = new PeriodDTO(startDate, endDate);
        periodDTO.setStartDate(startDate);
        periodDTO.setEndDate(endDate);

        return periodDTO;
    }

    public static boolean inPeriod(PeriodDTO period, LocalDate date) {
        return date.isAfter(period.getStartDate().minusDays(1))
                && date.isBefore(period.getEndDate().plusDays(1));
    }



    public static List<Month> getMonthsList() {
        return Arrays.asList(Month.values());
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }


}
