package com.turbopick.autowise.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CarDto {

    @NotEmpty(message = "Car name cannot be empty")
    private String name;

    @NotEmpty(message = "YouTube link cannot be empty")
    private String youtubeLink;

    @NotEmpty(message = "Car image ID cannot be empty")
    private String carImageId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Type ID is required")
    private Long typeId;

    @NotNull(message = "Rating ID is required")
    private Long ratingId;

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

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getCarImageId() {
        return carImageId;
    }

    public void setCarImageId(String carImageId) {
        this.carImageId = carImageId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Long getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Long productionYear) {
        this.productionYear = productionYear;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public Long getSeat() {
        return seat;
    }

    public void setSeat(Long seat) {
        this.seat = seat;
    }

    public Long getDoor() {
        return door;
    }

    public void setDoor(Long door) {
        this.door = door;
    }

    public Long getWarranty() {
        return warranty;
    }

    public void setWarranty(Long warranty) {
        this.warranty = warranty;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public Long getColor() {
        return color;
    }

    public void setColor(Long color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
