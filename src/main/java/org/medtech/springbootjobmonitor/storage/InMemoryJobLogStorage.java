package org.medtech.springbootjobmonitor.storage;

import org.medtech.springbootjobmonitor.model.JobExecutionLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of JobLogStorage.
 * This class stores job execution logs in memory using a ConcurrentHashMap.
 */
public class InMemoryJobLogStorage implements JobLogStorage{
    private final Map<String, List<JobExecutionLog>> logs = new ConcurrentHashMap<>();

    @Override
    public void addLog(String jobName, JobExecutionLog log) {
        logs.computeIfAbsent(jobName, k -> new ArrayList<>()).add(log);
    }
    @Override
    public List<JobExecutionLog> getLogsForJob(String jobName) {
        return logs.getOrDefault(jobName, Collections.emptyList());
    }

    @Override
    public Map<String, List<JobExecutionLog>> getAllLogs() {
          return logs;
    }
}
