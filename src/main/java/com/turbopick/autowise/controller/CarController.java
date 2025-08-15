package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class  CarController {

    @Autowired
    private CarRepository carRepository;

    // Handle the /carList route to display the list of cars
    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carRepository.findAll();  // Get all cars from the repository
        model.addAttribute("cars", cars);
        return "car-list"; // This will return the 'car-list.html' template
    }

    // Handle the /car-detail/{id} route to display car details
    @GetMapping("/car-detail/{id}")
    public String carDetail(@PathVariable("id") Long id, Model model) {
        Optional<Car> carOpt = carRepository.findById(id);
        if (carOpt.isEmpty()) {
            return "redirect:/carList"; // Redirect to the car list if the car is not found
        }
        model.addAttribute("car", carOpt.get()); // Add car data to the model
        return "listing-single"; // This will return the 'listing-single.html' template
    }
}
