package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String description;

    // Inverse side
    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();

}
