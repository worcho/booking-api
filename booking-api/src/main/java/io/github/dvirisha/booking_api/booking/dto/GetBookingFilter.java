package io.github.dvirisha.booking_api.booking.dto;

import io.github.dvirisha.booking_api.booking.BookingStatus;

import java.time.LocalDate;

public record GetBookingFilter(
        BookingStatus status,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate
) {
}
