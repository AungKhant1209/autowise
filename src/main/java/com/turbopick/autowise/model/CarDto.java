package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
@Data
public class CarDto {

    @NotBlank
    private String name;

    private String youtubeLink;

    @NotNull
    private Long price;

    private String fuelType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionYear;

    private String engineSize;

    private Integer seat;
    private Integer door;

    private Long warranty;

    private String transmission;
    private String driveType;
    private String color;

    private String description;

    // Must match CarType.typeId (Long)
    @NotNull
    private Long carTypeId;

    // ✅ NEW: must match CarBrand.brandId (Long)
    @NotNull
    private Long brandId;
}
