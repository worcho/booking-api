package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.common.util.PageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class PageUtilTest {

    @Test
    void shouldPageSizeBeNormalizedSuccessfully() {
        PageRequest pageRequest = PageRequest.of(0, 100);

        Pageable result = PageUtil.normalizePageable(pageRequest, Collections.emptySet());

        assertTrue("Page size is greater than 50", result.getPageSize() <= 50);
    }

    @Test
    void sizeShouldNotBeChangedIfUnderTheLimit() {
        PageRequest pageRequest = PageRequest.of(0, 20);

        Pageable result = PageUtil.normalizePageable(pageRequest, Collections.emptySet());

        assertEquals(20, result.getPageSize());
    }

    @Test
    void shouldPreservePageNumber() {
        PageRequest pageRequest = PageRequest.of(5, 20);

        Pageable result = PageUtil.normalizePageable(pageRequest, Collections.emptySet());

        assertEquals(5, result.getPageNumber());
    }

    @Test
    void shouldAddDefaultSortWhenUnsorted() {
        Pageable requested = PageRequest.of(0, 10);

        Pageable result = PageUtil.normalizePageable(requested, Collections.emptySet());

        Sort.Order order = result.getSort().iterator().next();
        assertEquals("id", order.getProperty());
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void shouldKeepValidSorting() {
        Pageable requested = PageRequest.of(0, 10, Sort.Direction.DESC, "price");

        Pageable result = PageUtil.normalizePageable(requested, Collections.singleton("price"));

        Sort.Order order = result.getSort().iterator().next();
        assertEquals("price", order.getProperty());
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }


    @Test
    void shouldThrowWhenSortFieldNotAllowed() {
        Pageable requested = PageRequest.of(0, 10, Sort.by("hackMe"));

        assertThrows(
                IllegalArgumentException.class,
                () -> PageUtil.normalizePageable(requested, Set.of("id", "capacity", "price"))
        );
    }

    @Test
    void shouldConvertSortToStringList() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        List<String> result = PageUtil.toSortList(sort);

        assertEquals(1, result.size());
        assertEquals("id, asc", result.get(0));
    }

    @Test
    void shouldHandleMultipleSortFields() {
        Sort sort = Sort.by(
                Sort.Order.asc("id"),
                Sort.Order.desc("price")
        );

        List<String> result = PageUtil.toSortList(sort);

        assertEquals(2, result.size());
        assertEquals("id, asc", result.get(0));
        assertEquals("price, desc", result.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenUnsorted() {
        List<String> result = PageUtil.toSortList(Sort.unsorted());

        Assertions.assertTrue(result.isEmpty());
    }
}
