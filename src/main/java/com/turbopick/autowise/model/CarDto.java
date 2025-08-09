package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotNull(message = "Production year is required")
    private Long productionYear;

    @NotEmpty(message = "Engine size cannot be empty")
    private String engineSize;

    @NotNull(message = "Seat count is required")
    private Long seat;

    @NotNull(message = "Door count is required")
    private Long door;

    @NotNull(message = "Warranty is required")
    private Long warranty;

    @NotEmpty(message = "Transmission cannot be empty")
    private String transmission;

    @NotEmpty(message = "Drive type cannot be empty")
    private String driveType;

    @NotNull(message = "Color ID is required")
    private Long color;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

}
