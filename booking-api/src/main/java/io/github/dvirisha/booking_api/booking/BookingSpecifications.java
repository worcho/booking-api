package io.github.dvirisha.booking_api.booking;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class BookingSpecifications {

    public static Specification<Booking> withRoomId(Long roomId) {
        return (root, query, criteriaBuilder) ->
                roomId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("roomId"), roomId);
    }

    public static Specification<Booking> withStatus(BookingStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Booking> withStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThan(root.get("endDate"), startDate);
    }

    public static Specification<Booking> withEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate);
    }
}
