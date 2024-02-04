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
    private List<Month> monthsList;
    private int currentMonth;
    private int defaultSelectedYear;
    private final String[] russianMonths = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
}
