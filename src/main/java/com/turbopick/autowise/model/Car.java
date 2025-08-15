package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String youtubeLink;
    private Long price;
    private String fuelType;
    private Date productionYear;
    private String engineSize;
    private int seat;
    private int door;
    private Long warranty;
    private String transmission;
    private String driveType;
    private String color;
    private String description;
}