package com.example.StaffCalc.service;

import com.example.StaffCalc.dto.PeriodDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Component
public class PeriodUtils {


    private static final int START_PERIOD_DATE_FOR_CALCULATE_INCOME = 15;
    private int currentMonth;
    private int defaultSelectedYear;

    public static PeriodDTO getPeriodForCalculateIncome(int providedMonth, int providedYear) {

        LocalDate startDate = LocalDate.of(providedYear, providedMonth, START_PERIOD_DATE_FOR_CALCULATE_INCOME).minusMonths(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);


        List<Month> monthsList = Arrays.asList(Month.values());

        PeriodDTO periodDTO = new PeriodDTO();
        periodDTO.setStartDate(startDate);
        periodDTO.setEndDate(endDate);


        return periodDTO;
    }

    public List<Month> getMonthsList() {
        return Arrays.asList(Month.values());
    }

    public int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public int getDefaultYear() {
        return LocalDate.now().getYear();
    }

}
