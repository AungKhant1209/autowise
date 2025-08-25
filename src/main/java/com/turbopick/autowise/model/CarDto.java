package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;

@Data
public class CarDto {

    @NotBlank
    private String name;

    private String youtubeLink;

    @NotNull
    private Long price;

    private String fuelType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionYear;

    private String engineSize;

    private Integer seat;
    private Integer door;

    private Long warranty;

    private String transmission;
    private String driveType;
    private String color;

    private String description;

    
    @NotNull
    private Long carTypeId;

    
    @NotNull
    private Long brandId;
    private List<Long> featureIds;

}
