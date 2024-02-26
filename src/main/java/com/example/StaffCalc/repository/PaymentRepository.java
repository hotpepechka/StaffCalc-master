package com.example.StaffCalc.repository;
import com.example.StaffCalc.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser_IdInAndPaymentDateBetween(List<Long> userIds, LocalDate startDate, LocalDate endDate);

}
