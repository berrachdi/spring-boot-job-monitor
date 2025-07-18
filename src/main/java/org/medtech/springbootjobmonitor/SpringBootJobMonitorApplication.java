package org.medtech.springbootjobmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableScheduling
public class SpringBootJobMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootJobMonitorApplication.class, args);
    }
}
