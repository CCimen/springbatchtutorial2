package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import com.programvaruprojekt.springbatchtutorial.model.*;
import com.programvaruprojekt.springbatchtutorial.processors.FilterAccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterPersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterTransactionItemProcessor;
import com.programvaruprojekt.springbatchtutorial.repository.RemovedAccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.RemovedPersonRepository;
import com.programvaruprojekt.springbatchtutorial.repository.RemovedTransactionRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class Filter extends DefaultBatchConfiguration {

    @Value("100")
    private Integer chunkSize;

    @Autowired     //TODO: shouldn't work but does.. How to fix entityManagerFactory?
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }

    @Bean
    public Step personFilterStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ItemWriter<RemovedPerson> removedPersonWriter) {
        StepBuilder stepBuilder = new StepBuilder("personFilterStep", jobRepository);
        SimpleStepBuilder<Person, RemovedPerson> simpleStepBuilder = stepBuilder
                .<Person, RemovedPerson>chunk(chunkSize, transactionManager)
                .reader(personReaderFromDatabase())
                .processor(personFilterProcessor())
                .writer(removedPersonWriter)
                .listener(loggingChunkListener());

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    @Bean
    public Step accountFilterStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                                  ItemWriter<RemovedAccount> removedAccountWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountStep", jobRepository);
        SimpleStepBuilder<Account, RemovedAccount> simpleStepBuilder = stepBuilder
                .<Account, RemovedAccount>chunk(chunkSize, transactionManager)
                .reader(accountReaderFromDatabase())
                .processor(accountFilterProcessor())
                .writer(removedAccountWriter)
                .listener(loggingChunkListener());

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    @Bean
    public Step transactionFilterStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ItemWriter<RemovedTransaction> removedTransactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionFilterStep", jobRepository);
        SimpleStepBuilder<Transaction, RemovedTransaction> simpleStepBuilder = stepBuilder
                .<Transaction, RemovedTransaction>chunk(chunkSize, transactionManager)
                .reader(transactionReaderFromDatabase())
                .processor(transactionFilterProcessor())
                .writer(removedTransactionWriter)
                .listener(loggingChunkListener());

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
    public JpaCursorItemReader<Account> accountReaderFromDatabase() {
        return new JpaCursorItemReaderBuilder<Account>()
                .name("accountReaderFromDatabase")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Account a ORDER BY a.id ASC")
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
    public FilterPersonItemProcessor personFilterProcessor() {
        return new FilterPersonItemProcessor();
    }
    @Bean
    public FilterTransactionItemProcessor transactionFilterProcessor() {
        return new FilterTransactionItemProcessor();
    }
    @Bean
    public FilterAccountItemProcessor accountFilterProcessor() {
        return new FilterAccountItemProcessor();
    }

    /* Writes to Removed Tables */
    @Bean
    public JpaItemWriter<RemovedPerson> removedPersonWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RemovedPerson> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    @Bean
    public JpaItemWriter<RemovedAccount> removedAccountWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RemovedAccount> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    @Bean
    public JpaItemWriter<RemovedTransaction> removedTransactionWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RemovedTransaction> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /*
    //Readers: Transaction only filter every second page
    @Bean
    public JpaPagingItemReader<Person> personReaderFromDatabase() {
        JpaPagingItemReader<Person> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT p FROM Person p");
        reader.setPageSize(10);
        return reader;
    }
    @Bean
    public JpaPagingItemReader<Account> accountReaderFromDatabase() {
        JpaPagingItemReader<Account> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT a FROM Account a");
        reader.setPageSize(10);
        return reader;
    }
        @Bean
    public JpaPagingItemReader<Transaction> transactionReaderFromDatabase() {
        JpaPagingItemReader<Transaction> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT t FROM Transaction t");
        reader.setPageSize(100);
        return reader;
    }
    */

    /*
    // Works, just different kind of writer
    @Bean
    public RepositoryItemWriter<RemovedPerson> removedPersonWriter (RemovedPersonRepository removedPersonRepository) {
        RepositoryItemWriter<RemovedPerson> writer = new RepositoryItemWriter<>();
        writer.setRepository(removedPersonRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public RepositoryItemWriter<RemovedAccount> removedAccountWriter (RemovedAccountRepository removedAccountRepository) {
        RepositoryItemWriter<RemovedAccount> writer = new RepositoryItemWriter<>();
        writer.setRepository(removedAccountRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public RepositoryItemWriter<RemovedTransaction> removedTransactionWriter(RemovedTransactionRepository removedTransactionRepository) {
        RepositoryItemWriter<RemovedTransaction> writer = new RepositoryItemWriter<>();
        writer.setRepository(removedTransactionRepository);
        writer.setMethodName("save");
        return writer;
    }
    */

}
