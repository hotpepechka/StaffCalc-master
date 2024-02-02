package com.example.StaffCalc.models;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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


    @Value("${myapp.advancePaymentPercentage}")
    private double advancePaymentPercentage;

    @org.springframework.beans.factory.annotation.Value("${myapp.incomePerShift}")
    private double incomePerShift;


    private double advancePaymentAmount;
    public User(String name) {
        this.name = name;

    }




}


