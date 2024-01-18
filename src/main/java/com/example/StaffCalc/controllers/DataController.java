/*package com.example.StaffCalc.controllers;

import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DataController {

    private final UserRepository userRepository;

    @Autowired
    public DataController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setName(name);
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
*/