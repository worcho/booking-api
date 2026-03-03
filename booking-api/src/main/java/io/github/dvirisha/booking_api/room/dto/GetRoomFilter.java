package io.github.dvirisha.booking_api.room.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record GetRoomFilter(
        @Min(1)
        Integer  capacityMin,
        @Min(1)
        BigDecimal priceMin,
        @Min(1)
        BigDecimal priceMax
) {
}
