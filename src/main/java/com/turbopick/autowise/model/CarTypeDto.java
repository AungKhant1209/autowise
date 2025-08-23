package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CarTypeDto {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Brand cannot be empty")
    private String brand;

    @NotEmpty(message = "Code cannot be empty")
    private String code;

    @NotEmpty(message = "Description cannot be empty")
    private String description;
}
