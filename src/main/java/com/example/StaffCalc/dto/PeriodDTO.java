package com.example.StaffCalc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Getter
@Setter
public class PeriodDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    public PeriodDTO(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
