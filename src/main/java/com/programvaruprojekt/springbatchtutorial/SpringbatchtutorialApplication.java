package com.programvaruprojekt.springbatchtutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbatchtutorialApplication implements CommandLineRunner {
	//
	private final JobLauncher jobLauncher;
	//private final Job filterJob;
	private final Job filterCascadeJob;
	private final Job loadJob;

	public SpringbatchtutorialApplication(JobLauncher jobLauncher, Job filterCascadeJob, Job loadJob /*, Job filterJob */) {
		this.jobLauncher = jobLauncher;
	//	this.filterJob = filterJob;
		this.filterCascadeJob = filterCascadeJob;
		this.loadJob = loadJob;
	}

	public static void main(String[] args) {SpringApplication.run(SpringbatchtutorialApplication.class, args);}

	@Override
	public void run(String... args) throws Exception {
		JobExecution loadJobExecution = jobLauncher.run(loadJob, new JobParameters());
		System.out.println("Job Status: " + loadJobExecution.getStatus());
		JobExecution filterCascadeJobExecution = jobLauncher.run(filterCascadeJob, new JobParameters());
		System.out.println("Job Status: " + filterCascadeJobExecution.getStatus());

	}
}
