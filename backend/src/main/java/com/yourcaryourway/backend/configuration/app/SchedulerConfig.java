package com.yourcaryourway.backend.configuration.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class for task scheduling.
 * Provides a TaskScheduler bean for scheduling one-time timeout checks.
 */
@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // allow up to 5 concurrent scheduled tasks
        scheduler.setThreadNamePrefix("chat-timeout-");
        return scheduler;
    }

}