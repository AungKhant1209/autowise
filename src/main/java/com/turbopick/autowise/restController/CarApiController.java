package com.turbopick.autowise.restController;

import com.turbopick.autowise.dto.CarListViewDto;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarApiController {

   @Autowired
   private CarService carService;

   @GetMapping
   public CarListViewDto getAllCars() {
      return carService.getCarsForListView();
   }

}
