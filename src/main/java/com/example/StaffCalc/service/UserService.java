package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.calculate.Calculate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final Calculate calculate;
    private final PaymentService paymentService;
    private final PeriodService periodService;

    public List<UserDTO> getUsers(PeriodDTO periodDTO) {
        List<UserDTO> userDTOList = periodService.getUsersWithPeriods(periodDTO);

        userDTOList.forEach(userDTO -> {
            // Рассчитать доход
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingPeriodDates(), periodDTO));

            // Рассчитать сумму аванса
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));

            // Получить список всех выплат для пользователя
            List<PaymentDTO> paymentDTOList = paymentService.getUserPaymentsInPeriod(userDTO.getId(), periodDTO);
            userDTO.setPayments(paymentDTOList);

            // Рассчитать суммы разных типов выплат
            userDTO.setMainPayments(calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.MAIN_PAYMENT));
            userDTO.setAdvancePayments(calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.ADVANCE_PAYMENT));
            userDTO.setExtraPayments(calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.EXTRA_PAYMENT));

            // Рассчитать общую сумму выплат
            userDTO.setTotalPayments(userDTO.getMainPayments() + userDTO.getAdvancePayments() + userDTO.getExtraPayments());
        });

        return userDTOList;
    }

    private double calculateTotalAmountForPaymentType(List<PaymentDTO> payments, Payment.PaymentType paymentType) {
        return payments.stream()
                .filter(paymentDTO -> paymentDTO.getType() == paymentType)
                .mapToDouble(PaymentDTO::getAmount)
                .sum();
    }

    public void fillTakenDates(List<UserDTO> userDTOList) {
        periodService.fillTakenDates(userDTOList);
    }

    @Transactional
    public void editUser(Long id, String name, String workingDatesString) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setName(name);

        // Обработка новых рабочих дат
        if (workingDatesString != null && !workingDatesString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Set<LocalDate> parsedDates = Arrays.stream(workingDatesString.split(","))
                    .map(date -> {
                        try {
                            return LocalDate.parse(date.trim(), formatter);
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException("Invalid date format: " + date);
                        }
                    })
                    .collect(Collectors.toSet());

            periodService.updateWorkingDates(user, parsedDates);
        }

        // Сохранение изменений в репозитории
        userRepository.save(user);
    }

}



