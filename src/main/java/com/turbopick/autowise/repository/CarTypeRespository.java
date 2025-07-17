package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CarTypeRespository extends JpaRepository<CarType,Integer> {
    public CarType findByBrand(String brand);
}

