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

        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(users);

        userDTOList.forEach(userDTO -> {
            // доход для работника
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingDates(), periodDTO));

            // сумма аванса для работника
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));

            // список выплат для пользователя
            List<Payment> userPayments = paymentRepository.findByUser(userDTO.getUser());

            // фильтрация выплат по месяцам в
            userPayments = userPayments.stream()
                    .filter(payment -> PeriodUtils.inPeriod(periodDTO, payment.getPaymentDate()))
                    .collect(Collectors.toList());

            List<PaymentDTO> paymentDTOList = UserConverter.convertToPaymentDTOList(userPayments);
            userDTO.setPayments(paymentDTOList);

            // сумма основных выплат
            double mainPayments = userPayments.stream()
                    .filter(payment -> payment.getType() == Payment.PaymentType.MAIN_PAYMENT)
                    .mapToDouble(Payment::getAmount)
                    .sum();
            userDTO.setMainPayments(mainPayments);

            // сумма авансовых выплат
            double advancePayments = userPayments.stream()
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

    public void updatePaymentsForUser(User user, String[] mainPaymentsAmount, String[] mainPaymentsDates) {
        paymentRepository.deleteByUser(user);

        List<LocalDate> workingDatesList = new ArrayList<>(user.getWorkingDates());

        Map<LocalDate, Double> mainPaymentsMap = new HashMap<>();

        // значениями из mainPaymentsAmount и mainPaymentsDates
        if (mainPaymentsAmount != null && mainPaymentsDates != null) {
            for (int i = 0; i < Math.min(mainPaymentsAmount.length, mainPaymentsDates.length); i++) {
                LocalDate date = LocalDate.parse(mainPaymentsDates[i]);
                mainPaymentsMap.put(date, Double.parseDouble(mainPaymentsAmount[i]));
            }
        }

        // новые записи о выплатах
        for (LocalDate date : workingDatesList) {
            double income = calculate.calculateIncome(user.getWorkingDates(), new PeriodDTO(date, date));

            // основная выплата
            Payment mainPayment = new Payment(user, Payment.PaymentType.MAIN_PAYMENT, date, income);

            mainPayment.setAmount(0.0);

            // значение mainPaymentsAmount по дате
            if (mainPaymentsMap.containsKey(date)) {
                mainPayment.setAmount(mainPaymentsMap.get(date));
            }

            paymentRepository.save(mainPayment);

            // аванс
            double advancePayment = calculate.calculateAdvancePayment(mainPayment.getAmount());
            Payment advancePaymentEntity = new Payment(user, Payment.PaymentType.ADVANCE_PAYMENT, date, advancePayment);

            paymentRepository.save(advancePaymentEntity);

            // для внеочередной выплаты
        }
    }

    @Transactional
    public void removeAllDates(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setWorkingDates(Collections.emptySet());
            userRepository.save(user);
            paymentRepository.deleteByUser(user);
        }
    }

}
