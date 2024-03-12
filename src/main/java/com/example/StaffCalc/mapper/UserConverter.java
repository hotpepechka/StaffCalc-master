package com.example.StaffCalc.mapper;
import com.example.StaffCalc.dto.PaymentDTO;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.dto.WorkingPeriodDTO;
import com.example.StaffCalc.models.Payment;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.models.WorkingPeriod;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserConverter {
    public static UserDTO convertToUserDTO(User user, PeriodDTO periodDTO) {
        // Создаем объект UserDTO
        UserDTO userDTO = new UserDTO();

        // Копируем базовые данные пользователя
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());

        // Разделяем рабочие периоды на соответствующие и нет
        Map<Boolean, List<WorkingPeriod>> partitionedPeriods = user.getWorkingPeriods().stream()
                .collect(Collectors.partitioningBy(
                        workingPeriod -> workingPeriod.getYear() == periodDTO.getStartYear()
                                && workingPeriod.getMonth() == periodDTO.getStartMonth()
                ));

        // Извлекаем рабочие даты из периодов, соответствующих заданному году и месяцу
        Set<LocalDate> workingPeriodDates = partitionedPeriods.getOrDefault(true, Collections.emptyList())
                .stream()
                .flatMap(workingPeriod -> workingPeriod.getWorkingDates().stream())
                .collect(Collectors.toSet());

        // Записываем рабочие даты в объект UserDTO
        userDTO.setWorkingPeriodDates(workingPeriodDates);

        // Преобразуем рабочие периоды в список WorkingPeriodDTO
        List<WorkingPeriodDTO> workingPeriods = partitionedPeriods.getOrDefault(true, Collections.emptyList())
                .stream()
                .map(workingPeriod -> new WorkingPeriodDTO(
                        workingPeriod.getYear(),
                        workingPeriod.getMonth(),
                        new HashSet<>(workingPeriod.getWorkingDates())))
                .collect(Collectors.toList());

        // Добавляем пустой период, если список пустой
        if (workingPeriods.isEmpty()) {
            workingPeriods.add(new WorkingPeriodDTO(periodDTO.getStartYear(), periodDTO.getStartMonth(), new HashSet<>()));
        }

        // Записываем список WorkingPeriodDTO в объект UserDTO
        userDTO.setWorkingPeriods(workingPeriods);

        // Преобразуем список платежей и записываем в UserDTO
        userDTO.setPayments(convertToPaymentDTOList(user.getPayments()));

        // Возвращаем готовый UserDTO
        return userDTO;
    }

    public static List<UserDTO> convertToUserDTOList(List<User> users, PeriodDTO periodDTO) {
        return users.stream()
                .map(user -> convertToUserDTO(user, periodDTO))
                .collect(Collectors.toList());
    }

    public static List<PaymentDTO> convertToPaymentDTOList(List<Payment> payments) {
        return payments.stream()
                .map(payment -> new PaymentDTO(payment.getId(), payment.getType(), payment.getPaymentDate(), payment.getAmount()))
                .collect(Collectors.toList());
    }
}

