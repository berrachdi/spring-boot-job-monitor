package io.github.berrachdi.springbootjobmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Spring Boot Job Monitor.
 * This class enables AspectJ auto-proxying and scheduling support.
 * It serves as the entry point for the Spring Boot application.
 *
 * @author Mohamed Berrachdi
 */
@EnableAspectJAutoProxy
@EnableScheduling
public class SpringBootJobMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootJobMonitorApplication.class, args);
    }
}
