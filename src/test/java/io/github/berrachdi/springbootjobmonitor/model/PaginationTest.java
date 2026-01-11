package io.github.berrachdi.springbootjobmonitor.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestTest {

    @Test
    void testValidPageRequest() {
        PageRequest pageRequest = new PageRequest(0, 10);
        assertEquals(0, pageRequest.page());
        assertEquals(10, pageRequest.size());
        assertNull(pageRequest.sort());
        assertNull(pageRequest.direction());
        assertEquals(0, pageRequest.getOffset());
        assertEquals(10, pageRequest.getLimit());
    }

    @Test
    void testPageRequestWithSorting() {
        PageRequest pageRequest = new PageRequest(2, 5, "start_time", "DESC");
        assertEquals(2, pageRequest.page());
        assertEquals(5, pageRequest.size());
        assertEquals("start_time", pageRequest.sort());
        assertEquals("DESC", pageRequest.direction());
        assertEquals(10, pageRequest.getOffset());
        assertEquals(5, pageRequest.getLimit());
    }

    @Test
    void testInvalidPageRequest() {
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 101));
    }
}

class PageTest {

    @Test
    void testPageCreation() {
        List<String> content = Arrays.asList("item1", "item2", "item3");
        PageRequest pageRequest = new PageRequest(0, 3);
        
        Page<String> page = Page.of(content, 10, pageRequest);
        
        assertEquals(content, page.content());
        assertEquals(10, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(3, page.pageSize());
        assertEquals(4, page.totalPages()); // ceil(10/3) = 4
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    void testLastPage() {
        List<String> content = Collections.singletonList("lastItem");
        PageRequest pageRequest = new PageRequest(3, 3);
        
        Page<String> page = Page.of(content, 10, pageRequest);
        
        assertEquals(content, page.content());
        assertEquals(10, page.totalElements());
        assertEquals(3, page.pageNumber());
        assertEquals(3, page.pageSize());
        assertEquals(4, page.totalPages());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void testEmptyPage() {
        List<String> content = Collections.emptyList();
        PageRequest pageRequest = new PageRequest(0, 10);
        
        Page<String> page = Page.of(content, 0, pageRequest);
        
        assertTrue(page.content().isEmpty());
        assertEquals(0, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
        assertEquals(0, page.totalPages());
        assertFalse(page.hasNext());
        assertFalse(page.hasPrevious());
    }
}