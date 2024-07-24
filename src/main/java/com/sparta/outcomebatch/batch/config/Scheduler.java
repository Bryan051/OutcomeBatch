package com.sparta.outcomebatch.batch.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.sparta.outcomebatch.batch.batchconfig.CustomJobListener.*;

@Component
public class Scheduler {

    private final JobLauncher jobLauncher;
    private final Job createVideoBatchJob;
    private final Job createAdBatchJob;
    private final TaskExecutor taskExecutor;

    public Scheduler(JobLauncher jobLauncher, Job createVideoBatchJob, Job createAdBatchJob, @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
        this.jobLauncher = jobLauncher;
        this.createVideoBatchJob = createVideoBatchJob;
        this.createAdBatchJob = createAdBatchJob;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedRate = 99999999)
    public void runJobs() {

        try {
            System.out.println("\n[순차] 잡 실행 시작");

            JobExecution sequentialExecution1 = runJob(createVideoBatchJob, "순차");
            Objects.requireNonNull(sequentialExecution1).getJobParameters();
            JobExecution sequentialExecution2 = runJob(createAdBatchJob, "순차");
            Objects.requireNonNull(sequentialExecution2).getJobParameters();

            while (sequentialExecution1.isRunning() || sequentialExecution2.isRunning()) {
                Thread.sleep(100);
            }

            System.out.println("[병렬] 잡 실행 시작\n");

            List<Runnable> tasks = new ArrayList<>();
            tasks.add(() -> runJob(createVideoBatchJob, "병렬"));
            tasks.add(() -> runJob(createAdBatchJob, "병렬"));

            CountDownLatch latch = new CountDownLatch(tasks.size());

            for (Runnable task : tasks) {
                taskExecutor.execute(() -> {
                    task.run();
                    latch.countDown();
                });
            }

            latch.await();

            printSummary();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JobExecution runJob(Job job, String mode) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("uuid", UUID.randomUUID().toString())
                    .addString("mode", mode)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            while (jobExecution.isRunning()) {
                Thread.sleep(100);
            }
            return jobExecution;
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void printSummary() {
        System.out.println("\n=== 배치 Job 병렬처리 테스트 Summary ===");
        if (sequentialJobCount > 0) {
            System.out.println("순차 총 소요시간: " + sequentialTotalTime + " ms");
        }
        if (parallelStartTime > 0 && parallelEndTime > 0) {
            long parallelTotalTime = parallelEndTime - parallelStartTime;
            System.out.println("병렬 총 소요시간: " + parallelTotalTime + " ms");
        }
        System.out.println("순차와 병렬의 총 소요시간 차이: " + (parallelEndTime - parallelStartTime - sequentialTotalTime) * (-1) + " ms");
    }
}