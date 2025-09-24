package io.github.berrachdi.springbootjobmonitor.storage;

import io.github.berrachdi.springbootjobmonitor.model.JobExecutionLog;
import io.github.berrachdi.springbootjobmonitor.model.Page;
import io.github.berrachdi.springbootjobmonitor.model.PageRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of JobLogStorage that uses JDBC to store job execution logs.
 *
 * @author Mohamed Berrachdi
 */
public class JdbcJobLogStorage implements JobLogStorage{
    public static final String JOB_NAME = "job_name";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String DURATION_MS = "duration_ms";
    public static final String SUCCESS = "success";
    public static final String ERROR_MESSAGE = "error_message";
    private final JdbcTemplate jdbcTemplate;

    public JdbcJobLogStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        ensureTableExists();
    }

    private void ensureTableExists() {
        try {
            jdbcTemplate.execute("SELECT 1 FROM job_logs LIMIT 1");
        } catch (DataAccessException ex) {
            jdbcTemplate.execute("""
            CREATE TABLE job_logs (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                job_name VARCHAR(255),
                start_time TIMESTAMP,
                end_time TIMESTAMP,
                duration_ms BIGINT,
                success BOOLEAN,
                error_message TEXT
            )
        """);
        }
    }

    @Override
    public void addLog(String jobName, JobExecutionLog log) {
        jdbcTemplate.update(
                "INSERT INTO job_logs (job_name, start_time, end_time, duration_ms, success, error_message) VALUES (?, ?, ?, ?, ?, ?)",
                jobName,
                log.startTime(),
                log.endTime(),
                log.durationMs(),
                log.success(),
                log.errorMessage()
        );
    }

    @Override
    public List<JobExecutionLog> getLogsForJob(String jobName) {
        return jdbcTemplate.query(
                "SELECT * FROM job_logs WHERE job_name = ? ORDER BY start_time DESC LIMIT 50",
                (rs, rowNum) -> new JobExecutionLog(
                        rs.getString(JOB_NAME),
                        rs.getTimestamp(START_TIME).toLocalDateTime(),
                        rs.getTimestamp(END_TIME).toLocalDateTime(),
                        rs.getLong(DURATION_MS),
                        rs.getBoolean(SUCCESS),
                        rs.getString(ERROR_MESSAGE)
                ),
                jobName
        );
    }

    @Override
    public Page<JobExecutionLog> getLogsForJob(String jobName, PageRequest pageRequest) {
        // Build sort clause
        String sortClause = "ORDER BY start_time DESC";
        if (pageRequest.sort() != null && !pageRequest.sort().isEmpty()) {
            String direction = pageRequest.direction() != null ? pageRequest.direction() : "DESC";
            sortClause = String.format("ORDER BY %s %s", pageRequest.sort(), direction);
        }

        // Get total count
        Long totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM job_logs WHERE job_name = ?",
                Long.class,
                jobName
        );
        long total = totalCount != null ? totalCount : 0L;

        // Get paginated results
        String sql = String.format("SELECT * FROM job_logs WHERE job_name = ? %s LIMIT ? OFFSET ?", sortClause);
        List<JobExecutionLog> logs = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new JobExecutionLog(
                        rs.getString(JOB_NAME),
                        rs.getTimestamp(START_TIME).toLocalDateTime(),
                        rs.getTimestamp(END_TIME).toLocalDateTime(),
                        rs.getLong(DURATION_MS),
                        rs.getBoolean(SUCCESS),
                        rs.getString(ERROR_MESSAGE)
                ),
                jobName,
                pageRequest.getLimit(),
                pageRequest.getOffset()
        );

        return Page.of(logs, total, pageRequest);
    }

    @Override
    public Map<String, List<JobExecutionLog>> getAllLogs() {
        List<JobExecutionLog> allLogs = jdbcTemplate.query(
                "SELECT * FROM job_logs ORDER BY start_time DESC " ,
                (rs, rowNum) -> new JobExecutionLog(
                        rs.getString(JOB_NAME),
                        rs.getTimestamp(START_TIME).toLocalDateTime(),
                        rs.getTimestamp(END_TIME).toLocalDateTime(),
                        rs.getLong(DURATION_MS),
                        rs.getBoolean(SUCCESS),
                        rs.getString(ERROR_MESSAGE)
                )
        );
        return allLogs.stream()
                .collect(Collectors.groupingBy(JobExecutionLog::jobName));
    }

    @Override
    public Page<JobExecutionLog> getAllLogs(PageRequest pageRequest) {
        // Build sort clause
        String sortClause = "ORDER BY start_time DESC";
        if (pageRequest.sort() != null && !pageRequest.sort().isEmpty()) {
            String direction = pageRequest.direction() != null ? pageRequest.direction() : "DESC";
            sortClause = String.format("ORDER BY %s %s", pageRequest.sort(), direction);
        }

        // Get total count
        Long totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM job_logs", Long.class);
        long total = totalCount != null ? totalCount : 0L;

        // Get paginated results
        String sql = String.format("SELECT * FROM job_logs %s LIMIT ? OFFSET ?", sortClause);
        List<JobExecutionLog> logs = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new JobExecutionLog(
                        rs.getString(JOB_NAME),
                        rs.getTimestamp(START_TIME).toLocalDateTime(),
                        rs.getTimestamp(END_TIME).toLocalDateTime(),
                        rs.getLong(DURATION_MS),
                        rs.getBoolean(SUCCESS),
                        rs.getString(ERROR_MESSAGE)
                ),
                pageRequest.getLimit(),
                pageRequest.getOffset()
        );

        return Page.of(logs, total, pageRequest);
    }
}
