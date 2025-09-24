//package com.turbopick.autowise.service;
//
//import com.turbopick.autowise.model.Car;
//import com.turbopick.autowise.model.Image;
//import com.turbopick.autowise.repository.CarRepository;
//import com.turbopick.autowise.repository.ImageRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ImageService {
//
//    private final S3Service s3Service;               // your existing S3 uploader
//    private final CarRepository carRepository;
//    private final ImageRepository imageRepository;
//
//    @Transactional
//    public List<Image> uploadImagesForCar(Long carId, List<MultipartFile> files) {
//        Car car = carRepository.findById(carId)
//                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));
//
//        List<Image> toSave = new ArrayList<>();
//        for (MultipartFile file : files) {
//            if (file == null || file.isEmpty()) continue;
//
//            String url;
//            try {
//                url = s3Service.uploadFile(file);  // returns public URL
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
//            }
//
//            Image img = Image.builder()
//                    .url(url)
//                    .car(car)              // owning side must be set
//                    .build();
//
//            car.addImage(img);             // keep bidirectional in sync
//            toSave.add(img);
//        }
//
//        // Persist (cascade from car will also save images, but this is explicit):
//        carRepository.save(car);
//        return imageRepository.saveAll(toSave);
//    }
//
//    public List<Image> listImages(Long carId) {
//        return imageRepository.findByCarId(carId);
//    }
//
//    @Transactional
//    public void deleteImage(Long imageId) {
//        // (Optional) If you later store S3 keys, also delete from S3 here.
//        imageRepository.deleteById(imageId);
//    }
//}
