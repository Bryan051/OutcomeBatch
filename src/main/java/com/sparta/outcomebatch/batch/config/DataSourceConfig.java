package com.sparta.outcomebatch.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource batchDataSource() {
        return batchDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource streamingDataSource() {
        return streamingDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.streaming")
    public DataSourceProperties streamingDataSourceProperties() {
        return new DataSourceProperties();
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

    @Bean(name = "batchTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(8); // m1 cpu 수
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("batchTaskExecutor-");
        executor.initialize();
        log.info("Initialized ThreadPoolTaskExecutor with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }

}