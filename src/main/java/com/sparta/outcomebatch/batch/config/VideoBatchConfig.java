package com.sparta.outcomebatch.batch.config;//package com.sparta.outcome.batch.config;

import com.sparta.outcomebatch.batch.VideoBatchProcessor;
import com.sparta.outcomebatch.batch.VideoRevProcessor;
import com.sparta.outcomebatch.entity.Video;
import com.sparta.outcomebatch.entity.VideoRev;
import com.sparta.outcomebatch.entity.VideoStats;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VideoBatchConfig {


    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job createStatisticsJob(JobRepository jobRepository, Step step1, Step step2) {
        return new JobBuilder("createStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JpaPagingItemReader<Video> reader, VideoBatchProcessor videoBatchProcessor, JpaItemWriter<VideoStats> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Video, VideoStats>chunk(10, transactionManager)
                .reader(reader)
                .processor(videoBatchProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JpaPagingItemReader<Video> reader, VideoRevProcessor videoRevProcessor, JpaItemWriter<VideoRev> writer) {
        return new StepBuilder("step2", jobRepository)
                .<Video, VideoRev>chunk(10, transactionManager)
                .reader(reader)
                .processor(videoRevProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Video> reader() {
        return new JpaPagingItemReaderBuilder<Video>()
                .name("videoReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM Video v")
                .pageSize(10)
                .build();
    }

    @Bean
    public JpaItemWriter<VideoStats> writer() {
        JpaItemWriter<VideoStats> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<VideoRev> writer2() {
        JpaItemWriter<VideoRev> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
