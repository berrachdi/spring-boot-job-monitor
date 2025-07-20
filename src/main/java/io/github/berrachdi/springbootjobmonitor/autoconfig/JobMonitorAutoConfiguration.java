package io.github.berrachdi.springbootjobmonitor.autoconfig;

import io.github.berrachdi.springbootjobmonitor.storage.InMemoryJobLogStorage;
import io.github.berrachdi.springbootjobmonitor.storage.JdbcJobLogStorage;
import io.github.berrachdi.springbootjobmonitor.storage.JobLogStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Autoconfiguration for Spring Boot Job Monitor.
 * This configuration class sets up the job log storage mechanism.
 * It provides an in-memory storage by default,
 * but can be configured to use JDBC storage if a DataSource is available.
 *
 * @author Mohamed Berrachdi
 */
@Configuration
@ComponentScan("io.github.berrachdi.springbootjobmonitor")
public class JobMonitorAutoConfiguration {

    @Configuration
    @ConditionalOnClass(name = "javax.sql.DataSource")
    @ConditionalOnProperty(prefix = "spring.datasource", name = "url")
    static class JdbcJobLogConfiguration {
        @Bean
        @ConditionalOnMissingBean(JobLogStorage.class)
        public JobLogStorage jdbcJobLogStorage(DataSource dataSource) {
            return new JdbcJobLogStorage(new JdbcTemplate(dataSource));
        }
    }

    @Bean
    @ConditionalOnMissingBean(JobLogStorage.class)
    public JobLogStorage inMemoryJobLogStorage() {
        return new InMemoryJobLogStorage();
    }

}

