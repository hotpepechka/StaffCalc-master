package com.example.StaffCalc.controllers;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.PaymentRepository;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.PaymentService;
//TODO не используемый импорт
import com.example.StaffCalc.service.UserService;
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


    //TODO Метод создания - метод создания по REST практикам это POST запрос, PUT запрос это метод изменения
    //Так же не корректное использование в пути {id} - оно подразумевает в данном случае id платежа - а у тебя это id пользователя
    //Корректнее будет сделать в данном случае POST /users/{id}/payments или POST /payments и userId унести в @RequestParam
    @Operation(summary = "Add a payment", description = "Add a new payment for the specified user.")
    @ApiResponse(responseCode = "200", description = "Payment added successfully",
            content = @Content(mediaType = "text/plain"))
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
            //TODO вижу есть вариант ошибки с некорретной суммой - стоит так же учитывать защиты от дурака - например что бы не могли вносить сумму < 1
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format, payment type, or amount");
        }
    }

    //TODO не нужен префикс /delete - у тебя разделяются операции по типу операции GET, POST, PUT, DELETE
    @Operation(summary = "Delete a payment", description = "Delete a payment with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Payment deleted successfully",
            content = @Content(mediaType = "text/plain"))
    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        paymentRepository.deleteById(paymentId);
        return ResponseEntity.ok("Payment deleted successfully");
    }

}
