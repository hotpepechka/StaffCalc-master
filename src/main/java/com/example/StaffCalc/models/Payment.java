package com.example.StaffCalc.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private LocalDate paymentDate;

    private double amount;

    public enum PaymentType {
        MAIN_PAYMENT,
        ADVANCE_PAYMENT,
        EXTRA_PAYMENT
    }

    public Payment(User user, PaymentType type, LocalDate paymentDate, double amount) {
        this.user = user;
        this.type = type;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }
}
