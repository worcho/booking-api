package io.github.dvirisha.booking_api.auth.dto;

import jakarta.validation.constraints.NotNull;

public record GenerateTokenRequest(
        @NotNull
        String username,
        @NotNull
        String password
) {
}
