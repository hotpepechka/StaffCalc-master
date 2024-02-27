package com.example.StaffCalc.repository;
import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    //TODO не используемый метод
    User findByName(String name);
}
