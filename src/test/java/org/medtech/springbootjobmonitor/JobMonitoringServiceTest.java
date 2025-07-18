package org.medtech.springbootjobmonitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.service.JobMonitoringService;
import org.medtech.springbootjobmonitor.storage.JobLogStorage;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testGetAllLogs_ShouldReturnAllLogs() {
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

        Map<String, List<JobExecutionLog>> actualLogs = jobMonitoringService.getAllLogs();

        assertEquals(expectedLogs, actualLogs);
        verify(jobLogStorage, times(1)).getAllLogs();
    }

    @Test
    void testGetLogsForJob_ShouldReturnLogsForGivenJob() {
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

        List<JobExecutionLog> actualLogs = jobMonitoringService.getLogsForJob(jobName);

        assertEquals(expectedLogs, actualLogs);
        verify(jobLogStorage, times(1)).getLogsForJob(jobName);
    }
}