package com.turbopick.autowise.service;

import com.turbopick.autowise.dto.CarDto;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    public CarService(CarRepository carRepository) { this.carRepository = carRepository; }

    /* ========= Name normalization & checks ========= */

    /** Public so controller can reuse the same normalization. */
    public String normalizeName(String s) {
        if (s == null) return null;
        // trim and collapse any internal whitespace sequences to a single space
        return s.trim().replaceAll("\\s+", " ");
    }

    public boolean nameExists(String raw) {
        String n = normalizeName(raw);
        return n != null && !n.isEmpty() && carRepository.existsByNameIgnoreCase(n);
    }

    public boolean nameExistsExcludingId(Long id, String raw) {
        String n = normalizeName(raw);
        return id != null && n != null && !n.isEmpty()
                && carRepository.existsByNameIgnoreCaseAndIdNot(n, id);
    }

    /* ========= Create / Update ========= */

    public Car save(Car car) {
        // always persist normalized name
        car.setName(normalizeName(car.getName()));
        return carRepository.save(car);
    }

    public List<Car> saveAll(List<Car> cars) {
        cars.forEach(c -> c.setName(normalizeName(c.getName())));
        return carRepository.saveAll(cars);
    }

    /* ========= Read ========= */

    public Optional<Car> findById(Long id) { return carRepository.findById(id); }
    public Car findByIdOrNull(Long id) { return carRepository.findById(id).orElse(null); }

    public Car findByIdOrThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + id));
    }

    public Optional<Car> findByName(String name) {
        String n = normalizeName(name);
        return (n == null || n.isEmpty()) ? Optional.empty() : carRepository.findByNameIgnoreCase(n);
    }

    public List<Car> findAll() { return carRepository.findAll(); }

    public boolean existsById(Long id) { return carRepository.existsById(id); }

    /* ========= Delete ========= */

    public void deleteById(Long id) { carRepository.deleteById(id); }

    @Transactional
    public void delete(Car car) {
        if (car == null || car.getId() == null) {
            throw new IllegalArgumentException("Car (with id) is required for delete");
        }
        carRepository.delete(
                carRepository.findById(car.getId()).orElseThrow(() ->
                        new IllegalArgumentException("Car not found: " + car.getId()))
        );
    }

    /* ========= Feature fetch ========= */

    @Transactional(readOnly = true)
    public Optional<Car> findByIdWithFeatures(Long id) {
        return carRepository.findByIdWithFeatures(id);
    }

    @Transactional(readOnly = true)
    public Car findByIdWithFeaturesOrNull(Long id) {
        return findByIdWithFeatures(id).orElse(null);
    }

    /* ========= Mapping helpers ========= */

    public Car buildCarForCreate(CarDto dto, CarType type, CarBrand brand, List<Feature> features) {
        Car car = new Car();
        applyDtoToEntity(car, dto, type, brand, features);
        // ensure normalized on build as well (save() re-normalizes too)
        car.setName(normalizeName(car.getName()));
        return car;
    }

    /** Apply DTO onto an existing entity (useful for update). */
    public void applyDtoToEntity(Car target, CarDto dto, CarType type, CarBrand brand, List<Feature> features) {
        // Scalars
        target.setName(dto.getName());
        target.setYoutubeLink(dto.getYoutubeLink());
        target.setPrice(dto.getPrice());
        target.setFuelType(dto.getFuelType());
        target.setProductionYear(dto.getProductionYear());
        target.setEngineSize(dto.getEngineSize());
        target.setSeat(dto.getSeat());
        target.setDoor(dto.getDoor());
        target.setWarranty(dto.getWarranty());
        target.setTransmission(dto.getTransmission());
        target.setDriveType(dto.getDriveType());
        target.setColor(dto.getColor());
        target.setDescription(dto.getDescription());

        // Relations
        target.setCarType(type);
        target.setCarBrand(brand);

        // Features (replace)
        target.getFeatures().clear();
        if (features != null && !features.isEmpty()) {
            target.getFeatures().addAll(new HashSet<>(features));
        }
    }
    public CarDto buildDtoForEdit(Car car) {
        if (car == null) return null;

        CarDto dto = new CarDto();
        // scalars
        dto.setName(car.getName());
        dto.setYoutubeLink(car.getYoutubeLink());
        dto.setPrice(car.getPrice());
        dto.setFuelType(car.getFuelType());
        dto.setProductionYear(car.getProductionYear());
        dto.setEngineSize(car.getEngineSize());
        dto.setSeat(car.getSeat());
        dto.setDoor(car.getDoor());
        dto.setWarranty(car.getWarranty());
        dto.setTransmission(car.getTransmission());
        dto.setDriveType(car.getDriveType());
        dto.setColor(car.getColor());
        dto.setDescription(car.getDescription());

        // relations (preselects)
        if (car.getCarType() != null) {
            dto.setCarTypeId(String.valueOf(car.getCarType().getTypeId()));
        }
        if (car.getCarBrand() != null) {
            dto.setCarBrandId(car.getCarBrand().getBrandId());
        }

        // features (ids only)
        dto.setFeatureIds(
                car.getFeatures() == null
                        ? java.util.Collections.emptyList()
                        : car.getFeatures().stream()
                        .map(Feature::getId)
                        .collect(Collectors.toList())
        );

        return dto;
    }
}