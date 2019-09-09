package com.cmit.payandwithdraw.configuration;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);
    @Bean(value = "token")
    public Executor tokenExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        initializeExecutor(executor);
        return executor;
    }

    @Bean(value = "pay")
    public Executor payExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        initializeExecutor(executor);
        return executor;
    }

    @Bean(value = "withdraw")
    public Executor withdrawExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        initializeExecutor(executor);
        return executor;
    }

    private void initializeExecutor(ThreadPoolTaskExecutor executor) {
        //配置核心线程数
        executor.setCorePoolSize(10);
        //配置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(500);
        //配置线程池中的线程的名称前缀
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
    }
}
