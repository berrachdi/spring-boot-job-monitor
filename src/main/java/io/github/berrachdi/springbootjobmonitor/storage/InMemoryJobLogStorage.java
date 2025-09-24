package io.github.berrachdi.springbootjobmonitor.storage;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.model.Page;
import io.github.berrachdi.springbootjobmonitor.model.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of JobLogStorage.
 * This class stores job execution logs in memory using a ConcurrentHashMap.
 *
 * @author Mohamed Berrachdi
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
    public Page<JobExecutionLog> getLogsForJob(String jobName, PageRequest pageRequest) {
        List<JobExecutionLog> jobLogs = logs.getOrDefault(jobName, Collections.emptyList());
        
        // Apply sorting
        List<JobExecutionLog> sortedLogs = jobLogs.stream()
                .sorted(getSortComparator(pageRequest))
                .collect(Collectors.toList());
        
        return paginateList(sortedLogs, pageRequest);
    }

    @Override
    public Map<String, List<JobExecutionLog>> getAllLogs() {
          return logs;
    }

    @Override
    public Page<JobExecutionLog> getAllLogs(PageRequest pageRequest) {
        List<JobExecutionLog> allLogs = logs.values().stream()
                .flatMap(List::stream)
                .sorted(getSortComparator(pageRequest))
                .collect(Collectors.toList());
        
        return paginateList(allLogs, pageRequest);
    }

    private Comparator<JobExecutionLog> getSortComparator(PageRequest pageRequest) {
        Comparator<JobExecutionLog> comparator;
        
        // Default sort by start_time
        if (pageRequest.sort() == null || pageRequest.sort().isEmpty() || "start_time".equals(pageRequest.sort())) {
            comparator = Comparator.comparing(JobExecutionLog::startTime);
        } else if ("end_time".equals(pageRequest.sort())) {
            comparator = Comparator.comparing(JobExecutionLog::endTime);
        } else if ("duration_ms".equals(pageRequest.sort())) {
            comparator = Comparator.comparing(JobExecutionLog::durationMs);
        } else if ("job_name".equals(pageRequest.sort())) {
            comparator = Comparator.comparing(JobExecutionLog::jobName);
        } else {
            // Default fallback
            comparator = Comparator.comparing(JobExecutionLog::startTime);
        }
        
        // Apply direction (default is DESC for start_time)
        if (pageRequest.direction() == null || "DESC".equalsIgnoreCase(pageRequest.direction())) {
            comparator = comparator.reversed();
        }
        
        return comparator;
    }

    private <T> Page<T> paginateList(List<T> list, PageRequest pageRequest) {
        int total = list.size();
        int startIndex = (int) pageRequest.getOffset();
        int endIndex = Math.min(startIndex + pageRequest.getLimit(), total);
        
        List<T> pageContent = (startIndex < total) 
            ? list.subList(startIndex, endIndex) 
            : Collections.emptyList();
        
        return Page.of(pageContent, total, pageRequest);
    }
}
