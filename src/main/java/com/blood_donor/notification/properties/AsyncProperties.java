/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Min
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.annotation.Validated
 */
package com.blood_donor.notification.properties;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.async")
public class AsyncProperties {
    private NotificationPool notification = new NotificationPool();
    private SchedulerPool scheduler = new SchedulerPool();

    public NotificationPool getNotification() {
        return this.notification;
    }

    public SchedulerPool getScheduler() {
        return this.scheduler;
    }

    public void setNotification(NotificationPool notification) {
        this.notification = notification;
    }

    public void setScheduler(SchedulerPool scheduler) {
        this.scheduler = scheduler;
    }

    public static class NotificationPool {
        @Min(value=1L, message="Core pool >= 1")
        private @Min(value=1L, message="Core pool >= 1") int corePoolSize = 5;
        @Min(value=1L, message="Max pool >= 1")
        private @Min(value=1L, message="Max pool >= 1") int maxPoolSize = 20;
        @Min(value=10L, message="Queue capacity >= 10")
        private @Min(value=10L, message="Queue capacity >= 10") int queueCapacity = 100;
        @Min(value=10L, message="Keep alive >= 10 seconds")
        private @Min(value=10L, message="Keep alive >= 10 seconds") int keepAliveSeconds = 60;
        private String threadNamePrefix = "Notification-";
        @Min(value=5L, message="Termination seconds >= 5")
        private @Min(value=5L, message="Termination seconds >= 5") int awaitTerminationSeconds = 30;

        public int getCorePoolSize() {
            return this.corePoolSize;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public int getKeepAliveSeconds() {
            return this.keepAliveSeconds;
        }

        public String getThreadNamePrefix() {
            return this.threadNamePrefix;
        }

        public int getAwaitTerminationSeconds() {
            return this.awaitTerminationSeconds;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
            this.awaitTerminationSeconds = awaitTerminationSeconds;
        }
    }

    public static class SchedulerPool {
        @Min(value=1L)
        private @Min(value=1L) int corePoolSize = 3;
        @Min(value=1L)
        private @Min(value=1L) int maxPoolSize = 10;
        @Min(value=10L)
        private @Min(value=10L) int queueCapacity = 50;
        @Min(value=10L)
        private @Min(value=10L) int keepAliveSeconds = 30;
        private String threadNamePrefix = "Scheduler-Async-";
        @Min(value=5L)
        private @Min(value=5L) int awaitTerminationSeconds = 60;

        public int getCorePoolSize() {
            return this.corePoolSize;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public int getKeepAliveSeconds() {
            return this.keepAliveSeconds;
        }

        public String getThreadNamePrefix() {
            return this.threadNamePrefix;
        }

        public int getAwaitTerminationSeconds() {
            return this.awaitTerminationSeconds;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
            this.awaitTerminationSeconds = awaitTerminationSeconds;
        }
    }
}

