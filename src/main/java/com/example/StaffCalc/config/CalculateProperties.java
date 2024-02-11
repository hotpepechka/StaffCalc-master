package com.example.StaffCalc.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "calculate")
public class CalculateProperties {
    private boolean percentagePaymentEnabled;
    private double incomePerShift;
    private int advancePayment;
    private double advancePaymentPercentage;

}
