package com.example.StaffCalc.dto;

import com.example.StaffCalc.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private final String[] russianMonths = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private Long id;
    private String name;
    private Set<LocalDate> workingDates;


    private double incomePerShift;
    private List<User> users;
    private Map<User, Double> incomeMap;

    // Add new fields
    private List<Month> monthsList;
    private int currentMonth;
    private int defaultSelectedYear;
    private double advancePaymentAmount;



}
