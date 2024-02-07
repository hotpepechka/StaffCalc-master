package com.example.StaffCalc.Controllers;
import com.example.StaffCalc.controllers.UserController;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodService;
import com.example.StaffCalc.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    private PeriodService periodService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testList() {
        // Arrange
        Model model = mock(Model.class);
        when(periodService.getPeriodData(1, 2024)).thenReturn(new PeriodDTO());
        when(userService.getUserData(any(PeriodDTO.class))).thenReturn(new UserDTO());
        when(periodService.getMonthsList()).thenReturn(Collections.singletonList(Month.JANUARY));
        when(periodService.getCurrentMonth()).thenReturn(1);

        // Act
        String result = userController.list(model, 1, 2024);

        // Assert
        assertEquals("users", result);
        verify(model, times(1)).addAttribute(eq("userDTO"), any(UserDTO.class));
        verify(model, times(1)).addAttribute(eq("periodDTO"), any(PeriodDTO.class));
        verify(model, times(1)).addAttribute(eq("months"), anyList());
        verify(model, times(1)).addAttribute(eq("currentMonth"), eq(1));
        verify(model, times(1)).addAttribute(eq("userService"), eq(userService));
    }

    @Test
    public void testAddUser() {
        // Arrange
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userRepository.save(any(User.class))).thenReturn(new User("John"));

        // Act
        String result = userController.addUser("John", redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User added successfully"));
    }

    @Test
    public void testEditUserForm() {
        // Arrange
        Model model = mock(Model.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("John")));

        // Act
        String result = userController.editUserForm(1L, model);

        // Assert
        assertEquals("editUser", result);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testEditUser() {
        // Arrange
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("John")));

        // Act
        String result = userController.editUser(1L, "NewName", null, redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User updated successfully"));
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = userController.deleteUser(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).deleteById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User deleted successfully"));
    }
    @Test
    public void testGetIncomeMapForPeriod() {
        // Arrange
        List<User> users = Collections.singletonList(new User("John")); // использование конструктора с параметром
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(7);
        double incomePerShift = 100.0;
        UserDTO userDTO = new UserDTO();

        // Mock the behavior of userService and periodService
        when(userService.getIncomeMapForPeriod(users, startDate, endDate, incomePerShift, userDTO))
                .thenReturn(Collections.singletonMap(new User("John"), 200.0));

        // Act
        Map<User, Double> result = userService.getIncomeMapForPeriod(users, startDate, endDate, incomePerShift, userDTO);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testCalculateIncome() {
        // Arrange
        User user = new User("John");
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(7);
        double incomePerShift = 100.0;
        UserDTO userDTO = new UserDTO();

        // Mock the behavior of userService
        when(userService.calculateIncome(user, startDate, endDate, incomePerShift, userDTO.getAdvancePaymentAmountMap())).thenReturn(200.0);

        // Act
        double result = userService.calculateIncome(user, startDate, endDate, incomePerShift, userDTO.getAdvancePaymentAmountMap());

        // Assert
        assertEquals(200.0, result);
    }

}

