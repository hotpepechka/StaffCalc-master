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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public AuthController( UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/list")
    public String list(Model model, @RequestParam(required = false, defaultValue = "1") int month, @RequestParam(required = false, defaultValue = "2024") int year) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        LocalDate startDate = LocalDate.of(year, month, 15);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        //double incomePerShift = 50;

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("incomePerShift", incomePerShift);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);

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
                         //  @RequestParam(value = "removeDate", required = false) String removeDate,
                         //  @RequestParam(value = "removeAllDates", required = false) boolean removeAllDates,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));


       /* if (removeAllDates) {
            user.getWorkingDates().clear();
        } else if (removeDate != null) {
            LocalDate dateToRemove = LocalDate.parse(removeDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            user.getWorkingDates().remove(dateToRemove);
        } else {*/
            user.setName(name);

            if (workingDates != null) {
                Set<LocalDate> parsedDates = workingDates.stream()
                        .map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .collect(Collectors.toSet());

                user.setWorkingDates(parsedDates);
            }
       // }

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
