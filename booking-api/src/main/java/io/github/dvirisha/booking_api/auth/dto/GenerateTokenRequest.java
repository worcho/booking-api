package io.github.dvirisha.booking_api.auth.dto;

public record GenerateTokenRequest(
        String username,
        String password
) {
}
