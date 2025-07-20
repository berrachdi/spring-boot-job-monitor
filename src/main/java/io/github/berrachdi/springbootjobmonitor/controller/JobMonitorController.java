package io.github.berrachdi.springbootjobmonitor.controller;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing job execution logs.
 * This controller provides endpoints to retrieve logs for all jobs and for a specific job.
 *
 * @author Mohamed Berrachdi
 */
@RestController
@RequestMapping("/job-monitor")
public class JobMonitorController {
    private final JobMonitoringService monitoringService;

    public JobMonitorController(JobMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping
    public Map<String, List<JobExecutionLog>> getAllLogs() {
        return monitoringService.getAllLogs();
    }

    @GetMapping("/{jobName}")
    public List<JobExecutionLog> getLogsForJob(@PathVariable String jobName) {
        return monitoringService.getLogsForJob(jobName);
    }
}
