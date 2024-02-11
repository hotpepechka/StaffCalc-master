package com.example.StaffCalc.dto;

import com.example.StaffCalc.models.Payment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentDTO {

    private Long id;
    private Payment.PaymentType type;
    private LocalDate paymentDate;
    private double amount;
}
