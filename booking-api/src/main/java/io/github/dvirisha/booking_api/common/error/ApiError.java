package io.github.dvirisha.booking_api.common.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        int status,
        String message,
        String traceId,
        List<FieldError> errors,
        Instant timestamp
) {
    public record FieldError(String field, String message) {}
}
