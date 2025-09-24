package io.github.berrachdi.springbootjobmonitor.config;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Test data configuration for demonstrating the job monitoring UI.
 * This component adds sample job execution logs when the application starts.
 * This is only for demonstration purposes.
 *
 * @author Mohamed Berrachdi
 */
@Component
public class TestDataConfiguration {

    @Autowired
    private JobMonitoringService jobMonitoringService;
    
    private final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    public void createTestData() {
        createJobExecutions("DataProcessingJob", 25, 0.85);
        createJobExecutions("EmailSenderJob", 40, 0.95);
        createJobExecutions("ReportGeneratorJob", 18, 0.75);
        createJobExecutions("DatabaseBackupJob", 12, 0.90);
        createJobExecutions("FileCleanupJob", 30, 0.98);
    }

    private void createJobExecutions(String jobName, int count, double successRate) {
        LocalDateTime baseTime = LocalDateTime.now().minusDays(7);
        
        for (int i = 0; i < count; i++) {
            LocalDateTime startTime = baseTime.plusHours(i * 6).plusMinutes(random.nextInt(60));
            long durationMs = 30000 + random.nextInt(120000); // 30s to 2.5min
            LocalDateTime endTime = startTime.plusNanos(durationMs * 1_000_000);
            boolean success = random.nextDouble() < successRate;
            String errorMessage = null;
            
            if (!success) {
                String[] errors = {
                    "Database connection timeout",
                    "File not found: /tmp/data.csv",
                    "Memory limit exceeded",
                    "Network connection failed",
                    "Permission denied accessing resource",
                    "Invalid data format in input file",
                    "Service temporarily unavailable"
                };
                errorMessage = errors[random.nextInt(errors.length)];
            }
            
            JobExecutionLog log = new JobExecutionLog(
                jobName,
                startTime,
                endTime,
                durationMs,
                success,
                errorMessage
            );
            
            jobMonitoringService.addLog(jobName, log);
        }
    }
}