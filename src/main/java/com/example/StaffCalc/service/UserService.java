package com.example.StaffCalc.service;
import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.mapper.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PeriodService periodService;  // Inject PeriodService

    @Value("${myapp.incomePerShift}")
    private double incomePerShift;

    @Value("${myapp.advancePaymentPercentage}")
    private double advancePaymentPercentage;

    private double advancePaymentAmount;
    @Autowired
    public UserService(UserRepository userRepository, PeriodService periodService) {
        this.userRepository = userRepository;
        this.periodService = periodService;
    }

    public UserDTO getUserData(PeriodDTO periodDTO) {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(users);
        UserDTO userDTO = new UserDTO();

        Map<User, Double> incomeMap = getIncomeMapForPeriod(users, periodDTO.getStartDate(), periodDTO.getEndDate(), incomePerShift, userDTO);

        userDTO.setUsers(userDTOList);
        userDTO.setIncomeMap(incomeMap);

        // Set values from PeriodService
        userDTO.setMonthsList(periodService.getMonthsList());
        userDTO.setCurrentMonth(periodService.getCurrentMonth());
        userDTO.setDefaultSelectedYear(periodService.getDefaultYear());
        return userDTO;
    }


    // UserService.java
    public Map<User, Double> getIncomeMapForPeriod(List<User> users, LocalDate startDate, LocalDate endDate, double incomePerShift, UserDTO userDTO) {
        Map<User, Double> incomeMap = new HashMap<>();
        Map<User, Double> advancePaymentAmountMap = new HashMap<>();

        for (User user : users) {
            double incomeForUser = calculateIncome(user, startDate, endDate, incomePerShift, advancePaymentAmountMap);

            incomeMap.put(user, incomeForUser);
        }
        userDTO.setAdvancePaymentAmountMap(advancePaymentAmountMap);

        return incomeMap;
    }

    public double calculateIncome(User user, LocalDate startDate, LocalDate endDate, double incomePerShift, Map<User, Double> advancePaymentAmountMap) {
        // Настройка даты начала и окончания периода
        LocalDate adjustedStartDate = startDate.minusMonths(1).withDayOfMonth(15);
        LocalDate adjustedEndDate = endDate.minusMonths(1).withDayOfMonth(14);

        // Фильтрация рабочих дат в пределах указанного периода
        long numberOfShifts = user.getWorkingDates().stream()
                .filter(date -> date.isAfter(adjustedStartDate.minusDays(1)) && date.isBefore(adjustedEndDate.plusDays(1)))
                .count();

        // Рассчет суммы заработка
        double totalIncome = numberOfShifts * incomePerShift;

        // Рассчет суммы аванса и основного платежа
        double advancePaymentAmount = totalIncome * advancePaymentPercentage / 100.0;

        // Устанавливаем суммы в карту advancePaymentAmountMap для каждого пользователя
        advancePaymentAmountMap.put(user, advancePaymentAmount);

        // Возвращаем общую сумму (заработную плату минус аванс)
        return totalIncome - advancePaymentAmount;
    }
    public Set<LocalDate> getWorkingDatesForMonthAndYear(User user, int month, int year) {
        return user.getWorkingDates().stream()
                .filter(date -> date.getMonthValue() == month && date.getYear() == year)
                .collect(Collectors.toSet());
    }
}
