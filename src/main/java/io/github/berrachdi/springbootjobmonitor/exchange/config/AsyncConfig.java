package io.github.berrachdi.springbootjobmonitor.exchange.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configures thread pools for async matching operations and scheduled rate fetching.
 * Pool sizes are externalised via {@code application.yml} under {@code exchange.engine.*}.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Value("${exchange.engine.matching-threads:4}")
    private int matchingThreads;

    @Value("${exchange.engine.matching-threads-max:8}")
    private int matchingThreadsMax;

    @Value("${exchange.engine.matching-threads-queue:100}")
    private int matchingThreadsQueue;

    @Value("${exchange.engine.scheduler-threads:3}")
    private int schedulerThreads;

    private ThreadPoolTaskExecutor matchingExecutor;
    private ThreadPoolTaskScheduler rateScheduler;

    /**
     * Thread pool for {@code @Async} matching engine operations.
     * Uses {@link ThreadPoolExecutor.CallerRunsPolicy} so that no orders are silently dropped
     * when the queue is full — the caller thread processes the task instead.
     */
    @Bean(name = "matchingExecutor")
    public ThreadPoolTaskExecutor matchingExecutor() {
        matchingExecutor = new ThreadPoolTaskExecutor();
        matchingExecutor.setCorePoolSize(matchingThreads);
        matchingExecutor.setMaxPoolSize(matchingThreadsMax);
        matchingExecutor.setQueueCapacity(matchingThreadsQueue);
        matchingExecutor.setThreadNamePrefix("matching-engine-");
        matchingExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        matchingExecutor.setWaitForTasksToCompleteOnShutdown(true);
        matchingExecutor.setAwaitTerminationSeconds(30);
        matchingExecutor.initialize();
        return matchingExecutor;
    }

    /**
     * Scheduler for {@code @Scheduled} exchange rate fetching tasks.
     */
    @Bean(name = "rateScheduler")
    public ThreadPoolTaskScheduler rateScheduler() {
        rateScheduler = new ThreadPoolTaskScheduler();
        rateScheduler.setPoolSize(schedulerThreads);
        rateScheduler.setThreadNamePrefix("rate-scheduler-");
        rateScheduler.setWaitForTasksToCompleteOnShutdown(true);
        rateScheduler.setAwaitTerminationSeconds(30);
        rateScheduler.initialize();
        return rateScheduler;
    }

    @PreDestroy
    public void shutdown() {
        if (matchingExecutor != null) {
            matchingExecutor.shutdown();
        }
        if (rateScheduler != null) {
            rateScheduler.shutdown();
        }
    }
}
