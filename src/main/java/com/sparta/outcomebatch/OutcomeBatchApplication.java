package com.sparta.outcomebatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OutcomeBatchApplication {

    private final JobLauncher jobLauncher;
    private final Job adBatchJob;
    private final Job videoBatchJob;

    public OutcomeBatchApplication(JobLauncher jobLauncher,
                                   @Qualifier("createAdBatchJob") Job adBatchJob,
                                   @Qualifier("createVideoBatchJob") Job videoBatchJob) {
        this.jobLauncher = jobLauncher;
        this.adBatchJob = adBatchJob;
        this.videoBatchJob = videoBatchJob;
    }
    public static void main(String[] args) {
        SpringApplication.run(OutcomeBatchApplication.class, args);
    }


    @Bean
    public CommandLineRunner run() {
        return args -> {
            // 두 개의 Job을 순차적으로 실행
            jobLauncher.run(videoBatchJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());

            jobLauncher.run(adBatchJob, new JobParametersBuilder()
                    // +1 해 줌으로 job 파라미터 값이 항상 달라진다.
                    .addLong("time", System.currentTimeMillis() + 1)
                    .toJobParameters());
        };
    }


}
