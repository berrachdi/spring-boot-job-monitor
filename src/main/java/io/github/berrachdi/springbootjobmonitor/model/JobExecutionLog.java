package io.github.berrachdi.springbootjobmonitor.model;

import java.time.LocalDateTime;

/**
 * JobExecutionLog` is a record that represents the log of a job execution.
 * @param jobName
 * @param startTime
 * @param endTime
 * @param durationMs
 * @param success
 * @param errorMessage
 *
 * @author Mohamed Berrachdi
 */
public record JobExecutionLog(
        String jobName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationMs,
        boolean success,
        String errorMessage
) {}
