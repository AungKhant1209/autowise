package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "car")
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

    @Column(length = 4000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_type_id")
    private CarType carType;

    // ✅ NEW: Brand relation
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private CarBrand carBrand;
}
