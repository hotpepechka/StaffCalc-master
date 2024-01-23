package com.example.StaffCalc.models;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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


    public User(String name) {
        this.name = name;
    }

    public double calculateIncome(LocalDate startDate, LocalDate endDate, double incomePerShift){
        long numberOfShift = workingDates.stream()
                .filter(date -> date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1)))
                .count();

        return numberOfShift * incomePerShift;
    }



}
