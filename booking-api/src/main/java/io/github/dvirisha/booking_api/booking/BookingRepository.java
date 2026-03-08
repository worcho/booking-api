package io.github.dvirisha.booking_api.booking;

import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("select (count(b) > 0) from Booking b where b.room.id = ?1 and b.startDate < ?3 and b.endDate > ?2")
    boolean isBookingExist(Long roomId, LocalDate startDate, LocalDate endDate);

    @Override
    @EntityGraph(attributePaths = "room")
    Page<Booking> findAll(@NonNull Specification<Booking> spec, @NonNull Pageable pageable);

}
