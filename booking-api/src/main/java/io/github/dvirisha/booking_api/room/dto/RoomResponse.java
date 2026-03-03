package io.github.dvirisha.booking_api.room.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        BigDecimal price,
        Instant createdAt
) {
}
