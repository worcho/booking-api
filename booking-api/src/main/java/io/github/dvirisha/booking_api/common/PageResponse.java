package io.github.dvirisha.booking_api.common;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<String> sort
) {
}
