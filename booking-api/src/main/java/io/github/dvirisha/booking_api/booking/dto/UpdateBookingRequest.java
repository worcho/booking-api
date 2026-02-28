package io.github.dvirisha.booking_api.booking.dto;

import java.time.LocalDate;

public record UpdateBookingRequest(
        LocalDate startDate,
        LocalDate endDate
) {
}
