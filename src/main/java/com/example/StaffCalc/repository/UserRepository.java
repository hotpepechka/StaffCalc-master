package com.example.StaffCalc.repository;

import com.example.StaffCalc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByName(String name);

  /*@Query("SELECT u FROM User u LEFT JOIN FETCH u.workingDates WHERE u.id = :userId")
    User findByIdWithWorkingDates(@Param("userId") Long userId);
*/
}
