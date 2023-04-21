package com.programvaruprojekt.springbatchtutorial.scheduler;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





@Service
public class SchedulerService {

    private final JobLauncher jobLauncher;
    private final Job job;
    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService (JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }
    @Scheduled(cron = "0 0 0/5 * * *")
    public void scheduleBatch() {

        log.info("ScheduledBatch");
        try {
            jobLauncher.run(job, new JobParameters());
        } catch (Exception e) {
            log.error("Error running batch job", e);
        }
    }
}
