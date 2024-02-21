package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.service.calculate.Calculate;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final Calculate calculate;


    public List<UserDTO> getUsers(PeriodDTO periodDTO) {
        List<User> users = userRepository.findAll();
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        List<Payment> userPayments = paymentRepository.findByUser_IdInAndPaymentDateBetween(
                userIds, periodDTO.getStartDate(), periodDTO.getEndDate());

        Map<Long, List<Payment>> userPaymentsMap = userPayments.stream()
                .collect(Collectors.groupingBy(payment -> payment.getUser().getId()));

        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(users);

        userDTOList.forEach(userDTO -> {
            long userId = userDTO.getUser().getId();

            // доход для работника
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingDates(), periodDTO));

            // сумма аванса для работника
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));

            // список выплат для пользователя
            List<Payment> userPaymentsForUser = userPaymentsMap.getOrDefault(userId, Collections.emptyList())
                    .stream()
                    .filter(payment -> PeriodUtils.inPeriod(periodDTO, payment.getPaymentDate()))
                    .collect(Collectors.toList());

            List<PaymentDTO> paymentDTOList = UserConverter.convertToPaymentDTOList(userPaymentsForUser);
            userDTO.setPayments(paymentDTOList);

            // сумма основных выплат
            double mainPayments = userPaymentsForUser.stream()
                    .filter(payment -> payment.getType() == Payment.PaymentType.MAIN_PAYMENT)
                    .mapToDouble(Payment::getAmount)
                    .sum();
            userDTO.setMainPayments(mainPayments);

            // сумма авансовых выплат
            double advancePayments = userPaymentsForUser.stream()
                    .filter(payment -> payment.getType() == Payment.PaymentType.ADVANCE_PAYMENT)
                    .mapToDouble(Payment::getAmount)
                    .sum();
            userDTO.setAdvancePayments(advancePayments);

            // общую сумму выплат для пользователя в период
            double totalPayments = userDTO.getMainPayments() + userDTO.getAdvancePayments();
            userDTO.setTotalPayments(totalPayments);
        });

        return userDTOList;
    }

    public void addNewPayment(User user, LocalDate date, Payment.PaymentType paymentType, Double amount) {
        // Создание записи о новой выплате
        Payment newPayment = new Payment(user, paymentType, date, amount);
        paymentRepository.save(newPayment);
    }

}
