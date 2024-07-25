package com.sparta.outcomebatch.batch.batchconfig;

import com.sparta.outcomebatch.batch.AdBatchProcessor;
import com.sparta.outcomebatch.batch.AdRevProcessor;
import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.AdStats;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final AdBatchProcessor adBatchProcessor;
    private final AdRevProcessor adRevProcessor;
    private final CustomStepListener customStepListener;
    private final CustomJobListener customJobListener;

    @Autowired
    @Qualifier("streamingEntityManagerFactory")
    private EntityManagerFactory streamingEntityManagerFactory;

    @Autowired
    @Qualifier("batchEntityManagerFactory")
    private EntityManagerFactory batchEntityManagerFactory;

    @Bean
    public Job createAdBatchJob(JobRepository jobRepository, Step adStats, Step adRev) {
        return new JobBuilder("CreateAdBatchJob", jobRepository)
                .preventRestart()
                .listener(customJobListener)
                .start(adStats)
                .next(adRev)
                .build();
    }

    @Bean
    public Step adStats(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                        @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
        return new StepBuilder("adStats", jobRepository)
                .<VideoAd, AdStats>chunk(10, transactionManager)
                .listener(customStepListener)
                .taskExecutor(taskExecutor)
                .reader(videoAdJpaPagingItemReader())
                .processor(adBatchProcessor)
                .writer(adStatsJpaItemWriter())
                .build();
    }

    @Bean
    public Step adRev(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
        return new StepBuilder("adRev", jobRepository)
                .<VideoAd, AdRev>chunk(10, transactionManager)
                .listener(customStepListener)
                .taskExecutor(taskExecutor)
                .reader(videoAdJpaPagingItemReader())
                .processor(adRevProcessor)
                .writer(adRevJpaItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<VideoAd> videoAdJpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<VideoAd>()
                .name("VideoAdReader")
                .entityManagerFactory(streamingEntityManagerFactory)
                .queryString("SELECT v FROM VideoAd v")
                .pageSize(10)
                .saveState(false)
                .build();
    }

    @Bean
    public JpaItemWriter<AdStats> adStatsJpaItemWriter() {
        JpaItemWriter<AdStats> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<AdRev> adRevJpaItemWriter() {
        JpaItemWriter<AdRev> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }


}
