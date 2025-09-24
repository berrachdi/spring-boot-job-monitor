package io.github.berrachdi.springbootjobmonitor.storage;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.model.Page;
import io.github.berrachdi.springbootjobmonitor.model.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * Interface for storing and retrieving job execution logs.
 * This interface defines methods to add logs for a specific job,
 * retrieve logs for a specific job, and get all job names
 * that have logs stored.
 *
 * @author Mohamed Berrachdi
 */
public interface JobLogStorage {
    void addLog(String jobName, JobExecutionLog log);
    List<JobExecutionLog> getLogsForJob(String jobName);
    Page<JobExecutionLog> getLogsForJob(String jobName, PageRequest pageRequest);
    Map<String, List<JobExecutionLog>> getAllLogs();
    Page<JobExecutionLog> getAllLogs(PageRequest pageRequest);
}
