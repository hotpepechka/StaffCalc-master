package com.example.StaffCalc.service;

import com.example.StaffCalc.dto.PeriodDTO;
import com.example.StaffCalc.dto.UserDTO;
import com.example.StaffCalc.dto.WorkingPeriodDTO;
import com.example.StaffCalc.mapper.UserConverter;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.models.WorkingPeriod;
import com.example.StaffCalc.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class PeriodService {

    private final UserRepository userRepository;

    public List<UserDTO> getUsersWithPeriods(PeriodDTO periodDTO) {
        int year = periodDTO.getStartYear();
        int month = periodDTO.getStartMonth();

        // Получить пользователей с рабочими периодами
        List<User> usersWithPeriods = userRepository.findUsersWithWorkingPeriods(year, month);

        // Преобразовать пользователей в DTO
        List<UserDTO> userDTOList = UserConverter.convertToUserDTOList(usersWithPeriods, periodDTO);

        // Для каждого пользователя:
        userDTOList.forEach(userDTO -> {
            // Получить нужный working period
            WorkingPeriodDTO workingPeriod = userDTO.getWorkingPeriods()
                    .stream()
                    .filter(wp -> wp.getYear() == year && wp.getMonth() == month)
                    .findFirst()
                    .orElse(new WorkingPeriodDTO(year, month, new HashSet<>()));

            // Заполнить рабочие даты
            userDTO.setWorkingPeriodDates(new HashSet<>(workingPeriod.getWorkingDates()));
        });

        return userDTOList;
    }
    public void fillTakenDates(List<UserDTO> userDTOList) {
        userDTOList.forEach(userDTO -> {
            List<LocalDate> takenDates = userDTOList.stream()
                    .filter(otherUser -> !otherUser.getId().equals(userDTO.getId()))
                    .flatMap(otherUser -> otherUser.getWorkingPeriodDates().stream())
                    .distinct()
                    .collect(Collectors.toList());

            userDTO.setTakenDates(takenDates);
        });
    }

    public void updateWorkingDates(User user, Set<LocalDate> parsedDates) {
        int year;
        int month;

        LocalDate firstDate = parsedDates.iterator().next();
        // Определение year и month в соответствии с логикой начала с 15 числа предыдущего месяца
        if (firstDate.getDayOfMonth() < 15) {
            year = firstDate.getYear();
            month = firstDate.getMonthValue() - 1;
            if (month == 0) {
                month = 12;
                year--;
            }
        } else {
            year = firstDate.getYear();
            month = firstDate.getMonthValue();
        }

        // Ищем существующий период для заданного месяца и года
        WorkingPeriod existingPeriod = findPeriodForMonthAndYear(user, year, month);

        // Обновление рабочих дат в существующем периоде или создание нового периода
        updateWorkingDates(existingPeriod, user, year, month, parsedDates);
    }

    private WorkingPeriod findPeriodForMonthAndYear(User user, int year, int month) {
        return user.getWorkingPeriods().stream()
                .filter(period -> period.getYear() == year && period.getMonth() == month)
                .findFirst()
                .orElse(null);
    }

    private void updateWorkingDates(WorkingPeriod existingPeriod, User user, int year, int month, Set<LocalDate> parsedDates) {
        if (existingPeriod != null) {
            // Если период для месяца существует, обновляем даты в этом периоде
            clearAndAddWorkingDates(existingPeriod, parsedDates);
        } else {
            // Если период для месяца не существует, создаем новый период и добавляем в него даты
            WorkingPeriod newPeriod = createNewWorkingPeriod(user, year, month, parsedDates);

            // Добавляем новый период к списку рабочих периодов пользователя
            user.getWorkingPeriods().add(newPeriod);
        }
    }

    private WorkingPeriod createNewWorkingPeriod(User user, int year, int month, Set<LocalDate> parsedDates) {
        WorkingPeriod newPeriod = new WorkingPeriod(user, year, month);

        // Устанавливаем начало и конец периода с 15 числа предыдущего месяца по 14 число текущего месяца
        newPeriod.setStartDate(LocalDate.of(year, month, 15).minusMonths(1));
        newPeriod.setEndDate(LocalDate.of(year, month, 14));

        // Добавляем даты в новый период
        newPeriod.getWorkingDates().addAll(parsedDates);

        return newPeriod;
    }

    private void clearAndAddWorkingDates(WorkingPeriod workingPeriod, Set<LocalDate> parsedDates) {
        // Очищаем существующие рабочие даты и добавляем новые
        workingPeriod.getWorkingDates().clear();
        workingPeriod.getWorkingDates().addAll(parsedDates);
    }


}
