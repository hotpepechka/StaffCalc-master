package com.example.StaffCalc.repository;

import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    //TODO сделать кастомынй метод на поиск пользователя по имени

}
