package com.example.StaffCalc.controllers;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PeriodUtils;
import com.example.StaffCalc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private final PaymentRepository paymentRepository;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public List<UserDTO> list(Model model,
                       @RequestParam(value = "month", required = false) Integer month,
                       @RequestParam(value = "year", required = false) Integer year) {

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

        // фильтр по месяцам для рабочих дат
        for (UserDTO user : userDTOList) {
            List<LocalDate> filteredDates = user.getWorkingDates().stream()
                    .filter(date -> PeriodUtils.inPeriod(periodDTO, date))
                    .collect(Collectors.toList());
            user.setFilteredWorkingDates(filteredDates);
        }
        // фильтр для календаря чтобы не позволял выбрать чужие рабочие даты
        List<List<LocalDate>> takenDatesList = userDTOList.stream()
                .map(userDTO -> userDTOList.stream()
                        .filter(u -> !u.getId().equals(userDTO.getId()))
                        .flatMap(u -> u.getWorkingDates().stream())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        model.addAttribute("userDTOList", userDTOList);

        model.addAttribute("takenDatesList", takenDatesList);
        List<Month> monthsList = PeriodUtils.getMonthsList();
        int currentMonth = PeriodUtils.getCurrentMonth();
        model.addAttribute("selectedYear", resolvedYear);
        model.addAttribute("selectedMonth", resolvedMonth);
        model.addAttribute("periodDTO", periodDTO);
        model.addAttribute("months", monthsList);
        model.addAttribute("currentMonth", currentMonth);

        return userDTOList;
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<String> addUser(@RequestParam String name, RedirectAttributes redirectAttributes) {
        User newUser = new User(name);
        userRepository.save(newUser);

        redirectAttributes.addFlashAttribute("message", "User added successfully");
        return ResponseEntity.ok("Пользователь успешно добавлен");
    }



    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam(value = "workingDates", required = false) String workingDatesString,
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

        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return ResponseEntity.ok("Пользователь успешно обновлен");
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Пользователь успешно удален");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}
