package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.mapper.UserConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PeriodUtils periodUtils;

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    @Value("${myapp.advancePaymentPercentage}")
    private double advancePaymentPercentage;

    @Autowired
    public UserService(UserRepository userRepository, PeriodUtils periodUtils) {
        this.userRepository = userRepository;
        this.periodUtils = periodUtils;
        this.advancePaymentPercentage = 25;
    }

    public List<UserDTO> getUsers(PeriodDTO periodDTO) {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(users);

        userDTOList.forEach(userDTO -> {
            userDTO.setIncome(calculateIncome(userDTO.getWorkingDates(), periodDTO));
            userDTO.setAdvancePaymentAmount(calculateAdvancePayment(userDTO.getIncome()));
        });

        return userDTOList;
    }



    public double calculateIncome(Set<LocalDate> workingDates, PeriodDTO periodDTO) {


        long numberOfShifts = workingDates.stream()
                .filter(date -> date.isAfter(periodDTO.getStartDate().minusDays(1)) && date.isBefore(periodDTO.getEndDate().plusDays(1)))
                .count();

        return numberOfShifts * incomePerShift;
    }

    public double calculateAdvancePayment(Double income) {
        return income * advancePaymentPercentage / 100;
    }



}
