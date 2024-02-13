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

    private final PaymentRepository paymentRepository;

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

        model.addAttribute("userDTOList", userDTOList);

        List<Month> monthsList = PeriodUtils.getMonthsList();
        int currentMonth = PeriodUtils.getCurrentMonth();

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

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        List<Payment> mainPayments = paymentRepository.findByUserAndType(user, Payment.PaymentType.MAIN_PAYMENT);
        List<Payment> advancePayments = paymentRepository.findByUserAndType(user, Payment.PaymentType.ADVANCE_PAYMENT);

        // Add payments to the model
        model.addAttribute("mainPayments", mainPayments);
        model.addAttribute("advancePayments", advancePayments);
        return "editUser";
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam(value = "workingDates", required = false) String workingDatesString,
                           @RequestParam(value = "mainPaymentsAmount", required = false) String[] mainPaymentsAmount,
                           @RequestParam(value = "mainPaymentsDates", required = false) String[] mainPaymentsDates,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setName(name);

        if (workingDatesString != null && !workingDatesString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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

        userService.updatePaymentsForUser(user, mainPaymentsAmount, mainPaymentsDates);

        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/users";
    }




    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/users";
    }

    @Transactional
    @PostMapping("/removeAllDates/{id}")
    public String removeAllDates(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.removeAllDates(id);
            redirectAttributes.addFlashAttribute("message", "All dates removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing dates: " + e.getMessage());
        }
        return "redirect:/users";
    }



}
