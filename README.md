# Spring Boot Job Monitor

[![Maven Central](https://img.shields.io/maven-central/v/io.github.berrachdi/spring-boot-job-monitor.svg)](https://search.maven.org/artifact/io.github.berrachdi/spring-boot-job-monitor)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot 3.5+](https://img.shields.io/badge/Spring%20Boot-3.5+-green.svg)](https://spring.io/projects/spring-boot)

Spring Boot Job Monitor is a lightweight starter library that enables **automatic monitoring of scheduled job executions** in Spring Boot applications. It provides comprehensive job execution tracking with **zero configuration** required from the client side.

## 🚀 Key Features

- **🔍 Automatic Job Monitoring**: Monitors all methods annotated with `@Scheduled` automatically
- **📊 Execution Tracking**: Records start time, end time, duration, success status, and error messages
- **💾 Flexible Storage**: Supports both in-memory and JDBC-based persistent storage
- **🌐 REST API**: Exposes job execution logs through convenient REST endpoints
- **🏷️ Custom Job Naming**: Use `@JobName` annotation for custom job identifiers
- **🔧 Zero Configuration**: Works out-of-the-box with Spring Boot auto-configuration
- **🏗️ Production Ready**: Thread-safe implementations suitable for production environments
- **📈 Observability**: Built-in metrics and logs for better application monitoring

## 📋 Table of Contents

- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Usage Examples](#-usage-examples)
- [REST API](#-rest-api)
- [Storage Options](#-storage-options)
- [Advanced Configuration](#-advanced-configuration)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

## 📦 Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.berrachdi</groupId>
    <artifactId>spring-boot-job-monitor</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
implementation 'io.github.berrachdi:spring-boot-job-monitor:0.0.1'
```

## 🚀 Quick Start

The library works automatically with zero configuration. Simply add the dependency and your scheduled jobs will be monitored:

```java
@SpringBootApplication
@EnableScheduling
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

@Component
public class MyScheduledJobs {
    
    @Scheduled(fixedRate = 60000) // Runs every minute
    public void dataProcessingJob() {
        // Your job logic here
        System.out.println("Processing data...");
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @JobName("daily-cleanup") // Custom job name
    public void cleanupJob() {
        // Your cleanup logic here
        System.out.println("Cleaning up old data...");
    }
}
```

That's it! Your jobs are now being monitored automatically.

## ⚙️ Configuration

### Storage Configuration

The library automatically chooses the storage implementation based on your application configuration:

#### In-Memory Storage (Default)
No configuration needed. Jobs logs are stored in memory and will be lost when the application restarts.

#### JDBC Storage (Automatic)
When you have a DataSource configured, the library automatically switches to JDBC storage:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
```

The library automatically creates the required `job_logs` table with the following schema:

```sql
CREATE TABLE job_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    success BOOLEAN,
    error_message TEXT
);
```

### REST API Configuration

The job monitoring endpoints are exposed at `/job-monitor` by default. You can customize this:

```yaml
# application.yml
# The endpoints will be available under your custom context path
server:
  servlet:
    context-path: /api
```

## 💡 Usage Examples

### Basic Scheduled Job

```java
@Component
public class MyJobs {
    
    @Scheduled(fixedDelay = 30000)
    public void simpleJob() {
        // This job will be automatically monitored
        // Job name will be "simpleJob" (method name)
        performSomeTask();
    }
}
```

### Custom Job Name

```java
@Component
public class MyJobs {
    
    @Scheduled(cron = "0 */15 * * * ?")
    @JobName("data-sync-job") // Custom name instead of method name
    public void synchronizeData() {
        // This job will appear as "data-sync-job" in logs
        syncExternalData();
    }
}
```

### Job with Error Handling

```java
@Component
public class MyJobs {
    
    @Scheduled(fixedRate = 120000)
    @JobName("email-processor")
    public void processEmails() throws Exception {
        // If this method throws an exception, it will be captured
        // in the job execution log with success=false
        processEmailQueue();
    }
}
```

### Accessing Job Monitoring Service

You can also programmatically access the job monitoring service:

```java
@Component
public class JobAnalytics {
    
    private final JobMonitoringService jobMonitoringService;
    
    public JobAnalytics(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }
    
    public void printJobStatistics() {
        Map<String, List<JobExecutionLog>> allLogs = jobMonitoringService.getAllLogs();
        
        allLogs.forEach((jobName, logs) -> {
            long successCount = logs.stream().filter(JobExecutionLog::success).count();
            long totalCount = logs.size();
            System.out.println("Job: " + jobName + " - Success Rate: " + 
                             (successCount * 100.0 / totalCount) + "%");
        });
    }
}
```

## 🌐 REST API

The library exposes the following REST endpoints:

### Get All Job Logs
```http
GET /job-monitor
```

**Response:**
```json
{
  "data-sync-job": [
    {
      "jobName": "data-sync-job",
      "startTime": "2024-01-15T10:30:00",
      "endTime": "2024-01-15T10:30:05",
      "durationMs": 5000,
      "success": true,
      "errorMessage": null
    }
  ],
  "email-processor": [
    {
      "jobName": "email-processor",
      "startTime": "2024-01-15T10:25:00",
      "endTime": "2024-01-15T10:25:02",
      "durationMs": 2000,
      "success": false,
      "errorMessage": "Connection timeout"
    }
  ]
}
```

### Get Logs for Specific Job
```http
GET /job-monitor/{jobName}
```

**Example:**
```http
GET /job-monitor/data-sync-job
```

**Response:**
```json
[
  {
    "jobName": "data-sync-job",
    "startTime": "2024-01-15T10:30:00",
    "endTime": "2024-01-15T10:30:05",
    "durationMs": 5000,
    "success": true,
    "errorMessage": null
  },
  {
    "jobName": "data-sync-job",
    "startTime": "2024-01-15T10:15:00",
    "endTime": "2024-01-15T10:15:03",
    "durationMs": 3000,
    "success": true,
    "errorMessage": null
  }
]
```

> **Note:** JDBC storage returns up to 50 most recent logs per job, ordered by start time (newest first).

## 💾 Storage Options

### In-Memory Storage

- **Pros**: Fast, no database dependency
- **Cons**: Data lost on application restart
- **Best for**: Development, testing, non-critical monitoring

```java
// Automatically used when no DataSource is configured
// No additional configuration needed
```

### JDBC Storage

- **Pros**: Persistent storage, survives application restarts
- **Cons**: Requires database setup
- **Best for**: Production environments

**Supported Databases:**
- H2 (in-memory and file-based)
- MySQL/MariaDB
- PostgreSQL
- Oracle
- SQL Server
- Any JDBC-compatible database

**Database Configuration Examples:**

#### H2 (In-Memory)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:jobmonitor
    driverClassName: org.h2.Driver
    username: sa
    password:
```

#### PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: postgres
    password: mypassword
    driver-class-name: org.postgresql.Driver
```

#### MySQL
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myapp
    username: root
    password: mypassword
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 🔧 Advanced Configuration

### Custom Storage Implementation

You can provide your own storage implementation:

```java
@Configuration
public class CustomJobMonitorConfig {
    
    @Bean
    @Primary
    public JobLogStorage customJobLogStorage() {
        return new CustomJobLogStorage();
    }
}

public class CustomJobLogStorage implements JobLogStorage {
    
    @Override
    public void addLog(String jobName, JobExecutionLog log) {
        // Custom storage logic (e.g., Redis, MongoDB, etc.)
    }
    
    @Override
    public List<JobExecutionLog> getLogsForJob(String jobName) {
        // Custom retrieval logic
        return Collections.emptyList();
    }
    
    @Override
    public Map<String, List<JobExecutionLog>> getAllLogs() {
        // Custom retrieval logic
        return Collections.emptyMap();
    }
}
```

### Disabling Job Monitoring

To disable job monitoring conditionally:

```java
@Configuration
public class JobMonitorConfig {
    
    @Bean
    @ConditionalOnProperty(name = "job.monitoring.enabled", havingValue = "false")
    public JobLogStorage noOpJobLogStorage() {
        return new NoOpJobLogStorage();
    }
}
```

```yaml
# application.yml
job:
  monitoring:
    enabled: false
```

### Security Configuration

To secure the monitoring endpoints:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/job-monitor/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

## 🔍 Troubleshooting

### Common Issues

#### Jobs Not Being Monitored

**Problem:** Scheduled jobs are running but not appearing in logs.

**Solutions:**
1. Ensure `@EnableScheduling` is present on your main application class
2. Verify the job method is in a Spring-managed component (`@Component`, `@Service`, etc.)
3. Check that the method is annotated with `@Scheduled`

#### Database Table Not Created

**Problem:** Using JDBC storage but table is not created automatically.

**Solutions:**
1. Verify your DataSource configuration is correct
2. Ensure the database user has CREATE TABLE permissions
3. Check application logs for any SQL exceptions during startup

#### REST Endpoints Not Available

**Problem:** Cannot access `/job-monitor` endpoints.

**Solutions:**
1. Verify Spring Boot Actuator is on the classpath
2. Check if you have custom security configuration blocking the endpoints
3. Ensure the application context path is correct

### Logging Configuration

Enable debug logging to troubleshoot issues:

```yaml
logging:
  level:
    io.github.berrachdi.springbootjobmonitor: DEBUG
```

### Performance Considerations

#### Large Number of Job Executions
- JDBC storage automatically limits results to 50 per job
- Consider implementing custom cleanup jobs for old logs
- Use database indexing on `job_name` and `start_time` columns

#### High-Frequency Jobs
- In-memory storage is more suitable for high-frequency jobs
- Consider sampling or filtering for very frequent jobs

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](https://github.com/berrachdi/spring-boot-job-monitor/blob/main/CONTRIBUTING.md) for details.

### Development Setup

1. Clone the repository
2. Build with Maven: `mvn clean install`
3. Run tests: `mvn test`

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/berrachdi/spring-boot-job-monitor/blob/main/LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Contributors and users of this library

## 📞 Support

- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/berrachdi/spring-boot-job-monitor/issues)
- 💡 **Feature Requests**: [GitHub Discussions](https://github.com/berrachdi/spring-boot-job-monitor/discussions)
- 📧 **Contact**: mohamed_berrachdi@um5.ac.ma

---

Made with ❤️ by [Mohamed Berrachdi](https://github.com/berrachdi)
