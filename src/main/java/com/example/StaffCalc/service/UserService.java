package com.example.StaffCalc.service;
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
import java.util.*;

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
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingDates(), periodDTO));
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));
        });

        return userDTOList;
    }


    public void updatePaymentsForUser(User user) {
        // Удалите существующие записи о выплатах
        paymentRepository.deleteByUser(user);

        // Пересчитайте и добавьте новые записи о выплатах
        user.getWorkingDates().forEach(date -> {
            double income = calculate.calculateIncome(user.getWorkingDates(), new PeriodDTO(date, date));
            double advancePayment = calculate.calculateAdvancePayment(income);

            // Создайте и сохраните основную выплату
            Payment mainPayment = new Payment(user, Payment.PaymentType.MAIN_PAYMENT, date, income);
            paymentRepository.save(mainPayment);

            // Создайте и сохраните аванс
            Payment advancePaymentEntity = new Payment(user, Payment.PaymentType.ADVANCE_PAYMENT, date, advancePayment);
            paymentRepository.save(advancePaymentEntity);

            // логику для внеочередной выплаты, если необходимо

        });
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
