package com.turbopick.autowise.service;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.repository.CarTypeRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CarTypeService {
    @Autowired
    private CarTypeRespository carTypeRespository;

    public CarType saveCarType(CarType carType) {
        return carTypeRespository.save(carType);
    }
    public void deleteCarTypeById(int id) {
        carTypeRespository.deleteById(id);
    }
    public Optional<CarType> findCarTypeById(int id) {
        return carTypeRespository.findById(id);
    }
    public List<CarType> findAllCarTypes() {
        return carTypeRespository.findAll();
    }
}

