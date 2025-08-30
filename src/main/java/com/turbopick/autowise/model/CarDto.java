package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    @NotEmpty(message = "Car name cannot be empty")
    private String name;

    @NotEmpty(message = "YouTube link cannot be empty")
    private String youtubeLink;

    @NotNull(message = "Price is required")
    private Long price;

    @NotEmpty(message = "Fuel type cannot be empty")
    private String fuelType;


    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate productionYear;

    @NotEmpty(message = "Engine size cannot be empty")
    private String engineSize;

    @NotNull(message = "Seat count is required")
    private int seat;

    @NotNull(message = "Door count is required")
    private int door;

    @NotNull(message = "Warranty is required")
    private Long warranty;

    @NotEmpty(message = "Transmission cannot be empty")
    private String transmission;

    @NotEmpty(message = "Drive type cannot be empty")
    private String driveType;

    @NotNull(message = "Color ID is required")
    private String color;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotEmpty(message = "Car Type Id cannot be empty")
    private String carTypeId;

    private List<Long> featureIds;

}
