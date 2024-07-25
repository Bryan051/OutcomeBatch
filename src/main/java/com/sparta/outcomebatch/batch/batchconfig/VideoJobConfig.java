package com.sparta.outcomebatch.batch.batchconfig;//package com.sparta.outcome.batch.config;

import com.sparta.outcomebatch.batch.VideoBatchProcessor;
import com.sparta.outcomebatch.batch.VideoRevProcessor;
import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoRev;
import com.sparta.outcomebatch.batch.domain.VideoStats;
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
public class VideoJobConfig {


    private final EntityManagerFactory entityManagerFactory;
    private final VideoBatchProcessor videoBatchProcessor;
    private final VideoRevProcessor videoRevProcessor;
    private final CustomJobListener customJobListener;
    private final CustomStepListener customStepListener;

    @Autowired
    @Qualifier("streamingEntityManagerFactory")
    private EntityManagerFactory streamingEntityManagerFactory;

    @Autowired
    @Qualifier("batchEntityManagerFactory")
    private EntityManagerFactory batchEntityManagerFactory;

    @Bean
    public Job createVideoBatchJob(JobRepository jobRepository, Step videoStats, Step videoRev) {
        return new JobBuilder("CreateVideoBatchJob", jobRepository)
                .preventRestart()
                .listener(customJobListener)
                .start(videoStats)
                .next(videoRev)
                .build();
    }

    @Bean
    public Step videoStats(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
        return new StepBuilder("videoStats", jobRepository)
                .<Video, VideoStats>chunk(10, transactionManager)
                .listener(customStepListener)
                .taskExecutor(taskExecutor)
                .reader(videoReader())
                .processor(videoBatchProcessor)
                .writer(videoStatsJpaItemWriter())
                .build();
    }

    @Bean
    public Step videoRev(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                         @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
        return new StepBuilder("videoRev", jobRepository)
                .<Video, VideoRev>chunk(10, transactionManager)
                .listener(customStepListener)
                .taskExecutor(taskExecutor)
                .reader(videoReader())
                .processor(videoRevProcessor)
                .writer(videoRevJpaItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Video> videoReader() {
        return new JpaPagingItemReaderBuilder<Video>()
                .name("videoReader")
                .entityManagerFactory(streamingEntityManagerFactory)
//                .queryString("SELECT DISTINCT v FROM Video v LEFT JOIN FETCH v.videoViews vv WHERE FUNCTION('DATE', vv.createdAt) = :date")
//                .parameterValues(Collections.singletonMap("date", LocalDate.of(2024,7,17)))
                .queryString("SELECT v FROM Video v")
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






}


//    @Bean
//    public Job createVideoBatchJob(JobRepository jobRepository, Step videoStats, Step videoRev) {
//        return new JobBuilder("CreateVideoBatchJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(videoStats)
//                .next(videoRev)
//                .build();
//    }