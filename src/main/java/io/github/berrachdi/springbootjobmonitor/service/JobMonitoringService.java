package io.github.berrachdi.springbootjobmonitor.service;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.storage.JobLogStorage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for monitoring job executions and storing logs.
 * This service allows adding logs for specific jobs and retrieving logs for all jobs or a specific job.
 *
 * @author Mohamed Berrachdi
 */
@Service
public class JobMonitoringService {
    private final JobLogStorage jobLogStorage;

    public JobMonitoringService(JobLogStorage jobLogStorage) {
        this.jobLogStorage = jobLogStorage;
    }

    public void addLog(String jobName, JobExecutionLog log) {
        jobLogStorage.addLog(jobName, log);
    }

    public Map<String, List<JobExecutionLog>> getAllLogs() {
        return jobLogStorage.getAllLogs();
    }

    public List<JobExecutionLog> getLogsForJob(String jobName) {
        return jobLogStorage.getLogsForJob(jobName);
    }
}
