package com.example.StaffCalc.Controllers;
import com.example.StaffCalc.config.CalculateProperties;
import com.example.StaffCalc.controllers.PaymentController;
import com.example.StaffCalc.controllers.UserController;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PaymentService;
import com.example.StaffCalc.service.UserService;
import com.example.StaffCalc.service.calculate.BaseCalculate;
import com.example.StaffCalc.service.calculate.PercentageCalculate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentController paymentController;

    @InjectMocks
    private UserController userController;

    @Mock
    private PaymentService paymentService;


    @Test
    void testList() {
        Model model = mock(Model.class);
        when(userService.getUsers(any())).thenReturn(Collections.emptyList());

        List<UserDTO> userDTOList = userController.list(model, 1, 2024);

        assertEquals(0, userDTOList.size());
    }

    @Test
    void testAddUser() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userRepository.save(any())).thenReturn(new User("John"));

        ResponseEntity<String> responseEntity = userController.addUser("John", redirectAttributes);

        assertEquals(ResponseEntity.ok("Пользователь успешно добавлен"), responseEntity);
    }

    @Test
    void testEditUser() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(new User("John")));

        ResponseEntity<String> responseEntity = userController.editUser(1L, "John", "2024-01-01", redirectAttributes);

        assertEquals(ResponseEntity.ok("Пользователь успешно обновлен"), responseEntity);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(any());

        ResponseEntity<String> responseEntity = userController.deleteUser(1L);

        assertEquals(ResponseEntity.ok("Пользователь успешно удален"), responseEntity);
    }

    @Test
    void testAddPayment() {
        User mockUser = new User("John");
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(mockUser));

        LocalDate paymentDate = LocalDate.parse("2024-01-01");
        Payment.PaymentType paymentType = Payment.PaymentType.MAIN_PAYMENT;
        double paymentAmount = 2000.0;

        ResponseEntity<String> responseEntity = paymentController.addPayment(1L, "2024-01-01", "MAIN_PAYMENT", "2000");

        assertEquals(ResponseEntity.ok("Payment added successfully"), responseEntity);

        verify(paymentService, times(1)).addNewPayment(eq(mockUser), eq(paymentDate), eq(paymentType), eq(paymentAmount));
    }

    @Test
    void testDeletePayment() {
        Long paymentId = 1L;

        ResponseEntity<String> responseEntity = paymentController.deletePayment(paymentId);

        assertEquals(ResponseEntity.ok("Payment deleted successfully"), responseEntity);

        verify(paymentRepository, times(1)).deleteById(eq(paymentId));
    }

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

