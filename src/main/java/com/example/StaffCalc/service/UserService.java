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
import java.util.*;

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
            long userId = userDTO.getUser().getId();

            // доход для работника
            userDTO.setIncome(calculate.calculateIncome(userDTO.getWorkingDates(), periodDTO));

            // сумма аванса для работника
            userDTO.setAdvancePaymentAmount(calculate.calculateAdvancePayment(userDTO.getIncome()));

            // список выплат для пользователя
            List<PaymentDTO> paymentDTOList = paymentService.getUserPaymentsInPeriod(userId, periodDTO);
            userDTO.setPayments(paymentDTOList);

            //TODO метожды ниже проводят расчет трех типов выплат при этом каждый из них ходит в базу и вытягивает все платежи за период
            //потом они фильтрую по типу и считают сумму - получаем в данном методе 4 одинаковых запроса в БД
            //на 39 строке ты уже вытянул эти данные из базы и у тебя есть список платежей за период
            // просто фильтруй их по типу в стримах и считай сумму - не нужно плодить кучу не нужных запросов в персистентный слой

            // сумма основных выплат
            double mainPayments = paymentService.getSumOfPaymentsInPeriod(userId, Payment.PaymentType.MAIN_PAYMENT, periodDTO);
            userDTO.setMainPayments(mainPayments);

            // сумма авансовых выплат
            double advancePayments = paymentService.getSumOfPaymentsInPeriod(userId, Payment.PaymentType.ADVANCE_PAYMENT, periodDTO);
            userDTO.setAdvancePayments(advancePayments);

            double extraPayments = paymentService.getSumOfPaymentsInPeriod(userId, Payment.PaymentType.EXTRA_PAYMENT, periodDTO);
            userDTO.setExtraPayments(extraPayments);

            // общую сумму выплат для пользователя в период
            double totalPayments = userDTO.getMainPayments() + userDTO.getAdvancePayments();
            userDTO.setTotalPayments(totalPayments);
        });

        return userDTOList;
    }
}