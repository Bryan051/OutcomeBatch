package com.sparta.outcomebatch.batch.batchconfig;//package com.sparta.outcome.batch.config;

import com.sparta.outcomebatch.batch.VideoBatchProcessor;
import com.sparta.outcomebatch.batch.VideoRevProcessor;
import com.sparta.outcomebatch.batch.VideoUpdateProcessor;
import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoRev;
import com.sparta.outcomebatch.batch.domain.VideoStats;
import jakarta.persistence.EntityManagerFactory;
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
public class VideoJobConfig {

    private final VideoBatchProcessor videoBatchProcessor;
    private final VideoRevProcessor videoRevProcessor;
    private final CustomJobListener customJobListener;
    private final CustomStepListener customStepListener;
    private final VideoUpdateProcessor videoUpdateProcessor;
    private final EntityManagerFactory streamingEntityManagerFactory;
    private final EntityManagerFactory batchEntityManagerFactory;
    private final PlatformTransactionManager batchTransactionManager;
    private final PlatformTransactionManager streamingTransactionManager;


    @Autowired
    public VideoJobConfig(
            @Qualifier("streamingEntityManagerFactory") EntityManagerFactory streamingEntityManagerFactory,
            @Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory,
            VideoBatchProcessor videoBatchProcessor,
            VideoRevProcessor videoRevProcessor,
            CustomJobListener customJobListener,
            CustomStepListener customStepListener,
            VideoUpdateProcessor videoUpdateProcessor,
            @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager,
            @Qualifier("streamingTransactionManager") PlatformTransactionManager streamingTransactionManager) {
        this.streamingEntityManagerFactory = streamingEntityManagerFactory;
        this.batchEntityManagerFactory = batchEntityManagerFactory;
        this.videoBatchProcessor = videoBatchProcessor;
        this.videoRevProcessor = videoRevProcessor;
        this.customJobListener = customJobListener;
        this.customStepListener = customStepListener;
        this.videoUpdateProcessor = videoUpdateProcessor;
        this.batchTransactionManager = batchTransactionManager;
        this.streamingTransactionManager = streamingTransactionManager;
    }

    @Bean
    public Job createVideoBatchJob(JobRepository jobRepository,
                                   Step videoStatsMasterStep,
                                   Step videoRevMasterStep,
                                   Step videoUpdateMasterStep) {
        return new JobBuilder("CreateVideoBatchJob", jobRepository)
                .preventRestart()
                .listener(customJobListener)
                .start(videoStatsMasterStep)
                .next(videoRevMasterStep)
                .next(videoUpdateMasterStep)
                .build();
    }

    @Bean
    public Step videoStatsMasterStep(JobRepository jobRepository, Step videoStatsSlaveStep,
                                     @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                     VideoPartitioner videoPartitioner) {
        return new StepBuilder("videoStatsMasterStep", jobRepository)
                .partitioner("videoStatsSlaveStep", videoPartitioner)
                .gridSize(8)
                .step(videoStatsSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step videoRevMasterStep(JobRepository jobRepository, Step videoRevSlaveStep,
                                   @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                   VideoPartitioner videoPartitioner) {
        return new StepBuilder("videoRevMasterStep", jobRepository)
                .partitioner("videoRevSlaveStep", videoPartitioner)
                .gridSize(8)
                .step(videoRevSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step videoUpdateMasterStep(JobRepository jobRepository, Step videoUpdateSlaveStep,
                                      @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor,
                                      VideoPartitioner videoPartitioner) {
        return new StepBuilder("videoUpdateMasterStep", jobRepository)
                .partitioner("videoUpdateSlaveStep", videoPartitioner)
                .gridSize(8)
                .step(videoUpdateSlaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step videoStatsSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("videoStatsSlaveStep", jobRepository)
                .<Video, VideoStats>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoReader(null,null))// 초기값은 null로 설정, 이후 partitioner에서 설정
                .processor(videoBatchProcessor)
                .writer(videoStatsJpaItemWriter())
                .build();
    }

    @Bean
    public Step videoRevSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("videoRevSlaveStep", jobRepository)
                .<Video, VideoRev>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoReader(null,null))
                .processor(videoRevProcessor)
                .writer(videoRevJpaItemWriter())
                .build();
    }

    @Bean
    public Step videoUpdateSlaveStep(JobRepository jobRepository) {
        return new StepBuilder("videoUpdateSlaveStep", jobRepository)
                .<Video, Video>chunk(10, batchTransactionManager)
                .listener(customStepListener)
//                .taskExecutor(taskExecutor)
                .reader(videoUpdateReader(null,null))
                .processor(videoUpdateProcessor) // 뷰 업데이트 프로세서
                .writer(videoUpdateJpaItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Video> videoReader(@Value("#{stepExecutionContext['minValue']}") Integer minValue,
                                                  @Value("#{stepExecutionContext['maxValue']}") Integer maxValue) {
        return new JpaPagingItemReaderBuilder<Video>()
                .name("videoReader")
                .entityManagerFactory(streamingEntityManagerFactory)
                .queryString("SELECT v FROM Video v WHERE v.id BETWEEN :minValue AND :maxValue")
                .parameterValues(Map.of("minValue", minValue, "maxValue", maxValue))
                .pageSize(10)
                .saveState(false)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Video> videoUpdateReader(@Value("#{stepExecutionContext['minValue']}") Integer minValue,
                                                  @Value("#{stepExecutionContext['maxValue']}") Integer maxValue) {
        return new JpaPagingItemReaderBuilder<Video>()
                .name("videoUpdateReader")
                .entityManagerFactory(batchEntityManagerFactory)
                .queryString("SELECT v FROM Video v WHERE v.id BETWEEN :minValue AND :maxValue")
                .parameterValues(Map.of("minValue", minValue, "maxValue", maxValue))
                .pageSize(10)
                .saveState(false)
                .build();
    }

    @Bean
    public JpaItemWriter<VideoStats> videoStatsJpaItemWriter() {
        JpaItemWriter<VideoStats> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<VideoRev> videoRevJpaItemWriter() {
        JpaItemWriter<VideoRev> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<Video> videoUpdateJpaItemWriter() {
        JpaItemWriter<Video> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
        return jpaItemWriter;
    }






}


//    @Bean
//    public Job createVideoBatchJob(JobRepository jobRepository, Step videoStats, Step videoRev) {
//        return new JobBuilder("CreateVideoBatchJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(videoStats)
//                .next(videoRev)
//                .build();
//    }