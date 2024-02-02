package com.example.StaffCalc.service;

import com.example.StaffCalc.dto.PeriodDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Service
public class PeriodService {



    public PeriodDTO getPeriodData(int providedMonth, int providedYear) {
        int defaultMonth = 1;  // You can adjust the default month as needed
        int defaultYear = 2024;  // Set the default year to 2024 if not provided

        // Use the provided values if they are valid, otherwise use defaults
        int month = (providedMonth >= 1 && providedMonth <= 12) ? providedMonth : defaultMonth;
        int year = (providedYear > 0) ? providedYear : defaultYear;

        LocalDate startDate = LocalDate.of(year, month, 15);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Month> monthsList = Arrays.asList(Month.values());

        PeriodDTO periodDTO = new PeriodDTO();
        periodDTO.setStartDate(startDate);
        periodDTO.setEndDate(endDate);
        periodDTO.setMonthsList(monthsList);

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
