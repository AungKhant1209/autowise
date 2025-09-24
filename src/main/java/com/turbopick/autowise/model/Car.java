package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "car")
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    private String name;
    private String youtubeLink;
    private Long price;
    private String fuelType;
    private LocalDate productionYear;
    private String engineSize;
    private int seat;
    private int door;
    private Long warranty;
    private String transmission;
    private String driveType;
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_type_id")
    private CarType carType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private CarBrand carBrand;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "car_feature",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Feature> features = new java.util.HashSet<>();

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "image_url", length = 1024)
    private List<String> imageUrls = new ArrayList<>();

    // in Car.java
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();
}
