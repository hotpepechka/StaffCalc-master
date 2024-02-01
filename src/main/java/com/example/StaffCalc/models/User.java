package com.example.StaffCalc.models;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "user_working_dates", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "working_date")
    private Set<LocalDate> workingDates = new HashSet<>();

    private double advancePaymentAmount;
    private double mainPaymentAmount;
    private double advancePaymentPercentage = 25; // Процент авансового платежа
    private double incomePerShift = 1800;


    public User(String name) {
        this.name = name;
    }

    public double calculateIncome(LocalDate startDate, LocalDate endDate, double incomePerShift) {
        long numberOfShifts = workingDates.stream()
                .filter(date -> date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1)))
                .count();

        LocalDate previousMonthStartDate = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 15).minusMonths(1);
        LocalDate previousMonthEndDate = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 14);

        long numberOfShiftsPreviousMonth = workingDates.stream()
                .filter(date -> date.isAfter(previousMonthStartDate.minusDays(1)) && date.isBefore(previousMonthEndDate.plusDays(1)))
                .count();

        double totalIncome = numberOfShifts * incomePerShift;

        // Рассчитываем суммы аванса и основного платежа
        advancePaymentAmount = totalIncome * advancePaymentPercentage / 100.0;
        mainPaymentAmount = numberOfShiftsPreviousMonth * incomePerShift - advancePaymentAmount;

        // Возвращаем общую сумму (заработную плату минус аванс)
        return totalIncome - advancePaymentAmount;
    }

    public double getIncomeForPeriod(LocalDate startDate, LocalDate endDate, double incomePerShift){
        return calculateIncome(startDate, endDate, incomePerShift);
    }
    public Set<LocalDate> getWorkingDatesForMonthAndYear(int month, int year) {
        return workingDates.stream()
                .filter(date -> date.getMonthValue() == month && date.getYear() == year)
                .collect(Collectors.toSet());
    }


}


