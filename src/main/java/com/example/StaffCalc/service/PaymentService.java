package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentDTO> getUserPaymentsInPeriod(Long userId, PeriodDTO periodDTO) {
        List<Payment> userPayments = paymentRepository.findByUser_IdInAndPaymentDateBetween(
                Collections.singletonList(userId), periodDTO.getStartDate().plusDays(1), periodDTO.getEndDate().plusDays(1));

        return UserConverter.convertToPaymentDTOList(userPayments);
    }

    public void addNewPayment(User user, LocalDate date, Payment.PaymentType paymentType, Double amount) {
        // Создание записи о новой выплате
        Payment newPayment = new Payment(user, paymentType, date, amount);
        paymentRepository.save(newPayment);
    }

}
