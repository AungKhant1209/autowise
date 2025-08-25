package com.turbopick.autowise.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    private String name;
    private String category;
    private String description;

    // Inverse side
    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();

}
