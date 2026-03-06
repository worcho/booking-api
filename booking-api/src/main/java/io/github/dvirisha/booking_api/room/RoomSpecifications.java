package io.github.dvirisha.booking_api.room;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class RoomSpecifications {

    public static Specification<Room> capacityAtLeast(Integer min) {
        return (root, query, criteriaBuilder) ->
                min == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), min);
    }

    public static Specification<Room> priceAtLeast(BigDecimal priceMin) {
        return (root, query, criteriaBuilder) ->
                priceMin == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin);
    }

    public static Specification<Room> priceAtMost(BigDecimal priceMax) {
        return (root, query, criteriaBuilder) ->
                priceMax == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax);
    }
}
