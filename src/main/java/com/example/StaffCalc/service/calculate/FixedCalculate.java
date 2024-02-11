package com.example.StaffCalc.service.calculate;

import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.models.User;

public class FixedCalculate extends BaseCalculate {

    public FixedCalculate(CalculateProperties calculateProperties) {
        super(calculateProperties);
    }

    @Override
    public double calculateAdvancePayment(Double income) {
        return calculateProperties.getAdvancePayment();
    }

    @Override
    public void updatePaymentsForUser(User user) {

    }
}