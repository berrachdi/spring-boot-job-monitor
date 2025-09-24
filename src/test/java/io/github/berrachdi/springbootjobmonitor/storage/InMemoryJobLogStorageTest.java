package io.github.berrachdi.springbootjobmonitor.storage;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.model.Page;
import io.github.berrachdi.springbootjobmonitor.model.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryJobLogStorageTest {

    private InMemoryJobLogStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryJobLogStorage();
        
        // Add some test data
        for (int i = 0; i < 15; i++) {
            JobExecutionLog log = new JobExecutionLog(
                    "testJob",
                    LocalDateTime.now().minusHours(i),
                    LocalDateTime.now().minusHours(i).plusMinutes(30),
                    30 * 60 * 1000L, // 30 minutes in milliseconds
                    i % 3 != 0, // Some failures
                    i % 3 == 0 ? "Error " + i : null
            );
            storage.addLog("testJob", log);
        }
        
        // Add logs for another job
        for (int i = 0; i < 5; i++) {
            JobExecutionLog log = new JobExecutionLog(
                    "anotherJob",
                    LocalDateTime.now().minusHours(i + 20),
                    LocalDateTime.now().minusHours(i + 20).plusMinutes(15),
                    15 * 60 * 1000L,
                    true,
                    null
            );
            storage.addLog("anotherJob", log);
        }
    }

    @Test
    void testGetLogsForJobPaginated() {
        PageRequest pageRequest = new PageRequest(0, 5);
        Page<JobExecutionLog> page = storage.getLogsForJob("testJob", pageRequest);
        
        assertEquals(5, page.content().size());
        assertEquals(15, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(3, page.totalPages());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
        
        // Check that logs are sorted by start_time DESC by default
        LocalDateTime previousTime = null;
        for (JobExecutionLog log : page.content()) {
            if (previousTime != null) {
                assertTrue(log.startTime().isBefore(previousTime) || log.startTime().equals(previousTime));
            }
            previousTime = log.startTime();
        }
    }

    @Test
    void testGetLogsForJobPaginatedSecondPage() {
        PageRequest pageRequest = new PageRequest(1, 5);
        Page<JobExecutionLog> page = storage.getLogsForJob("testJob", pageRequest);
        
        assertEquals(5, page.content().size());
        assertEquals(15, page.totalElements());
        assertEquals(1, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(3, page.totalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void testGetLogsForJobPaginatedLastPage() {
        PageRequest pageRequest = new PageRequest(2, 5);
        Page<JobExecutionLog> page = storage.getLogsForJob("testJob", pageRequest);
        
        assertEquals(5, page.content().size());
        assertEquals(15, page.totalElements());
        assertEquals(2, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(3, page.totalPages());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void testGetLogsForJobPaginatedNonExistentJob() {
        PageRequest pageRequest = new PageRequest(0, 5);
        Page<JobExecutionLog> page = storage.getLogsForJob("nonExistentJob", pageRequest);
        
        assertTrue(page.content().isEmpty());
        assertEquals(0, page.totalElements());
        assertEquals(0, page.totalPages());
        assertFalse(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    void testGetAllLogsPaginated() {
        PageRequest pageRequest = new PageRequest(0, 10);
        Page<JobExecutionLog> page = storage.getAllLogs(pageRequest);
        
        assertEquals(10, page.content().size());
        assertEquals(20, page.totalElements()); // 15 + 5 logs
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
        assertEquals(2, page.totalPages());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    void testGetAllLogsPaginatedWithCustomSort() {
        PageRequest pageRequest = new PageRequest(0, 5, "job_name", "ASC");
        Page<JobExecutionLog> page = storage.getAllLogs(pageRequest);
        
        assertEquals(5, page.content().size());
        assertEquals(20, page.totalElements());
        
        // Check that logs are sorted by job_name ASC
        String previousJobName = null;
        for (JobExecutionLog log : page.content()) {
            if (previousJobName != null) {
                assertTrue(log.jobName().compareTo(previousJobName) >= 0);
            }
            previousJobName = log.jobName();
        }
    }
}