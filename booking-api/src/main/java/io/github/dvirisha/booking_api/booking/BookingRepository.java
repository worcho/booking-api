package io.github.dvirisha.booking_api.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select (count(b) > 0) from Booking b where b.roomId = ?1 and b.startDate < ?3 and b.endDate > ?2")
    boolean isBookingExist(Long roomId, LocalDate startDate, LocalDate endDate);

    @Query("select b from Booking b where b.roomId = ?1 and b.startDate < ?3 and b.endDate > ?2")
    List<Booking> findByRoomId(Long id, LocalDate startDate, LocalDate endDate);
}
