package com.example.StaffCalc.services;

import com.example.StaffCalc.dto.UserDto;
import com.example.StaffCalc.models.User;

import java.util.List;

public interface UserService {

    void saveUser(UserDto UserDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
