package org.medtech.springbootjobmonitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.response.JobExecutionFilter;
import org.medtech.springbootjobmonitor.response.PageResponse;
import org.medtech.springbootjobmonitor.service.JobMonitoringService;
import org.medtech.springbootjobmonitor.storage.JobLogStorage;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JobMonitoringServiceTest {

    private JobLogStorage jobLogStorage;
    private JobMonitoringService jobMonitoringService;

    @BeforeEach
    void setUp() {
        jobLogStorage = mock(JobLogStorage.class);
        jobMonitoringService = new JobMonitoringService(jobLogStorage);
    }

    @Test
    void testAddLog_ShouldDelegateToStorage() {
        String jobName = "exampleJob";
        JobExecutionLog log = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 10, 0),
                LocalDateTime.of(2025, 7, 18, 10, 5),
                300000,
                true,
                null
        );

        jobMonitoringService.addLog(jobName, log);

        verify(jobLogStorage, times(1)).addLog(jobName, log);
    }

    @Test
    void testGetAllLogsLegacy_ShouldReturnAllLogs() {
        JobExecutionLog log1 = new JobExecutionLog(
                "job1",
                LocalDateTime.of(2025, 7, 18, 9, 0),
                LocalDateTime.of(2025, 7, 18, 9, 5),
                300000,
                true,
                null
        );
        JobExecutionLog log2 = new JobExecutionLog(
                "job2",
                LocalDateTime.of(2025, 7, 18, 11, 0),
                LocalDateTime.of(2025, 7, 18, 11, 10),
                600000,
                false,
                "Failed due to timeout"
        );

        Map<String, List<JobExecutionLog>> expectedLogs = new HashMap<>();
        expectedLogs.put("job1", List.of(log1));
        expectedLogs.put("job2", List.of(log2));

        when(jobLogStorage.getAllLogs()).thenReturn(expectedLogs);

        Map<String, List<JobExecutionLog>> actualLogs = jobMonitoringService.getAllLogsLegacy();

        assertEquals(expectedLogs, actualLogs);
        verify(jobLogStorage, times(1)).getAllLogs();
    }

    @Test
    void testGetLogsForJobLegacy_ShouldReturnLogsForGivenJob() {
        String jobName = "job1";
        JobExecutionLog log = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 14, 0),
                LocalDateTime.of(2025, 7, 18, 14, 2),
                120000,
                true,
                null
        );
        List<JobExecutionLog> expectedLogs = List.of(log);

        when(jobLogStorage.getLogsForJob(jobName)).thenReturn(expectedLogs);

        List<JobExecutionLog> actualLogs = jobMonitoringService.getLogsForJobLegacy(jobName);

        assertEquals(expectedLogs, actualLogs);
        verify(jobLogStorage, times(1)).getLogsForJob(jobName);
    }

    @Test
    void testGetAllLogs_WithPaginationAndFiltering() {
        JobExecutionLog log1 = new JobExecutionLog(
                "job1",
                LocalDateTime.of(2025, 7, 18, 9, 0),
                LocalDateTime.of(2025, 7, 18, 9, 5),
                300000,
                true,
                null
        );
        JobExecutionLog log2 = new JobExecutionLog(
                "job2",
                LocalDateTime.of(2025, 7, 18, 11, 0),
                LocalDateTime.of(2025, 7, 18, 11, 10),
                600000,
                false,
                "Failed due to timeout"
        );

        Map<String, List<JobExecutionLog>> storageLogs = new HashMap<>();
        storageLogs.put("job1", List.of(log1));
        storageLogs.put("job2", List.of(log2));

        when(jobLogStorage.getAllLogs()).thenReturn(storageLogs);

        // Filter oluştur
        JobExecutionFilter filter = new JobExecutionFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setSortBy("executionTime");
        filter.setSortDirection("desc");

        PageResponse<Map.Entry<String, List<JobExecutionLog>>> actualLogs =
                jobMonitoringService.getAllLogs(filter);

        assertNotNull(actualLogs);
        assertEquals(2, actualLogs.getTotalElements());
        assertEquals(0, actualLogs.getPage());
        assertEquals(10, actualLogs.getSize());
        verify(jobLogStorage, times(1)).getAllLogs();
    }

    @Test
    void testGetLogsForJob_WithPaginationAndFiltering() {
        String jobName = "job1";
        JobExecutionLog log1 = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 14, 0),
                LocalDateTime.of(2025, 7, 18, 14, 2),
                120000,
                true,
                null
        );
        JobExecutionLog log2 = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 15, 0),
                LocalDateTime.of(2025, 7, 18, 15, 3),
                180000,
                false,
                "Error occurred"
        );

        List<JobExecutionLog> storageLogs = List.of(log1, log2);

        when(jobLogStorage.getLogsForJob(jobName)).thenReturn(storageLogs);

        // Filter oluştur
        JobExecutionFilter filter = new JobExecutionFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setSortBy("executionTime");
        filter.setSortDirection("desc");

        PageResponse<JobExecutionLog> actualLogs =
                jobMonitoringService.getLogsForJob(jobName, filter);

        assertNotNull(actualLogs);
        assertEquals(2, actualLogs.getTotalElements());
        assertEquals(0, actualLogs.getPage());
        assertEquals(10, actualLogs.getSize());
        verify(jobLogStorage, times(1)).getLogsForJob(jobName);
    }

    @Test
    void testGetLogsForJob_WithStatusFilter() {
        String jobName = "job1";
        JobExecutionLog successLog = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 14, 0),
                LocalDateTime.of(2025, 7, 18, 14, 2),
                120000,
                true,
                null
        );
        JobExecutionLog failedLog = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 15, 0),
                LocalDateTime.of(2025, 7, 18, 15, 3),
                180000,
                false,
                "Error occurred"
        );

        List<JobExecutionLog> storageLogs = List.of(successLog, failedLog);

        when(jobLogStorage.getLogsForJob(jobName)).thenReturn(storageLogs);

        JobExecutionFilter filter = new JobExecutionFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setStatus("SUCCESS");
        filter.setSortBy("executionTime");
        filter.setSortDirection("desc");

        PageResponse<JobExecutionLog> actualLogs =
                jobMonitoringService.getLogsForJob(jobName, filter);

        assertNotNull(actualLogs);
        assertEquals(1, actualLogs.getTotalElements());
        assertEquals(1, actualLogs.getContent().size());
        verify(jobLogStorage, times(1)).getLogsForJob(jobName);
    }

    @Test
    void testGetLogsForJob_WithDateRangeFilter() {
        String jobName = "job1";
        JobExecutionLog oldLog = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 17, 14, 0),
                LocalDateTime.of(2025, 7, 17, 14, 2),
                120000,
                true,
                null
        );
        JobExecutionLog recentLog = new JobExecutionLog(
                jobName,
                LocalDateTime.of(2025, 7, 18, 15, 0),
                LocalDateTime.of(2025, 7, 18, 15, 3),
                180000,
                true,
                null
        );

        List<JobExecutionLog> storageLogs = List.of(oldLog, recentLog);

        when(jobLogStorage.getLogsForJob(jobName)).thenReturn(storageLogs);

        JobExecutionFilter filter = new JobExecutionFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setStartDate(LocalDateTime.of(2025, 7, 18, 0, 0));
        filter.setSortBy("executionTime");
        filter.setSortDirection("desc");

        PageResponse<JobExecutionLog> actualLogs =
                jobMonitoringService.getLogsForJob(jobName, filter);

        assertNotNull(actualLogs);
        assertEquals(1, actualLogs.getTotalElements());
        assertEquals(1, actualLogs.getContent().size());
        verify(jobLogStorage, times(1)).getLogsForJob(jobName);
    }
}