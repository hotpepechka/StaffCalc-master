package com.example.StaffCalc.mapper;

import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {
    public static UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setWorkingDates(user.getWorkingDates());

        return userDTO;
    }

    public static List<UserDTO> convertToUserDTOList(List<User> users) {
        return users.stream()
                .map(UserConverter::convertToUserDTO)
                .collect(Collectors.toList());
    }
}
