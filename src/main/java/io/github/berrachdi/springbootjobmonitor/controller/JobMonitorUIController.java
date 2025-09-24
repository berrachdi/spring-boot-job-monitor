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
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            Model model) {
        
        // If page or size parameters are present, use paginated view
        if (page != null || size != null) {
            int pageNum = page != null ? page : 0;
            int pageSize = size != null ? size : 20;
            
            PageRequest pageRequest = new PageRequest(pageNum, pageSize, sort, direction);
            Page<io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog> pagedLogs = 
                monitoringService.getLogsForJob(jobName, pageRequest);
            
            model.addAttribute("jobName", jobName);
            model.addAttribute("logs", pagedLogs.content());
            model.addAttribute("page", pagedLogs);
            model.addAttribute("currentSort", sort != null ? sort : "start_time");
            model.addAttribute("currentDirection", direction != null ? direction : "DESC");
        } else {
            // Non-paginated view for backward compatibility
            model.addAttribute("jobName", jobName);
            model.addAttribute("logs", monitoringService.getLogsForJob(jobName));
            model.addAttribute("page", null); // Indicates non-paginated view
        }
        
        return "job-details";
    }
}
