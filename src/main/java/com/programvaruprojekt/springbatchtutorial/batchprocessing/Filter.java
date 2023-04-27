package com.programvaruprojekt.springbatchtutorial.batchprocessing;


import org.aspectj.apache.bcel.Repository;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.Properties;

import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import com.programvaruprojekt.springbatchtutorial.model.*;
import com.programvaruprojekt.springbatchtutorial.processors.FilterAccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterPersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterTransactionItemProcessor;
import com.programvaruprojekt.springbatchtutorial.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Properties;

@Configuration
@EnableBatchProcessing
public class Filter extends DefaultBatchConfiguration {

    @Value("100")
    private Integer chunkSize;
    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean
    public Step personFilterStep(DataSource dataSource, JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           RepositoryItemWriter<RemovedPerson> removedPersonWriter) {
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
                                  RepositoryItemWriter<RemovedAccount> removedAccountWriter) {
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
    public Step transactionFilterStep(DataSource dataSource, JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                RepositoryItemWriter<RemovedTransaction> removedTransactionWriter) {
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

        /* //TODO: transaction only filter every second page
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

}
