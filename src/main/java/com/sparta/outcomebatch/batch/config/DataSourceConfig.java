package com.sparta.outcomebatch.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class DataSourceConfig {
    @Autowired
    private Environment env;

//    @Bean
//    @Primary
//    public DataSource batchDataSource() {
//        return batchDataSourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean
//    @Primary
//    @ConfigurationProperties("spring.datasource.batch")
//    public DataSourceProperties batchDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource streamingDataSource() {
//        return streamingDataSourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean
//    @ConfigurationProperties("spring.datasource.streaming")
//    public DataSourceProperties streamingDataSourceProperties() {
//        return new DataSourceProperties();
//    }

    @Bean(name = "batchDataSource")
    @Primary
    public DataSource batchDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.batch.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.batch.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.batch.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.batch.driver-class-name"));
        dataSource.setMaximumPoolSize(env.getProperty("spring.datasource.batch.hikari.maximum-pool-size", Integer.class));
        dataSource.setMinimumIdle(env.getProperty("spring.datasource.batch.hikari.minimum-idle", Integer.class));
        dataSource.setIdleTimeout(env.getProperty("spring.datasource.batch.hikari.idle-timeout", Long.class));
        dataSource.setConnectionTimeout(env.getProperty("spring.datasource.batch.hikari.connection-timeout", Long.class));
        dataSource.setMaxLifetime(env.getProperty("spring.datasource.batch.hikari.max-lifetime", Long.class));
        dataSource.setKeepaliveTime(env.getProperty("spring.datasource.batch.hikari.keepalive-time", Long.class));
        dataSource.setValidationTimeout(env.getProperty("spring.datasource.batch.hikari.validation-timeout", Long.class));
        return dataSource;
    }

    @Bean(name = "streamingDataSource")
    public DataSource streamingDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.streaming.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.streaming.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.streaming.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.streaming.driver-class-name"));
        dataSource.setMaximumPoolSize(env.getProperty("spring.datasource.streaming.hikari.maximum-pool-size", Integer.class));
        dataSource.setMinimumIdle(env.getProperty("spring.datasource.streaming.hikari.minimum-idle", Integer.class));
        dataSource.setIdleTimeout(env.getProperty("spring.datasource.streaming.hikari.idle-timeout", Long.class));
        dataSource.setConnectionTimeout(env.getProperty("spring.datasource.streaming.hikari.connection-timeout", Long.class));
        dataSource.setMaxLifetime(env.getProperty("spring.datasource.streaming.hikari.max-lifetime", Long.class));
        dataSource.setKeepaliveTime(env.getProperty("spring.datasource.streaming.hikari.keepalive-time", Long.class));
        dataSource.setValidationTimeout(env.getProperty("spring.datasource.streaming.hikari.validation-timeout", Long.class));
        return dataSource;
    }



    @Bean(name = "batchTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int numOfCores = Runtime.getRuntime().availableProcessors();
        // 원하는 cpu 사용비율
        float targetCpuUtilization = 0.7f;
        // I/O bound 비율
        float blockingCoefficient = 1.0f;
        int corePoolSize = (int) (numOfCores * targetCpuUtilization * (1 + blockingCoefficient));
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("batchTaskExecutor-");
        // shutdown 상태가 아니라면 ThreadPoolTaskExecutor에 요청한 thread에서 직접 처리한다.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 유실 없이 마지막까지 다 처리하고 종료되길 원한다면 설정을 추가해야 한다.
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        // Allow core threads to time out
        executor.setAllowCoreThreadTimeOut(true);

        log.info("Initialized ThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());

        // 실행 중인 스레드 수를 주기적으로 로그에 기록
        new Thread(() -> {
            while (true) {
                log.info("Active Threads: {}", executor.getActiveCount());
                log.info("Pool Size: {}", executor.getPoolSize());
                log.info("Queue Size: {}", executor.getThreadPoolExecutor().getQueue().size());
                try {
                    Thread.sleep(10000); // 10초마다 로그 기록
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        return executor;
    }

}

//    @Bean
//    public JobRepository jobRepository(@Qualifier("batchDataSource") DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
//        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setTransactionManager(transactionManager);
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }

//    @Bean
//    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
//        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
//        jobLauncher.setJobRepository(jobRepository);
//        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        jobLauncher.afterPropertiesSet();
//        return jobLauncher;
//    }