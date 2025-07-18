package org.medtech.springbootjobmonitor.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.medtech.springbootjobmonitor.annotation.JobName;
import org.medtech.springbootjobmonitor.model.JobExecutionLog;
import org.medtech.springbootjobmonitor.service.JobMonitoringService;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspect to monitor scheduled jobs and log their execution details.
 * This aspect intercepts methods annotated with @Scheduled,
 * records their execution time, success status, and any error messages,
 * and stores this information in a JobExecutionLog.
 */
@Component
@Aspect
public class JobMonitorAspect {
    private final JobMonitoringService jobMonitoringService;

    public JobMonitorAspect(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }

    @Around("@annotation(scheduled)")
    public Object monitorScheduledJob(ProceedingJoinPoint joinPoint, Scheduled scheduled) throws Throwable {
        String jobName = joinPoint.getSignature().getName();

        JobName customJobName = AnnotationUtils.findAnnotation(
                joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName()), JobName.class);
        if (customJobName != null) {
            jobName = customJobName.value();
        }

        LocalDateTime start = LocalDateTime.now();
        boolean success = true;
        String errorMessage = null;

        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            success = false;
            errorMessage = ex.getMessage();
            throw ex;
        } finally {
            LocalDateTime end = LocalDateTime.now();
            long duration = java.time.Duration.between(start, end).toMillis();

            JobExecutionLog log = new JobExecutionLog(jobName, start, end, duration, success, errorMessage);

            jobMonitoringService.addLog(jobName, log);
    }
    }
}
