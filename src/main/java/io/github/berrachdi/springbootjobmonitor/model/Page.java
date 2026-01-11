package io.github.berrachdi.springbootjobmonitor.model;

import java.util.List;

/**
 * Page represents a paginated result set.
 *
 * @param content       The items on this page
 * @param totalElements Total number of elements across all pages
 * @param pageNumber    Current page number (0-based)
 * @param pageSize      Size of this page
 * @param totalPages    Total number of pages
 * @param hasNext       Whether there is a next page
 * @param hasPrevious   Whether there is a previous page
 *
 * @author Mohamed Berrachdi
 */
public record Page<T>(
        List<T> content,
        long totalElements,
        int pageNumber,
        int pageSize,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> Page<T> of(List<T> content, long totalElements, PageRequest pageRequest) {
        int totalPages = (int) Math.ceil((double) totalElements / pageRequest.size());
        boolean hasNext = pageRequest.page() + 1 < totalPages;
        boolean hasPrevious = pageRequest.page() > 0;
        
        return new Page<>(
                content,
                totalElements,
                pageRequest.page(),
                pageRequest.size(),
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}