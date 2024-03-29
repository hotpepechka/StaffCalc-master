package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.models.User;

import java.time.LocalDate;
import java.util.Set;

public interface Calculate {

    double calculateAdvancePayment(Double income);

    double calculateIncome(Set<LocalDate> workingDates, PeriodDTO periodDTO);

    public void updatePaymentsForUser(User user);
}
