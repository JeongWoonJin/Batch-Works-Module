package com.assignments.finalworks.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(6); // 코어 스레드 : 초기에 해당 수 만큼 생성되며, 작업을 완료해도 유지되는 스레드
        threadPoolExecutor.setMaxPoolSize(12); // ThreadPool 에서 최대로 유지할 수 있는 Thread 개수
        threadPoolExecutor.setQueueCapacity(12); // corePoolSize 초과하는 요청에 대해 큐에 담아둔다
        threadPoolExecutor.setThreadNamePrefix("Executor-");
        threadPoolExecutor.initialize();

        return threadPoolExecutor;
    }
}
