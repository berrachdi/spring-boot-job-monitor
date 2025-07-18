package org.medtech.springbootjobmonitor.service;

import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.storage.JobLogStorage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for monitoring job executions and storing logs.
 * This service allows adding logs for specific jobs and retrieving logs for all jobs or a specific job.
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
