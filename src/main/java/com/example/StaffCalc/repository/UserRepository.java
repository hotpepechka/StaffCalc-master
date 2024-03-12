package com.example.StaffCalc.repository;
import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.workingPeriods wp " +
            "ON wp.year = :year AND wp.month = :month")
    List<User> findUsersWithWorkingPeriods(@Param("year") int year, @Param("month") int month);

}




