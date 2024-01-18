package com.example.StaffCalc.Repository;


import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp(){
        userRepository.deleteAll();
    }


    //тестирование записи user
    @Test
    public void testSaveAndFindById(){
        User user = new User();
        user.setName("Ilya Kerdysahov");
        userRepository.save(user);

        Long userId = user.getId();
        Optional<User> foundUser = userRepository.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals("Ilya Kerdysahov", foundUser.get().getName());
    }

    //тестирование изменения user
    @Test
    public void testUpdate(){
        User user = new User();
        user.setName("Ilya Kerdyashov");
        userRepository.save(user);

        Long userId = user.getId();
        User retrievedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(retrievedUser);

        retrievedUser.setName("Updated Name");
        userRepository.save(retrievedUser);

        User updatedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    //тестирование удаление user

    @Test
    public void testDelete(){
        User user = new User();
        user.setName("Stas");
        userRepository.save(user);

        Long userId = user.getId();
        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
    }
}
