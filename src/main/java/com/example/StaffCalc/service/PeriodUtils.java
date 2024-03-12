package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PeriodDTO;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class PeriodUtils {


    private static final int START_PERIOD_DATE_FOR_CALCULATE_INCOME = 14;

    public static PeriodDTO getPeriodForCalculateIncome(int providedMonth, int providedYear) {
        LocalDate startDate = LocalDate.of(providedYear, providedMonth, START_PERIOD_DATE_FOR_CALCULATE_INCOME).minusMonths(1);
        LocalDate endDate = LocalDate.of(providedYear, providedMonth, 15);
        return new PeriodDTO(startDate, endDate);
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
