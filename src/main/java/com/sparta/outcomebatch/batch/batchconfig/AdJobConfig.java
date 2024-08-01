package com.sparta.outcomebatch.batch.batchconfig;

import com.sparta.outcomebatch.batch.*;
import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.AdStats;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;


@Slf4j
@Configuration
public class AdJobConfig {

    private final PlatformTransactionManager batchTransactionManager;
    private final PlatformTransactionManager streamingTransactionManager;
    private final AdBatchProcessor adBatchProcessor;
    private final AdRevProcessor adRevProcessor;
    private final CustomStepListener customStepListener;
    private final CustomJobListener customJobListener;
    private final VideoAdUpdateProcessor videoAdUpdateProcessor;
    private final EntityManagerFactory streamingEntityManagerFactory;
    private final EntityManagerFactory batchEntityManagerFactory;
    @Autowired
    public AdJobConfig(
            @Qualifier("streamingEntityManagerFactory") EntityManagerFactory streamingEntityManagerFactory,
            @Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory,
            AdBatchProcessor adBatchProcessor,
            AdRevProcessor adRevProcessor,
            CustomJobListener customJobListener,
            CustomStepListener customStepListener,
            VideoAdUpdateProcessor videoAdUpdateProcessor,
            @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager,
            @Qualifier("streamingTransactionManager") PlatformTransactionManager streamingTransactionManager) {
        this.streamingEntityManagerFactory = streamingEntityManagerFactory;
        this.batchEntityManagerFactory = batchEntityManagerFactory;
        this.adBatchProcessor = adBatchProcessor;
        this.adRevProcessor = adRevProcessor;
        this.customJobListener = customJobListener;
        this.customStepListener = customStepListener;
        this.videoAdUpdateProcessor = videoAdUpdateProcessor;
        this.batchTransactionManager = batchTransactionManager;
        this.streamingTransactionManager = streamingTransactionManager;
    }

    @Bean
    public Job createAdBatchJob(JobRepository jobRepository, Step adStatsMasterStep, Step adRevMasterStep, Step videoAdUpdateMasterStep) {
        return new JobBuilder("CreateAdBatchJob", jobRepository)
                .preventRestart()
                .listener(customJobListener)
                .start(adStatsMasterStep)
                .next(adRevMasterStep)
                .next(videoAdUpdateMasterStep)
                .build();
    }

    @Bean
    public Step adStatsMasterStep(JobRepository jobRepository, Step adStatsSlaveStep,
                                     @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                     VideoAdPartitioner videoAdPartitioner) {
        return new StepBuilder("adStatsMasterStep", jobRepository)
                .partitioner("adStatsSlaveStep", videoAdPartitioner)
                .gridSize(14)
                .step(adStatsSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step adRevMasterStep(JobRepository jobRepository, Step adRevSlaveStep,
                                  @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                  VideoAdPartitioner videoAdPartitioner) {
        return new StepBuilder("adRevMasterStep", jobRepository)
                .partitioner("adRevSlaveStep", videoAdPartitioner)
                .gridSize(14)
                .step(adRevSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step videoAdUpdateMasterStep(JobRepository jobRepository, Step videoAdUpdateSlaveStep,
                                @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                VideoAdPartitioner videoAdPartitioner) {
        return new StepBuilder("adStatsMasterStep", jobRepository)
                .partitioner("adStatsSlaveStep", videoAdPartitioner)
                .gridSize(14)
                .step(videoAdUpdateSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step adStatsSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("adStats", jobRepository)
                .<VideoAd, AdStats>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoAdJpaPagingItemReader(null,null))
                .processor(adBatchProcessor)
                .writer(adStatsJpaItemWriter())
                .build();
    }

    @Bean
    public Step adRevSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("adRev", jobRepository)
                .<VideoAd, AdRev>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoAdJpaPagingItemReader(null,null))
                .processor(adRevProcessor)
                .writer(adRevJpaItemWriter())
                .build();
    }

    @Bean
    public Step videoAdUpdateSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("videoAdUpdate", jobRepository)
                .<VideoAd, VideoAd>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoAdUpdateReader(null,null))
                .processor(videoAdUpdateProcessor)
                .writer(videoAdUpdateWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<VideoAd> videoAdJpaPagingItemReader(@Value("#{stepExecutionContext['minValue']}") Integer minValue,
                                                                   @Value("#{stepExecutionContext['maxValue']}") Integer maxValue) {
        return new JpaPagingItemReaderBuilder<VideoAd>()
                .name("VideoAdReader")
                .entityManagerFactory(streamingEntityManagerFactory)
                .queryString("SELECT v FROM VideoAd v WHERE v.id BETWEEN :minValue AND :maxValue")
                .parameterValues(Map.of("minValue", minValue, "maxValue", maxValue))
                .pageSize(10)
                .saveState(false)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<VideoAd> videoAdUpdateReader(@Value("#{stepExecutionContext['minValue']}") Integer minValue,
                                                            @Value("#{stepExecutionContext['maxValue']}") Integer maxValue) {
        return new JpaPagingItemReaderBuilder<VideoAd>()
                .name("VideoAdReader")
                .entityManagerFactory(batchEntityManagerFactory)
                .queryString("SELECT v FROM VideoAd v WHERE v.id BETWEEN :minValue AND :maxValue")
                .parameterValues(Map.of("minValue", minValue, "maxValue", maxValue))
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

    @Bean
    public JpaItemWriter<VideoAd> videoAdUpdateWriter() {
        JpaItemWriter<VideoAd> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }

}
