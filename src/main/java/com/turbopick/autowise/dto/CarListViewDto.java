package com.turbopick.autowise.dto;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CarListViewDto {
    private List<Car> cars;
    private List<CarBrand> carBrands;
    private List<Feature> features;
    private List<CarType> types;
    private long minPrice;
    private long maxPrice;

}

