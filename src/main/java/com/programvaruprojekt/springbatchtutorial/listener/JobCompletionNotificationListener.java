package com.programvaruprojekt.springbatchtutorial.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;


@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB  {} FINISHED !!!", jobExecution.getJobId());
        }
        else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("!!! JOB  {} FAILED !!!", jobExecution.getJobId());
        }
    }
}
