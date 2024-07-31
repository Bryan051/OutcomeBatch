package com.sparta.outcomebatch.batch.batchconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomStepListener implements StepExecutionListener {

    @Override
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("Thread {} started processing step {}", Thread.currentThread().getName(), stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Thread {} finished processing step {}", Thread.currentThread().getName(), stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }
}
