package io.github.dvirisha.booking_api.booking.dto;

import io.github.dvirisha.booking_api.booking.BookingStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull
        Long roomId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        BookingStatus status
) {
}
