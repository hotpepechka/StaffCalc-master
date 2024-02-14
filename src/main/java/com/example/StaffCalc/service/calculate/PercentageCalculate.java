package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.models.User;

public class PercentageCalculate extends BaseCalculate {

    public PercentageCalculate(CalculateProperties calculateProperties) {
        super(calculateProperties);
    }

    @Override
    public double calculateAdvancePayment(Double income) {
        return income * calculateProperties.getAdvancePaymentPercentage() / 100;
    }

    @Override
    public void updatePaymentsForUser(User user) {

    }

}