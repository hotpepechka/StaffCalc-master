package com.example.StaffCalc.models;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;



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


    public User(String name) {
        this.name = name;
    }

    // Getter and setter methods


}
