package com.cipherinfratech.lms.security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "meetingExecutor")
    public Executor meetingExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(3);        // Teams API is slow → keep low
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("meeting-");
        executor.setKeepAliveSeconds(60);

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.initialize();
        return executor;
    }

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);        // baseline workers
        executor.setMaxPoolSize(20);        // burst handling (fan-out events)
        executor.setQueueCapacity(1000);    // buffer for spikes
        executor.setThreadNamePrefix("notification-");
        executor.setKeepAliveSeconds(60);

        // Important: don't block main thread aggressively
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return meetingExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) ->
                log.error(
                        "Async error in method: "
                                + method.getName()
                                + " message: "
                                + throwable.getMessage());
    }
}
