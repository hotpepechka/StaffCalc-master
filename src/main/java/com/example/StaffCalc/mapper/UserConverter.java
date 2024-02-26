package com.example.StaffCalc.mapper;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {
    public static UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setWorkingDates(user.getWorkingDates());

        return userDTO;
    }

    public static List<UserDTO> convertToUserDTOList(List<User> users) {
        return users.stream()
                .map(UserConverter::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public static List<PaymentDTO> convertToPaymentDTOList(List<Payment> payments) {
        return payments.stream()
                .map(payment -> new PaymentDTO(payment.getId(), payment.getType(), payment.getPaymentDate(), payment.getAmount()))
                .collect(Collectors.toList());
    }
}
