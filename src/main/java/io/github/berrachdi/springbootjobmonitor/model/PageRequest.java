package io.github.berrachdi.springbootjobmonitor.model;

/**
 * PageRequest represents pagination parameters for queries.
 *
 * @param page     The page number (0-based)
 * @param size     The page size (number of items per page)
 * @param sort     The sort field (optional)
 * @param direction The sort direction (ASC/DESC, optional)
 *
 * @author Mohamed Berrachdi
 */
public record PageRequest(
        int page,
        int size,
        String sort,
        String direction
) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("Size must be > 0");
        if (size > 100) throw new IllegalArgumentException("Size must be <= 100");
    }
    
    public PageRequest(int page, int size) {
        this(page, size, null, null);
    }
    
    public long getOffset() {
        return (long) page * size;
    }
    
    public int getLimit() {
        return size;
    }
}