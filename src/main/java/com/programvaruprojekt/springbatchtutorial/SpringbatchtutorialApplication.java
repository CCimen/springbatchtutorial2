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
	private final Job job;
	public SpringbatchtutorialApplication(JobLauncher jobLauncher, Job job) {
		this.jobLauncher = jobLauncher;
		this.job = job;
	}

	public static void main(String[] args) {SpringApplication.run(SpringbatchtutorialApplication.class, args);}

	@Override
	public void run(String... args) throws Exception {
		JobExecution execution = jobLauncher.run(job, new JobParameters());
		System.out.println("Job Status: " + execution.getStatus());
	}
}
