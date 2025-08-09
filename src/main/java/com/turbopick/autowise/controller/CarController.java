package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CarController {
    @Autowired
    private CarService carService;

    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        System.out.println("CarController.carList"+ cars.size());
        return "car-list";
    }



}