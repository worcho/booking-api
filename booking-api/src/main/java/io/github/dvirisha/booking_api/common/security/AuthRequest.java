package io.github.dvirisha.booking_api.common.security;

public record AuthRequest(
        String username,
        String password
) {
}
