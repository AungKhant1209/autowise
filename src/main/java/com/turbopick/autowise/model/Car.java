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
    private LocalDate productionYear; // or int if you only need year
    private String engineSize;
    private int seat;
    private int door;
    private Long warranty;            // consider warrantyYears int
    private String transmission;
    private String driveType;
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Avoid ALL on ManyToOne to prevent accidental deletes of reference data
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "car_type_id")
    private CarType carType;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "car_feature",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Feature> features = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "brand_id")
    private CarBrand carBrand;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "image_url", length = 1024) // important for long S3 URLs
    private List<String> imageUrls = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        if (features != null) features.clear();
        setCarType(null);
    }
}
