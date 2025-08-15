package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String youtubeLink;
    private Long price;
    private String fuelType;
    private LocalDate productionYear;
    private String engineSize;
    private int seat;
    private int door;
    private Long warranty;
    private String transmission;
    private String driveType;
    private String color;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "car_type_id",nullable = true)
    private CarType carType;
}