package com.example.StaffCalc.Controllers;
import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.controllers.UserController;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodUtils;
import com.example.StaffCalc.service.UserService;
import com.example.StaffCalc.service.calculate.BaseCalculate;
import com.example.StaffCalc.service.calculate.Calculate;
import com.example.StaffCalc.service.calculate.PercentageCalculate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Autowired
    private Calculate calculate;

    @Mock
    private PeriodUtils periodUtils;

    @Mock
    private PaymentRepository paymentRepository;



    @InjectMocks
    private UserController userController;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    protected CalculateProperties calculateProperties;

    private MockMvc mockMvc;
    @Test
    public void testList() {
        // Mocking parameters
        Model model = Mockito.mock(Model.class);
        Integer month = 1;
        Integer year = 2024;

        List<UserDTO> mockUserDTOList = Collections.singletonList(new UserDTO(/* add necessary constructor parameters */));
        when(userService.getUsers(any(PeriodDTO.class))).thenReturn(mockUserDTOList);

        String result = userController.list(model, month, year);

        assertEquals("users", result);

        Mockito.verify(model).addAttribute("userDTOList", mockUserDTOList);

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
    public void testEditUser() {
        // Mocking parameters
        Long userId = 1L;
        String name = "NewName";
        String workingDatesString = "01-01-2024, 02-01-2024";
        String[] mainPaymentsAmount = {"100", "200"};
        String[] mainPaymentsDates = {"01-01-2024", "02-01-2024"};
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        User existingUser = new User();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        String result = userController.editUser(userId, name, workingDatesString, mainPaymentsAmount, mainPaymentsDates, redirectAttributes);

        assertEquals("redirect:/users", result);

        Mockito.verify(userRepository).save(existingUser);

        Mockito.verify(userService).updatePaymentsForUser(existingUser, mainPaymentsAmount, mainPaymentsDates);

        Mockito.verify(redirectAttributes).addFlashAttribute("message", "User updated successfully");
    }

    @Test
    void testDeleteUser() {

        String result = userController.deleteUser(1L, redirectAttributes);

        assertEquals("redirect:/users", result);
        verify(userRepository, times(1)).deleteById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("User deleted successfully"));
    }

}

