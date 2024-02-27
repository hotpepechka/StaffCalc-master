package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    //TODO сервси помечен как Transactional - получается что каждый метод транзакционный
    //в этом случае у тебя под транзакцию попадает логика связанная с конвертаций
    //корректнее для данного метода будет оставить транзакицю только в paymentRepository в момент получения данных
    //для остальных операций она не требуется
    public List<PaymentDTO> getUserPaymentsInPeriod(Long userId, PeriodDTO periodDTO) {
        //TODO забыл про сдвиг даты для платежей - сейчас все еще платеж внесенный в дату выплаты попадает не в тот период
        List<Payment> userPayments = paymentRepository.findByUser_IdInAndPaymentDateBetween(
                Collections.singletonList(userId), periodDTO.getStartDate(), periodDTO.getEndDate());

        return UserConverter.convertToPaymentDTOList(userPayments);
    }

    //TODO сервси помечен как Transactional - получается что каждый метод транзакционный
    //в этом случае у тебя под транзакцию попадает логика связанная с фильтрацией и расчетом суммы
    //корректнее для данного метода будет оставить транзакицю только в paymentRepository в момент получения данных
    //для остальных операций она не требуется
    public double getSumOfPaymentsInPeriod(Long userId, Payment.PaymentType paymentType, PeriodDTO periodDTO) {
        List<Payment> userPayments = paymentRepository.findByUser_IdInAndPaymentDateBetween(
                Collections.singletonList(userId), periodDTO.getStartDate(), periodDTO.getEndDate());

        return userPayments.stream()
                .filter(payment -> payment.getType() == paymentType)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public void addNewPayment(User user, LocalDate date, Payment.PaymentType paymentType, Double amount) {
        // Создание записи о новой выплате
        Payment newPayment = new Payment(user, paymentType, date, amount);
        paymentRepository.save(newPayment);
    }

}
