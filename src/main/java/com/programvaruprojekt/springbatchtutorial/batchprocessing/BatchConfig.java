package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.JobCompletionNotificationListener;
import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfiguration {

    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }

    @Bean
    public Job loadJob(JobRepository jobRepository,
                       JobCompletionNotificationListener listener,
                       Step personLoadStep, Step transactionLoadStep, Step accountLoadStep) {
        return new JobBuilder("loadJob", jobRepository)
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(personLoadStep)
                .next(accountLoadStep)
                .next(transactionLoadStep)
                .end()
                .build();
    }

    @Bean
    public Job filterJob(JobRepository jobRepository,
                         JobCompletionNotificationListener listener,
                         Step personFilterStep, Step transactionFilterStep, Step accountFilterStep) {
        return new JobBuilder("filteringJob", jobRepository)
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(personFilterStep)
                .next(accountFilterStep)
                .next(transactionFilterStep)
                .end()
                .build();
    }

}
