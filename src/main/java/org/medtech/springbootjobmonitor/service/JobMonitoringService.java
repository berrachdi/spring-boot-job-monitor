package org.medtech.springbootjobmonitor.service;

import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.response.JobExecutionFilter;
import org.medtech.springbootjobmonitor.response.PageResponse;
import org.medtech.springbootjobmonitor.storage.JobLogStorage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class JobMonitoringService {

    private final JobLogStorage jobLogStorage;

    public JobMonitoringService(JobLogStorage jobLogStorage) {
        this.jobLogStorage = jobLogStorage;
    }

    public void addLog(String jobName, JobExecutionLog log) {
        jobLogStorage.addLog(jobName, log);
    }


    public PageResponse<Map.Entry<String, List<JobExecutionLog>>> getAllLogs(JobExecutionFilter filter) {
        Map<String, List<JobExecutionLog>> allLogs = jobLogStorage.getAllLogs();

        Map<String, List<JobExecutionLog>> filteredLogs = allLogs.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> filterLogs(entry.getValue(), filter)
                ))
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty()) // Remove jobs with no matching logs
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map.Entry<String, List<JobExecutionLog>>> logsList = filteredLogs.entrySet()
                .stream()
                .collect(Collectors.toList());

        if ("executionTime".equals(filter.getSortBy())) {
            Comparator<Map.Entry<String, List<JobExecutionLog>>> comparator =
                    (entry1, entry2) -> {
                        LocalDateTime time1 = getMostRecentExecutionTime(entry1.getValue());
                        LocalDateTime time2 = getMostRecentExecutionTime(entry2.getValue());
                        return time1.compareTo(time2);
                    };

            if ("desc".equalsIgnoreCase(filter.getSortDirection())) {
                comparator = comparator.reversed();
            }
            logsList.sort(comparator);
        }

        long totalElements = logsList.size();
        int start = filter.getPage() * filter.getSize();
        int end = Math.min(start + filter.getSize(), logsList.size());

        List<Map.Entry<String, List<JobExecutionLog>>> pageContent =
                start >= logsList.size() ? List.of() : logsList.subList(start, end);

        return new PageResponse<>(pageContent, filter.getPage(), filter.getSize(),
                totalElements, filter.getSortBy(), filter.getSortDirection());
    }

    public PageResponse<JobExecutionLog> getLogsForJob(String jobName, JobExecutionFilter filter) {
        List<JobExecutionLog> jobLogs = jobLogStorage.getLogsForJob(jobName);
        List<JobExecutionLog> filteredLogs = filterLogs(jobLogs, filter);

        if ("executionTime".equals(filter.getSortBy())) {
            Comparator<JobExecutionLog> comparator = Comparator.comparing(JobExecutionLog::startTime);
            if ("desc".equalsIgnoreCase(filter.getSortDirection())) {
                comparator = comparator.reversed();
            }
            filteredLogs.sort(comparator);
        } else if ("status".equals(filter.getSortBy())) {
            Comparator<JobExecutionLog> comparator = Comparator.comparing(JobExecutionLog::success);
            if ("desc".equalsIgnoreCase(filter.getSortDirection())) {
                comparator = comparator.reversed();
            }
            filteredLogs.sort(comparator);
        }

        long totalElements = filteredLogs.size();
        int start = filter.getPage() * filter.getSize();
        int end = Math.min(start + filter.getSize(), filteredLogs.size());

        List<JobExecutionLog> pageContent =
                start >= filteredLogs.size() ? List.of() : filteredLogs.subList(start, end);

        return new PageResponse<>(pageContent, filter.getPage(), filter.getSize(),
                totalElements, filter.getSortBy(), filter.getSortDirection());
    }


    private List<JobExecutionLog> filterLogs(List<JobExecutionLog> logs, JobExecutionFilter filter) {
        return logs.stream()
                .filter(log -> filter.getStatus() == null || filter.getStatus().equalsIgnoreCase(String.valueOf(log.success())))
                .filter(log -> filter.getStartDate() == null || !log.startTime().isBefore(filter.getStartDate()))
                .filter(log -> filter.getEndDate() == null || !log.endTime().isAfter(filter.getEndDate()))
                .collect(Collectors.toList());
    }


    private LocalDateTime getMostRecentExecutionTime(List<JobExecutionLog> logs) {
        return logs.stream()
                .map(JobExecutionLog::startTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
    }

    public Map<String, List<JobExecutionLog>> getAllLogsLegacy() {
        return jobLogStorage.getAllLogs();
    }

    public List<JobExecutionLog> getLogsForJobLegacy(String jobName) {
        return jobLogStorage.getLogsForJob(jobName);
    }
}