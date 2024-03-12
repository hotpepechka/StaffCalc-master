package com.example.StaffCalc.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "working_period")
public class WorkingPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int year;

    private int month;

    private LocalDate startDate;
    private LocalDate endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "working_period_dates", joinColumns = @JoinColumn(name = "working_period_id"))
    @Column(name = "working_date")
    private Set<LocalDate> workingDates = new HashSet<>();

    public WorkingPeriod(User user, int year, int month) {
        this.user = user;
        this.year = year;
        this.month = month;
    }

}