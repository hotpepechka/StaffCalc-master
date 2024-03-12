package com.example.StaffCalc.controllers;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;


    @Operation(summary = "Add a payment", description = "Add a new payment for the specified user.")
    @ApiResponse(responseCode = "200", description = "Payment added successfully",
            content = @Content(mediaType = "text/plain"))
    @PostMapping("/{userId}/payments")
    public ResponseEntity<String> addPayment(@PathVariable Long userId,
                                             @RequestParam String newPaymentDate,
                                             @RequestParam String newPaymentType,
                                             @RequestParam String newPaymentAmount) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(newPaymentDate, formatter);
            Payment.PaymentType paymentType = Payment.PaymentType.valueOf(newPaymentType);
            double amount = Double.parseDouble(newPaymentAmount);
            if (amount < 1) {throw new IllegalArgumentException("Amount must be greater than or equal to 1");}
            paymentService.addNewPayment(user, date, paymentType, amount);
            return ResponseEntity.ok("Payment added successfully");
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format, payment type, or amount");
        }
    }

    @Operation(summary = "Delete a payment", description = "Delete a payment with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Payment deleted successfully",
            content = @Content(mediaType = "text/plain"))
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        paymentRepository.deleteById(paymentId);
        return ResponseEntity.ok("Payment deleted successfully");
    }

}