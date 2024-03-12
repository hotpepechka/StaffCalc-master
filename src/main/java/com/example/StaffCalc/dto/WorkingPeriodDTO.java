package com.example.StaffCalc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingPeriodDTO {
    private int year;
    private int month;

    private Set<LocalDate> workingDates;
    private LocalDate startDate;
    private LocalDate endDate;

    public WorkingPeriodDTO(int year, int month, Set<LocalDate> workingDates) {
        this.year = year;
        this.month = month;
        this.workingDates = workingDates;

    }

}
