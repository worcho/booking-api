package io.github.dvirisha.booking_api.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateBookingRequest(
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}
