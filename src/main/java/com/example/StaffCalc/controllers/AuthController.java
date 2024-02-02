package com.example.StaffCalc.controllers;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodService;
import com.example.StaffCalc.service.UserService;
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
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PeriodService periodService;

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");



    @Autowired
    public AuthController( UserRepository userRepository, UserService userService, PeriodService periodService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.periodService = periodService;
    }


    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(required = false, defaultValue = "1") int month,
                       @RequestParam(required = false, defaultValue = "2024") int year) {

        PeriodDTO periodDTO = periodService.getPeriodData(month, year);
        UserDTO userDTO = userService.getUserData(periodDTO);


        // Get other values from the PeriodService
        List<Month> monthsList = periodService.getMonthsList();
        int currentMonth = periodService.getCurrentMonth();

        model.addAttribute("periodDTO", periodDTO);
        model.addAttribute("userDTO", userDTO);

        model.addAttribute("months", monthsList);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("userService", userService);
        return "users";
    }



    @PostMapping("/addUser")
    public String addUser(@RequestParam String name, RedirectAttributes redirectAttributes) {
        User newUser = new User(name);
        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("message", "User added successfully");
        return "redirect:/list";
    }

    @GetMapping("/editUser/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam(value = "workingDates", required = false) List<String> workingDates,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setName(name);

        if (workingDates != null) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Set<LocalDate> parsedDates = workingDates.stream()
                    .filter(date -> !date.isEmpty()) // Исключаем пустые строки
                    .flatMap(date -> Arrays.stream(date.split(", ")))
                    .map(date -> {
                        try {
                            return LocalDate.parse(date, formatter);
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException("Invalid date format: " + date);
                        }
                    })
                    .collect(Collectors.toSet());

            user.setWorkingDates(parsedDates);
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/list";
    }


    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/list";
    }





}
