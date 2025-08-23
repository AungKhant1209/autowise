package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {

    Car findCarById(int id);

    Car findCarByName(String name);

    boolean existsById(Integer id);
}
