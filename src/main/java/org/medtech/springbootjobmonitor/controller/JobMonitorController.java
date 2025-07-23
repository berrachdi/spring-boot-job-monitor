package org.medtech.springbootjobmonitor.controller;

import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.response.JobExecutionFilter;
import org.medtech.springbootjobmonitor.response.PageResponse;
import org.medtech.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/job-monitor")
public class JobMonitorController {

    private final JobMonitoringService monitoringService;

    public JobMonitorController(JobMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Get all logs with pagination and filtering support
     *
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param status Filter by execution status (optional)
     * @param startDate Filter logs from this date (optional)
     * @param endDate Filter logs until this date (optional)
     * @param sortBy Sort field (default: executionTime)
     * @param sortDir Sort direction (default: desc)
     * @return Paginated logs for all jobs
     */
    @GetMapping
    public PageResponse<Map.Entry<String, List<JobExecutionLog>>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "executionTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        JobExecutionFilter filter = new JobExecutionFilter(status, startDate, endDate, sortBy, sortDir, page, size);
        return monitoringService.getAllLogs(filter);
    }

    /**
     * Get logs for a specific job with pagination and filtering support
     *
     * @param jobName Name of the job
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param status Filter by execution status (optional)
     * @param startDate Filter logs from this date (optional)
     * @param endDate Filter logs until this date (optional)
     * @param sortBy Sort field (default: executionTime)
     * @param sortDir Sort direction (default: desc)
     * @return Paginated logs for the specified job
     */
    @GetMapping("/{jobName}")
    public PageResponse<JobExecutionLog> getLogsForJob(
            @PathVariable String jobName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "executionTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        JobExecutionFilter filter = new JobExecutionFilter(status, startDate, endDate, sortBy, sortDir, page, size);
        return monitoringService.getLogsForJob(jobName, filter);
    }

    /**
     * Legacy endpoint for backward compatibility - returns all logs without pagination
     */
    @GetMapping("/all")
    public Map<String, List<JobExecutionLog>> getAllLogsLegacy() {
        return monitoringService.getAllLogsLegacy();
    }

    /**
     * Legacy endpoint for backward compatibility - returns all logs for a job without pagination
     */
    @GetMapping("/{jobName}/all")
    public List<JobExecutionLog> getLogsForJobLegacy(@PathVariable String jobName) {
        return monitoringService.getLogsForJobLegacy(jobName);
    }
}