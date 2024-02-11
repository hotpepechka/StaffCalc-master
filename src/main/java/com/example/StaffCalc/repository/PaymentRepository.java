package com.example.StaffCalc.repository;

import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    void deleteByUser(User user);
}
