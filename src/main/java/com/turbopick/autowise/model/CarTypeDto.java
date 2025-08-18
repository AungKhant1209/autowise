package com.turbopick.autowise.model;


import jakarta.validation.constraints.NotEmpty;

import lombok.Data;


@Data
public class CarTypeDto {

    @NotEmpty(message = "Name cannot be empty")
    private String typeName;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
