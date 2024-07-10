package com.sparta.outcomebatch.batch.config;

import com.sparta.outcomebatch.batch.AdBatchProcessor;
import com.sparta.outcomebatch.batch.AdRevProcessor;
import com.sparta.outcomebatch.entity.AdRev;
import com.sparta.outcomebatch.entity.AdStats;
import com.sparta.outcomebatch.entity.VideoAd;
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
public class AdBatchConfig {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job createAdBatchJob(JobRepository jobRepository, Step adStats, Step adRev) {
        return new JobBuilder("CreateAdBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(adStats)
                .next(adRev)
                .build();
    }

    @Bean
    public Step adStats(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JpaPagingItemReader<VideoAd> videoAdJpaPagingItemReader, AdBatchProcessor adBatchProcessor, JpaItemWriter<AdStats> adStatsJpaItemWriter) {
        return new StepBuilder("adStats", jobRepository)
                .<VideoAd, AdStats>chunk(10, transactionManager)
                .reader(videoAdJpaPagingItemReader)
                .processor(adBatchProcessor)
                .writer(adStatsJpaItemWriter)
                .build();
    }

    @Bean
    public Step adRev(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JpaPagingItemReader<VideoAd> videoAdJpaPagingItemReader, AdRevProcessor adRevProcessor, JpaItemWriter<AdRev> adRevJpaItemWriter) {
        return new StepBuilder("adRev", jobRepository)
                .<VideoAd, AdRev>chunk(10, transactionManager)
                .reader(videoAdJpaPagingItemReader)
                .processor(adRevProcessor)
                .writer(adRevJpaItemWriter)
                .build();
    }

    @Bean
    public JpaPagingItemReader<VideoAd> videoAdJpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<VideoAd>()
                .name("VideoAdReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM VideoAd v")
                .pageSize(10)
                .build();
    }

    @Bean
    public JpaItemWriter<AdStats> adStatsJpaItemWriter() {
        JpaItemWriter<AdStats> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<AdRev> adRevJpaItemWriter() {
        JpaItemWriter<AdRev> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
