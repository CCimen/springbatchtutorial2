package com.programvaruprojekt.springbatchtutorial;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbatchtutorialApplication implements CommandLineRunner {
	private final JobLauncher jobLauncher;
	private final Job filterJob;
	private final Job loadJob;

	public SpringbatchtutorialApplication(JobLauncher jobLauncher, Job filterJob, Job loadJob) {
		this.jobLauncher = jobLauncher;
		this.filterJob = filterJob;
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

		JobExecution filterJobExecution = jobLauncher.run(filterJob, new JobParameters()); // incrementer.getNext(new JobParameters()));
		System.out.println("Job Status: " + filterJobExecution.getStatus());
	}

	/**
	 * Scheduled start of batch job "FilterCascade"
	 * @throws Exception
	 */
/*
	@Scheduled
			//(cron = "0 04 11 * * *")  //Cron expressions: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
			(fixedDelay = 100000, initialDelay = 10000)
	public void runFilter() throws Exception {
		JobExecution filterCascadeJobExecution = jobLauncher.run(filterCascadeJob, new JobParameters()); // incrementer.getNext(new JobParameters()));
		System.out.println("Job Status: " + filterCascadeJobExecution.getStatus());
	}

	JobParametersIncrementer incrementer = parameters -> new JobParametersBuilder(parameters)
			.addString("time.id", LocalDateTime.now().toString())
			.toJobParameters();


 */


}
