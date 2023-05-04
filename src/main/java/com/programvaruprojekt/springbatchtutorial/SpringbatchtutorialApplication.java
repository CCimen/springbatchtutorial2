package com.programvaruprojekt.springbatchtutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class SpringbatchtutorialApplication implements CommandLineRunner {
	private final JobLauncher jobLauncher;
	private final Job filterCascadeJob;
	private final Job loadJob;

	public SpringbatchtutorialApplication(JobLauncher jobLauncher, Job filterCascadeJob, Job loadJob) {
		this.jobLauncher = jobLauncher;
		this.filterCascadeJob = filterCascadeJob;
		this.loadJob = loadJob;
	}

	public static void main(String[] args) {SpringApplication.run(SpringbatchtutorialApplication.class, args);}


	/**
	 * When the application starts this method loads the data into the database
	 * @param args incoming main method arguments
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {
		JobExecution loadJobExecution = jobLauncher.run(loadJob, new JobParameters());
		System.out.println("Job Status: " + loadJobExecution.getStatus());
	}

	/**
	 * Scheduled start of batch job "FilterCascade"
	 * @throws Exception
	 */
	@Scheduled (fixedDelay = 10000, initialDelay = 10000)
	//(cron = "0 0 16 * * *")  //För cron-uttryck: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
	public void runFilter() throws Exception {
		JobExecution filterCascadeJobExecution = jobLauncher.run(filterCascadeJob, new JobParameters());
		System.out.println("Job Status: " + filterCascadeJobExecution.getStatus());
	}

}
