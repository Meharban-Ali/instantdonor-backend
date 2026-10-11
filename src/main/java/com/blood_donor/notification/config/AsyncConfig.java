/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.scheduling.annotation.AsyncConfigurer
 *  org.springframework.scheduling.annotation.EnableAsync
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.blood_donor.notification.config;

import com.blood_donor.exception.AsyncExceptionHandler;
import com.blood_donor.notification.properties.AsyncProperties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig
implements AsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
    private final AsyncProperties asyncProperties;

    @Bean(name={"notificationExecutor"})
    public Executor notificationExecutor() {
        AsyncProperties.NotificationPool cfg = this.asyncProperties.getNotification();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cfg.getCorePoolSize());
        executor.setMaxPoolSize(cfg.getMaxPoolSize());
        executor.setQueueCapacity(cfg.getQueueCapacity());
        executor.setThreadNamePrefix(cfg.getThreadNamePrefix());
        executor.setKeepAliveSeconds(cfg.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(true);
        executor.setRejectedExecutionHandler((RejectedExecutionHandler)new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(cfg.getAwaitTerminationSeconds());
        executor.initialize();
        log.info("NotificationExecutor configured: core={}, max={}, queue={}, keepAlive={}s", new Object[]{cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getQueueCapacity(), cfg.getKeepAliveSeconds()});
        return executor;
    }

    @Bean(name={"schedulerAsyncExecutor"})
    public Executor schedulerAsyncExecutor() {
        AsyncProperties.SchedulerPool cfg = this.asyncProperties.getScheduler();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cfg.getCorePoolSize());
        executor.setMaxPoolSize(cfg.getMaxPoolSize());
        executor.setQueueCapacity(cfg.getQueueCapacity());
        executor.setThreadNamePrefix(cfg.getThreadNamePrefix());
        executor.setKeepAliveSeconds(cfg.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(false);
        executor.setRejectedExecutionHandler((RejectedExecutionHandler)new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(cfg.getAwaitTerminationSeconds());
        executor.initialize();
        log.info("SchedulerAsyncExecutor configured: core={}, max={}, queue={}", new Object[]{cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getQueueCapacity()});
        return executor;
    }

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    public AsyncConfig(AsyncProperties asyncProperties) {
        this.asyncProperties = asyncProperties;
    }
}

