package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.dto.PeriodDTO;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
public abstract class BaseCalculate implements Calculate{

    protected CalculateProperties calculateProperties;


    @Override
    public double calculateIncome(Set<LocalDate> workingDates, PeriodDTO periodDTO) {

        long numberOfShifts = workingDates.stream()
                .filter(date -> date.isAfter(periodDTO.getStartDate().minusDays(1)) && date.isBefore(periodDTO.getEndDate().plusDays(1)))
                .count();

        return numberOfShifts * calculateProperties.getIncomePerShift();
    }

}
