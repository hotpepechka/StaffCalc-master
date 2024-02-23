package com.example.StaffCalc.repository;

import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    void deleteByUser(User user);

    List<Payment> findByUserAndType(User user, Payment.PaymentType type);

    List<Payment> findByUser(User user);

    List<Payment> findByUser_IdInAndPaymentDateBetween(List<Long> userIds, LocalDate startDate, LocalDate endDate);


}
