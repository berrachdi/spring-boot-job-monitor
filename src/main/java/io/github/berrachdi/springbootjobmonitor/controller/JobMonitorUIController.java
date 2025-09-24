package io.github.berrachdi.springbootjobmonitor.controller;

import io.github.berrachdi.springbootjobmonitor.model.Page;
import io.github.berrachdi.springbootjobmonitor.model.PageRequest;
import io.github.berrachdi.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getJobDetails(
            @PathVariable String jobName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            Model model) {
        
        // For backward compatibility, if no pagination params, use the old method
        if (page == 0 && size == 20 && sort == null && direction == null) {
            model.addAttribute("jobName", jobName);
            model.addAttribute("logs", monitoringService.getLogsForJob(jobName));
            model.addAttribute("page", null); // Indicates non-paginated view
        } else {
            PageRequest pageRequest = new PageRequest(page, size, sort, direction);
            Page<io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog> pagedLogs = 
                monitoringService.getLogsForJob(jobName, pageRequest);
            
            model.addAttribute("jobName", jobName);
            model.addAttribute("logs", pagedLogs.content());
            model.addAttribute("page", pagedLogs);
            model.addAttribute("currentSort", sort != null ? sort : "start_time");
            model.addAttribute("currentDirection", direction != null ? direction : "DESC");
        }
        
        return "job-details";
    }
}
