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
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PeriodService periodService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PeriodService periodService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.periodService = periodService;
    }


    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) Integer month,
                       @RequestParam(required = false) Integer year) {

        int defaultMonth = LocalDate.now().getMonthValue();
        int defaultYear = LocalDate.now().getYear();

        // Use provided values or defaults if not provided
        int resolvedMonth = (month != null) ? month : defaultMonth;
        int resolvedYear = (year != null) ? year : defaultYear;

        PeriodDTO periodDTO = periodService.getPeriodData(resolvedMonth, resolvedYear);
        UserDTO userDTO = userService.getUserData(periodDTO);

        model.addAttribute("userDTO", userDTO);

        List<Month> monthsList = periodService.getMonthsList();
        int currentMonth = periodService.getCurrentMonth();

        model.addAttribute("periodDTO", periodDTO);
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
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "editUser";
    }

        @PostMapping("/edit/{id}")
        public String editUser(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(value = "workingDates", required = false) String workingDatesString,
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
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/users";
        }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/users";
    }





}
