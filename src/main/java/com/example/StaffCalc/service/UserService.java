package com.example.StaffCalc.service;

import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.calculate.Calculate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
