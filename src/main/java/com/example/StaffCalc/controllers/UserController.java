package com.example.StaffCalc.controllers;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodUtils;
import com.example.StaffCalc.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import com.example.StaffCalc.dto.UserDTO;
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    private PeriodUtils periodUtils;
    private final PaymentRepository paymentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) Integer month,
                       @RequestParam(required = false) Integer year) {

        int defaultMonth = LocalDate.now().getMonthValue();
        int defaultYear = LocalDate.now().getYear();

        int resolvedMonth = (month != null) ? month : defaultMonth;
        int resolvedYear = (year != null) ? year : defaultYear;

        if (resolvedMonth < 1 || resolvedMonth > 12) {
            throw new IllegalArgumentException("Not corrected month value");
        }
        if (resolvedYear < 2024) {
            throw new IllegalArgumentException("Not corrected year value");
        }

        PeriodDTO periodDTO = PeriodUtils.getPeriodForCalculateIncome(resolvedMonth, resolvedYear);
        List<UserDTO> userDTOList = userService.getUsers(periodDTO);


        for (UserDTO user : userDTOList) {
            List<LocalDate> filteredDates = user.getWorkingDates().stream()
                    .filter(date -> periodUtils.inPeriod(periodDTO, date))
                    .collect(Collectors.toList());
            user.setFilteredWorkingDates(filteredDates);
        }

        model.addAttribute("userDTOList", userDTOList);

        List<Month> monthsList = PeriodUtils.getMonthsList();
        int currentMonth = PeriodUtils.getCurrentMonth();
        model.addAttribute("selectedYear", resolvedYear);
        model.addAttribute("selectedMonth", resolvedMonth);
        model.addAttribute("periodDTO", periodDTO);
        model.addAttribute("months", monthsList);
        model.addAttribute("currentMonth", currentMonth);

        return "users";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String name, RedirectAttributes redirectAttributes) {
        User newUser = new User(name);
        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("message", "User added successfully");
        return "redirect:/users";
    }



    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam(value = "workingDates", required = false) String workingDatesString,
                           @RequestParam(value = "newPaymentDate", required = false) String newPaymentDate,
                           @RequestParam(value = "newPaymentType", required = false) String newPaymentType,
                           @RequestParam(value = "newPaymentAmount", required = false) String newPaymentAmount,
                           @RequestParam(value = "deletePaymentId", required = false) Long deletePaymentId,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setName(name);

        // Обработка рабочих дат
        if (workingDatesString != null && !workingDatesString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Set<LocalDate> parsedDates = Arrays.stream(workingDatesString.split(","))
                    .map(date -> {
                        try {
                            return LocalDate.parse(date.trim(), formatter);
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException("Invalid date format: " + date);
                        }
                    })
                    .collect(Collectors.toSet());

            user.setWorkingDates(parsedDates);
        } else {
            user.setWorkingDates(Collections.emptySet());
        }

        userRepository.save(user);

        // Обработка новой выплаты
        if (newPaymentDate != null && newPaymentType != null && newPaymentAmount != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(newPaymentDate, formatter);
                Payment.PaymentType paymentType = Payment.PaymentType.valueOf(newPaymentType);
                Double amount = Double.parseDouble(newPaymentAmount);
                userService.addNewPayment(user, date, paymentType, amount);
            } catch (DateTimeParseException | IllegalArgumentException e) {
                // Обработка ошибок
                redirectAttributes.addFlashAttribute("error", "Неверный формат даты, типа выплаты или суммы");
                return "redirect:/users";
            }
        }

        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/users";
    }
    private Set<LocalDate> filteredWorkingDates(Set<LocalDate> workingDates, LocalDate startDate, LocalDate endDate) {
        return workingDates.stream()
                .filter(date -> date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1)))
                .collect(Collectors.toSet());
    }
}
