package com.example.StaffCalc.controllers;

import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PaymentService;
import com.example.StaffCalc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final UserService userService;

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;


    @PutMapping("/{id}")
    public ResponseEntity<String> addPayment(@PathVariable Long id,
                                             @RequestParam String newPaymentDate,
                                             @RequestParam String newPaymentType,
                                             @RequestParam String newPaymentAmount) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(newPaymentDate, formatter);
            Payment.PaymentType paymentType = Payment.PaymentType.valueOf(newPaymentType);
            Double amount = Double.parseDouble(newPaymentAmount);
            paymentService.addNewPayment(user, date, paymentType, amount);
            return ResponseEntity.ok("Payment added successfully");
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format, payment type, or amount");
        }
    }


    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        paymentRepository.deleteById(paymentId);
        return ResponseEntity.ok("Payment deleted successfully");
    }

}
