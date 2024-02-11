package com.example.StaffCalc.Controllers;
import com.example.StaffCalc.controllers.UserController;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodUtils;
import com.example.StaffCalc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ComponentScan("com.example.StaffCalc.controllers")
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PeriodUtils periodUtils;

    @InjectMocks
    private UserController userController;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    @Value("${myapp.advancePaymentPercentage}")
    private double advancePaymentPercentage;

    @Test
    void testList() {
        // Arrange
        int defaultMonth = LocalDate.now().getMonthValue();
        int defaultYear = LocalDate.now().getYear();
        when(periodUtils.getCurrentMonth()).thenReturn(defaultMonth);
        when(userService.getUsers(any(PeriodDTO.class))).thenReturn(Collections.emptyList());
        when(periodUtils.getMonthsList()).thenReturn(Collections.singletonList(Month.JANUARY));

        // Act
        String result = userController.list(model, null, null);

        // Assert
        assertEquals("users", result);
        verify(model).addAttribute(eq("userDTO"), anyList());
        verify(model).addAttribute(eq("periodDTO"), any(PeriodDTO.class));
        verify(model).addAttribute(eq("months"), anyList());
        verify(model).addAttribute(eq("currentMonth"), eq(defaultMonth));
    }

    @Test
    void testAddUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(new User("John"));

        // Act
        String result = userController.addUser("John", redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User added successfully"));
    }

    @Test
    void testEditUserForm() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("John")));

        // Act
        String result = userController.editUserForm(1L, model);

        // Assert
        assertEquals("editUser", result);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void testEditUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("John")));

        // Act
        String result = userController.editUser(1L, "NewName", null, redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User updated successfully"));
    }

    @Test
    void testDeleteUser() {
        // Arrange

        // Act
        String result = userController.deleteUser(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).deleteById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User deleted successfully"));
    }

    @Test
    public void testCalculateIncome() {
        // Создаем тестовые данные
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 10);
        Set<LocalDate> workingDates = new HashSet<>();
        workingDates.add(LocalDate.of(2024, 1, 2));

        PeriodDTO periodDTO = new PeriodDTO();
        periodDTO.setStartDate(startDate);
        periodDTO.setEndDate(endDate);


        when(userService.calculateIncome(workingDates, periodDTO)).thenReturn(1 * incomePerShift);


        double income = userService.calculateIncome(workingDates, periodDTO);
        assertEquals(1 * incomePerShift, income, 0.001);
    }

    @Test
    public void testCalculateAdvancePayment() {

        double income = 1000.0;


        when(userService.calculateAdvancePayment(income)).thenReturn(10.0);


        double advancePayment = userService.calculateAdvancePayment(income);
        assertEquals(10.0, advancePayment, 0.001);
    }




}

