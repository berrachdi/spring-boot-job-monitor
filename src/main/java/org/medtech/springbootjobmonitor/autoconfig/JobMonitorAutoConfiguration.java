package org.medtech.springbootjobmonitor.autoconfig;

import org.medtech.springbootjobmonitor.storage.InMemoryJobLogStorage;
import org.medtech.springbootjobmonitor.storage.JdbcJobLogStorage;
import org.medtech.springbootjobmonitor.storage.JobLogStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan("org.medtech.springbootjobmonitor")
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

