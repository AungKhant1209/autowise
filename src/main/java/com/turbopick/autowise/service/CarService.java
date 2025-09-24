package com.turbopick.autowise.service;

import com.turbopick.autowise.dto.CarDto;
import com.turbopick.autowise.dto.CarListViewDto;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.repository.CarBrandRepository;
import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.repository.CarTypeRepository;
import com.turbopick.autowise.repository.FeatureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import com.turbopick.autowise.dto.CarListViewDto;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.repository.*;
import com.turbopick.autowise.spec.CarSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarTypeRepository carTypeRepository;
    private final FeatureRepository featureRepository;


    public CarListViewDto getCarsForListViewFiltered(
            String name, Long minPrice, Long maxPrice,
            Long brandId, Long typeId, String fuel,
            List<Long> featureIds, Pageable pageable) {

        Specification<Car> spec =
                CarSpecs.nameLike(name)
                        .and(CarSpecs.priceGte(minPrice))
                        .and(CarSpecs.priceLte(maxPrice))
                        .and(CarSpecs.brandIs(brandId))
                        .and(CarSpecs.typeIs(typeId))
                        .and(CarSpecs.fuelIs(fuel))
                        .and(CarSpecs.hasAllFeatures(featureIds));

        Page<Car> page = carRepository.findAll(spec, pageable);

        // sidebar data
        List<Feature> allFeatures = featureRepository.findAll();
        List<CarBrand> carBrands  = carBrandRepository.findAll();
        List<CarType>  carTypes   = carTypeRepository.findAll();

        long min = carRepository.findAll().stream()
                .filter(c -> c.getPrice() != null)
                .mapToLong(Car::getPrice).min().orElse(0);
        long max = carRepository.findAll().stream()
                .filter(c -> c.getPrice() != null)
                .mapToLong(Car::getPrice).max().orElse(0);

        CarListViewDto dto = new CarListViewDto();
        dto.setCars(page.getContent());
        dto.setFeatures(allFeatures);
        dto.setCarBrands(carBrands);
        dto.setTypes(carTypes);
        dto.setMinPrice(min);
        dto.setMaxPrice(max);
        return dto;
    }



   public CarListViewDto getCarsForListView(){
       List<Car> cars = carRepository.findAll();
       List<Feature> allFeatures = featureRepository.findAll();
       List<CarBrand> carBrands = carBrandRepository.findAll();
       List<CarType> carTypes = carTypeRepository.findAll();
       long minPrice = cars.stream().filter(c -> c.getPrice()!=null).mapToLong(Car::getPrice).min().orElse(0);
       long maxPrice = cars.stream().filter(c -> c.getPrice()!=null).mapToLong(Car::getPrice).max().orElse(0);
       CarListViewDto carListViewDto = new CarListViewDto();
       carListViewDto.setCars(cars);
       carListViewDto.setFeatures(allFeatures);
       carListViewDto.setCarBrands(carBrands);
       carListViewDto.setTypes(carTypes);
       carListViewDto.setMinPrice(minPrice);
       carListViewDto.setMaxPrice(maxPrice);
       return carListViewDto;
   }



    public Car buildCarForCreate(CarDto dto, CarType type, CarBrand brand, List<Feature> features) {
        Car car = new Car();
        applyDtoToEntity(car, dto, type, brand, features);
        car.setName(normalizeName(car.getName()));
        return car;
    }
    public List<Car> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return carRepository.findAllById(ids);
    }


    public Optional<Car> findById(Long id) { return carRepository.findById(id); }
    public Car findByIdOrNull(Long id) { return carRepository.findById(id).orElse(null); }

    public List<Car> findAll() { return carRepository.findAll(); }

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
    @Transactional(readOnly = true)
    public Optional<Car> findByIdWithFeatures(Long id) {
        return carRepository.findByIdWithFeatures(id);
    }


    public void applyDtoToEntity(Car target, CarDto dto, CarType type, CarBrand brand, List<Feature> features) {

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
        target.setCarType(type);
        target.setCarBrand(brand);

        target.getFeatures().clear();
        if (features != null && !features.isEmpty()) {
            target.getFeatures().addAll(new HashSet<>(features));
        }
    }
    public CarDto buildDtoForEdit(Car car) {
        if (car == null) return null;

        CarDto dto = new CarDto();
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

        if (car.getCarType() != null) {
            dto.setCarTypeId(String.valueOf(car.getCarType().getTypeId()));
        }
        if (car.getCarBrand() != null) {
            dto.setCarBrandId(car.getCarBrand().getBrandId());
        }

        dto.setFeatureIds(
                car.getFeatures() == null
                        ? java.util.Collections.emptyList()
                        : car.getFeatures().stream()
                        .map(Feature::getId)
                        .collect(Collectors.toList())
        );

        return dto;
    }
    public String normalizeName(String s) {
        if (s == null) return null;
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

    public Car save(Car car) {
        car.setName(normalizeName(car.getName()));
        return carRepository.save(car);
    }
}