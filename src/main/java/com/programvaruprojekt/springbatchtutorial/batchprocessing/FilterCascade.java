package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.JobCompletionNotificationListener;
import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import com.programvaruprojekt.springbatchtutorial.model.*;
import com.programvaruprojekt.springbatchtutorial.processors.cascade.PersonCascadeProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.cascade.TransactionCascadeProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableScheduling
public class FilterCascade extends DefaultBatchConfiguration {

    @Value("100")
    private Integer chunkSize;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }


    @Bean
    public Job filterCascadeJob(JobRepository jobRepository,
                                JobCompletionNotificationListener listener,
                                Step personFilterCascadeStep, Step transactionFilterCascadeStep) {
        return new JobBuilder("filterCascadeJob", jobRepository)
                //.preventRestart() //this Job does not support being started again. Restarting a Job that is not restartable causes a JobRestartException to be thrown.
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(transactionFilterCascadeStep)
                .next(personFilterCascadeStep)
                .end()
                .build();
    }

    @Bean
    public Step personFilterCascadeStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        ItemWriter<RemovedPerson> removedPersonWriter) {
        StepBuilder stepBuilder = new StepBuilder("personFilterCascadeStep", jobRepository);
        SimpleStepBuilder<Person, RemovedPerson> simpleStepBuilder = stepBuilder
                .<Person, RemovedPerson>chunk(chunkSize, transactionManager)
                .reader(personReaderFromDatabase())
                .processor(personCascadeProcessor())
                .writer(removedPersonWriter)
                //.listener(loggingChunkListener())
                ;
        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    @Bean
    public Step transactionFilterCascadeStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      ItemWriter<RemovedTransaction> removedTransactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionFilterCascadeStep", jobRepository);
        SimpleStepBuilder<Transaction, RemovedTransaction> simpleStepBuilder = stepBuilder
                .<Transaction, RemovedTransaction>chunk(chunkSize, transactionManager)
                .reader(transactionReaderFromDatabase())
                .processor(transactionFilterProcessor())
                .writer(removedTransactionWriter)
                //.listener(loggingChunkListener())
                ;
        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    /* Reads from tables */
    @Bean
    public JpaCursorItemReader<Person> personReaderFromDatabase() {
        return new JpaCursorItemReaderBuilder<Person>()
                .name("personReaderFromDatabase")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM Person p ORDER BY p.id ASC")
                .build();
    }
    @Bean
    public JpaCursorItemReader<Transaction> transactionReaderFromDatabase() {
        return new JpaCursorItemReaderBuilder<Transaction>()
                .name("transactionReaderFromDatabase")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT t FROM Transaction t ORDER BY t.id ASC")
                .build();
    }

    /* Filtering data */
    @Bean
    public PersonCascadeProcessor personCascadeProcessor() {
        return new PersonCascadeProcessor();
    }
    @Bean
    public TransactionCascadeProcessor transactionFilterProcessor() {
        return new TransactionCascadeProcessor();
    }


    /* Writes to Removed Tables */
    @Bean
    public JpaItemWriter<RemovedPerson> removedPersonWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RemovedPerson> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    @Bean
    public JpaItemWriter<RemovedTransaction> removedTransactionWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RemovedTransaction> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}
