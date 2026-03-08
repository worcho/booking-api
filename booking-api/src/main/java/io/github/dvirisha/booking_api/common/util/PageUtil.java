package io.github.dvirisha.booking_api.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public final class PageUtil {

    private static final int PAGE_MAX_SIZE = 50;

    public static Pageable normalizePageable(Pageable pageable, Set<String> allowedSortFields) {
        int requestedSize = pageable.getPageSize();

        int safeSize = Math.min(requestedSize, PAGE_MAX_SIZE);
        Sort safeSort = validateSort(pageable.getSort(), allowedSortFields);

        return PageRequest.of(pageable.getPageNumber(), safeSize, safeSort);
    }

    private static Sort validateSort(Sort sort, Set<String> allowedSortFields) {
        if (sort.isUnsorted()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        for (Sort.Order order : sort) {
            if (!allowedSortFields.contains(order.getProperty())) {
                throw new IllegalArgumentException("Sorting by field '%s' is not allowed".formatted(order.getProperty()));
            }
        }
        return sort;
    }

    public static List<String> toSortList(Sort sort){
        return sort.stream()
                .map(order -> order.getProperty() + ", " + order.getDirection().name().toLowerCase())
                .toList();
    }
}
