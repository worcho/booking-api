package io.github.dvirisha.booking_api.common.security.dto;

public record AuthRequest(
        String username,
        String password
) {
}
