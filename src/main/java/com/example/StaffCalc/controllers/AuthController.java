package com.example.StaffCalc.controllers;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final String[] russianMonths = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    @Autowired
    public AuthController( UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/list")
    public String list(Model model, @RequestParam(required = false, defaultValue = "1") int month, @RequestParam(required = false, defaultValue = "2024") int year, @RequestParam(required = false) Integer selectedMonth,
                       @RequestParam(required = false) Integer selectedYear) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        LocalDate startDate = LocalDate.of(year, month, 15);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Month> monthsList = Arrays.asList(Month.values());

        Map<User, Double> incomeMap = new HashMap<>();
        for (User user : users){
            double income = user.getIncomeForPeriod(startDate, endDate, incomePerShift);
            incomeMap.put(user,income);

        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("incomePerShift", incomePerShift);
        model.addAttribute("selectedMonth", currentMonth);
        model.addAttribute("selectedYear", currentYear);
        model.addAttribute("months", monthsList);
        model.addAttribute("russianMonths", russianMonths);
        model.addAttribute("incomeMap", incomeMap);

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
                    .filter(date -> {
                        try {
                            LocalDate.parse(date, formatter);
                            return true;
                        } catch (DateTimeParseException e) {
                            return false;
                        }
                    })
                    .map(date -> LocalDate.parse(date, formatter))
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
