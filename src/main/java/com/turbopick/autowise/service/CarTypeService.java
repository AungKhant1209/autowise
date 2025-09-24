package com.turbopick.autowise.service;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.repository.CarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CarTypeService {

    private final CarTypeRepository carTypeRepository;

    public CarTypeService(CarTypeRepository carTypeRepository) {
        this.carTypeRepository = carTypeRepository;
    }


    public CarType save(CarType carType) {
        return carTypeRepository.save(carType);
    }

    public List<CarType> saveAll(Collection<CarType> carTypes) {
        return carTypeRepository.saveAll(carTypes);
    }




    public CarType findById(Long id) {
        return carTypeRepository.findById(id).orElse(null);
    }

    public List<CarType> findAll() {
        return carTypeRepository.findAll();
    }



}




