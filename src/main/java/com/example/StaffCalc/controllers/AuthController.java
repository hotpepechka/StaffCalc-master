package com.example.StaffCalc.controllers;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @Autowired
    public AuthController( UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }




    @GetMapping("/list")
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
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
                           @RequestParam(value = "removeDate", required = false) String removeDate,
                           @RequestParam(value = "removeAllDates", required = false) boolean removeAllDates,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        if (removeAllDates) {
            // Remove all dates if the parameter is present
            user.getWorkingDates().clear();
        } else if (removeDate != null) {
            // Remove the specified date from workingDates
            LocalDate dateToRemove = LocalDate.parse(removeDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            user.getWorkingDates().remove(dateToRemove);
        } else {
            // Update user details if not removing a date or all dates
            user.setName(name);

            if (workingDates != null) {
                Set<LocalDate> parsedDates = workingDates.stream()
                        .map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .collect(Collectors.toSet());
                user.setWorkingDates(parsedDates);
            }
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/list";
    }




}
