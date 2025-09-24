package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByCar_IdOrderByIdAsc(Long carId);
    void deleteAllByCar_Id(Long carId);
}
