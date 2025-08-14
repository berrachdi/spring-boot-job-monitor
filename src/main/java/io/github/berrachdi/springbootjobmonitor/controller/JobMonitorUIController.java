package io.github.berrachdi.springbootjobmonitor.controller;

import io.github.berrachdi.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/job-monitor/ui")
public class JobMonitorUIController {

    private final JobMonitoringService monitoringService;

    public JobMonitorUIController(JobMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping
    public String getDashboard(Model model) {
        model.addAttribute("jobLogs", monitoringService.getAllLogs());
        return "job-monitor";
    }

    @GetMapping("/{jobName}")
    public String getJobDetails(@PathVariable String jobName, Model model) {
        model.addAttribute("jobName", jobName);
        model.addAttribute("logs", monitoringService.getLogsForJob(jobName));
        return "job-details";
    }
}
