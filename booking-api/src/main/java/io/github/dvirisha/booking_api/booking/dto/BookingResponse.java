package io.github.dvirisha.booking_api.booking.dto;

import io.github.dvirisha.booking_api.booking.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatus status,
        Instant createdAt
) {
}
