package com.example.StaffCalc.dto;

import com.example.StaffCalc.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {


    private Long id;
    private String name;
    private Set<LocalDate> workingDates;
    private double income;
    private double advancePaymentAmount;


    private List<PaymentDTO> payments;
    private double totalPayments;
    private double MainPayments;
    private double AdvancePayments;


    private User user;

    public User getUser() {
        if (user == null) {
            user = new User();
            user.setId(id);
            user.setName(name);
        }
        return user;
    }

}