package io.github.dvirisha.booking_api.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateRoomRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Min(1)
        int capacity,

        @Min(1)
        BigDecimal price
) {
}
