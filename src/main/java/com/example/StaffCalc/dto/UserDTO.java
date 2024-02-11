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
public class UserDTO {


    private Long id;
    private String name;
    private Set<LocalDate> workingDates;
    private double income;
    private double advancePaymentAmount;



}