package com.example.StaffCalc.Controllers;
import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.controllers.UserController;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodUtils;
import com.example.StaffCalc.service.UserService;
import com.example.StaffCalc.service.calculate.BaseCalculate;
import com.example.StaffCalc.service.calculate.Calculate;
import com.example.StaffCalc.service.calculate.PercentageCalculate;
import jakarta.servlet.http.HttpServletRequest;
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
import java.time.format.DateTimeFormatter;
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

    @Mock
    private HttpServletRequest request;



    @Test
    void testCalculateIncome() {
        Set<LocalDate> workingDates = Set.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2));

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        PeriodDTO periodDTO = new PeriodDTO(startDate, endDate);

        CalculateProperties calculateProperties = mock(CalculateProperties.class);
        when(calculateProperties.getIncomePerShift()).thenReturn(50.0);

        BaseCalculate baseCalculate = new BaseCalculate(calculateProperties) {
            @Override
            public double calculateAdvancePayment(Double income) {
                return 0;
            }
            @Override
            public void updatePaymentsForUser(User user) {
            }
        };
        double result = baseCalculate.calculateIncome(workingDates, periodDTO);
        assertEquals(100.0, result);
    }

    @Test
    void testCalculateAdvancePayment() {
        CalculateProperties calculateProperties = mock(CalculateProperties.class);
        when(calculateProperties.getAdvancePaymentPercentage()).thenReturn(10.0);
        PercentageCalculate percentageCalculate = new PercentageCalculate(calculateProperties);
        Double income = 500.0;
        double result = percentageCalculate.calculateAdvancePayment(income);
        assertEquals(50.0, result);
    }



}

