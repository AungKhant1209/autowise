package com.turbopick.autowise.spec;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.Feature;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class CarSpecs {

    public static Specification<Car> nameLike(String name) {
        return (root, q, cb) ->
                (name == null || name.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Car> priceGte(Long min) {
        return (root, q, cb) -> (min == null) ? cb.conjunction() : cb.ge(root.get("price"), min);
    }

    public static Specification<Car> priceLte(Long max) {
        return (root, q, cb) -> (max == null) ? cb.conjunction() : cb.le(root.get("price"), max);
    }

    public static Specification<Car> brandIs(Long brandId) {
        return (root, q, cb) ->
                (brandId == null) ? cb.conjunction() : cb.equal(root.get("carBrand").get("brandId"), brandId);
    }

    public static Specification<Car> typeIs(Long typeId) {
        return (root, q, cb) ->
                (typeId == null) ? cb.conjunction() : cb.equal(root.get("carType").get("typeId"), typeId);
    }

    public static Specification<Car> fuelIs(String fuel) {
        return (root, q, cb) ->
                (fuel == null || fuel.isBlank())
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("fuelType")), fuel.toLowerCase()); // case-insensitive
    }

    /** ANY of the given features (quick filter). */
    public static Specification<Car> hasAnyFeature(Collection<Long> featureIds) {
        return (root, query, cb) -> {
            if (featureIds == null || featureIds.isEmpty()) return cb.conjunction();
            query.distinct(true);
            Join<Car, Feature> f = root.join("features", JoinType.INNER);
            return f.get("id").in(featureIds);
        };
    }

    /** ALL features (strict filter). */
    public static Specification<Car> hasAllFeatures(Collection<Long> featureIds) {
        return (root, query, cb) -> {
            if (featureIds == null || featureIds.isEmpty()) return cb.conjunction();

            var sub = query.subquery(Long.class);
            var c2  = sub.from(Car.class);
            var f2  = c2.join("features", JoinType.INNER);

            sub.select(c2.get("id"))
                    .where(
                            cb.equal(c2.get("id"), root.get("id")), // FIX: compare IDs
                            f2.get("id").in(featureIds)
                    )
                    .groupBy(c2.get("id"))
                    .having(cb.equal(cb.countDistinct(f2.get("id")), (long) featureIds.size()));

            return cb.exists(sub);
        };
    }
}