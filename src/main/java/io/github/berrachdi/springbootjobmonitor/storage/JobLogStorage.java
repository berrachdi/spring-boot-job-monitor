package io.github.berrachdi.springbootjobmonitor.storage;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;

import java.util.List;
import java.util.Map;

/**
 * Interface for storing and retrieving job execution logs.
 * This interface defines methods to add logs for a specific job,
 * retrieve logs for a specific job, and get all job names
 * that have logs stored.
 */
public interface JobLogStorage {
    void addLog(String jobName, JobExecutionLog log);
    List<JobExecutionLog> getLogsForJob(String jobName);
    Map<String, List<JobExecutionLog>> getAllLogs();
}
