package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.config.CalculateProperties;

public class PercentageCalculate extends BaseCalculate {

    public PercentageCalculate(CalculateProperties calculateProperties) {
        super(calculateProperties);
    }

    @Override
    public double calculateAdvancePayment(Double income) {
        return income * calculateProperties.getAdvancePaymentPercentage() / 100;
    }
}
