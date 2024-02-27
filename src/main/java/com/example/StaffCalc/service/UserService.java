package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.mapper.UserConverter;
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

    public List<UserDTO> getUsers(PeriodDTO periodDTO) {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(users);

        userDTOList.forEach(userDTO -> {
            long userId = userDTO.getId();

            // доход для работника
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingDates(), periodDTO));

            // сумма аванса для работника
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));

            // Список всех выплат для пользователя за период
            List<PaymentDTO> paymentDTOList = paymentService.getUserPaymentsInPeriod(userId, periodDTO);
            userDTO.setPayments(paymentDTOList);

            double mainPayments = calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.MAIN_PAYMENT);
            userDTO.setMainPayments(mainPayments);

            double advancePayments = calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.ADVANCE_PAYMENT);
            userDTO.setAdvancePayments(advancePayments);

            double extraPayments = calculateTotalAmountForPaymentType(paymentDTOList, Payment.PaymentType.EXTRA_PAYMENT);
            userDTO.setExtraPayments(extraPayments);

            // Общая сумма выплат для пользователя в период
            double totalPayments = userDTO.getMainPayments() + userDTO.getAdvancePayments() + userDTO.getExtraPayments();
            userDTO.setTotalPayments(totalPayments);
        });

        return userDTOList;
    }

    private double calculateTotalAmountForPaymentType(List<PaymentDTO> payments, Payment.PaymentType paymentType) {
        return payments.stream()
                .filter(paymentDTO -> paymentDTO.getType() == paymentType)
                .mapToDouble(PaymentDTO::getAmount)
                .sum();
    }

    public void fillFilteredWorkingDates(List<UserDTO> userDTOList, PeriodDTO periodDTO) {
        for (UserDTO user : userDTOList) {
            List<LocalDate> filteredDates = user.getWorkingDates().stream()
                    .filter(date -> PeriodUtils.inPeriod(periodDTO, date))
                    .collect(Collectors.toList());
            user.setFilteredWorkingDates(filteredDates);
        }
    }

    public void fillTakenDates(List<UserDTO> userDTOList) {
        for (UserDTO userDTO : userDTOList) {
            List<LocalDate> takenDates = userDTOList.stream()
                    .filter(otherUser -> !otherUser.getId().equals(userDTO.getId()))
                    .flatMap(otherUser -> otherUser.getWorkingDates().stream())
                    .distinct()
                    .collect(Collectors.toList());

            userDTO.setTakenDates(takenDates);
        }
    }

    @Transactional
    public void editUser(Long id, String name, String workingDatesString) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setName(name);

        // Обработка рабочих дат
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

            user.setWorkingDates(parsedDates);
        } else {
            user.setWorkingDates(Collections.emptySet());
        }

        userRepository.save(user);
    }
}