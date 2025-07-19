package org.medtech.springbootjobmonitor.controller;

import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
