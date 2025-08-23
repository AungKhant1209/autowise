package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarTypeRepository extends JpaRepository<CarType, Long> {
}
