package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.service.PeriodUtils;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
public abstract class BaseCalculate implements Calculate{

    protected CalculateProperties calculateProperties;


    @Override
    public double calculateIncome(Set<LocalDate> workingDates, PeriodDTO periodDTO) {

        long numberOfShifts = workingDates.stream()
                .filter(date -> PeriodUtils.inPeriod(periodDTO, date))
                .count();

        return numberOfShifts * calculateProperties.getIncomePerShift();
    }

}
