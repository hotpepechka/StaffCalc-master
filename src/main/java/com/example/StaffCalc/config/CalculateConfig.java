package com.example.StaffCalc.config;

import com.example.StaffCalc.service.calculate.Calculate;
import com.example.StaffCalc.service.calculate.FixedCalculate;
import com.example.StaffCalc.service.calculate.PercentageCalculate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class CalculateConfig {

    @Bean
    @ConditionalOnProperty(prefix = "calculate", value = "percentage-payment-enabled", havingValue = "true")
    public Calculate percentageCalculate(CalculateProperties calculateProperties) {
        return new PercentageCalculate(calculateProperties);
    }


    @Bean
    @ConditionalOnProperty(prefix = "calculate", value = "percentage-payment-enabled", havingValue = "false")
    public Calculate fixedCalculate(CalculateProperties calculateProperties) {
        return new FixedCalculate(calculateProperties);
    }

}
