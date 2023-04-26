package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.model.*;
import com.programvaruprojekt.springbatchtutorial.processors.FilterAccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterPersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterTransactionItemProcessor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@EnableBatchProcessing
public class FilterBatchConfig extends DefaultBatchConfiguration {

    @Value("100")
    private Integer chunkSize;


    @Bean
    public Step personFilterStep(DataSource dataSource, JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcBatchItemWriter<RemovedPerson> removedPersonWriter) {
        StepBuilder stepBuilder = new StepBuilder("personFilterStep", jobRepository);
        SimpleStepBuilder<Person, RemovedPerson> simpleStepBuilder = stepBuilder
                .<Person, RemovedPerson>chunk(chunkSize, transactionManager)
                .reader(personReaderFromDatabase(dataSource))
                .processor(personFilterProcessor())
                .writer(removedPersonWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }


    @Bean
    public Step accountFilterStep(DataSource dataSource, JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<RemovedAccount> removedAccountWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountStep", jobRepository);
        SimpleStepBuilder<Account, RemovedAccount> simpleStepBuilder = stepBuilder
                .<Account, RemovedAccount>chunk(chunkSize, transactionManager)
                .reader(accountReaderFromDatabase(dataSource))
                .processor(accountFilterProcessor())
                .writer(removedAccountWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step transactionFilterStep(DataSource dataSource, JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<RemovedTransaction> removedTransactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionFilterStep", jobRepository);
        SimpleStepBuilder<Transaction, RemovedTransaction> simpleStepBuilder = stepBuilder
                .<Transaction, RemovedTransaction>chunk(chunkSize, transactionManager)
                .reader(transactionReaderFromDatabase(dataSource))
                .processor(transactionFilterProcessor())
                .writer(removedTransactionWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }



    @Bean
    public JdbcCursorItemReader<Person> personReaderFromDatabase(DataSource dataSource) {
        JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, first_name, last_name, DOB FROM Persons");
        reader.setRowMapper((resultSet, rowNum) -> {
            Person person = new Person();
            person.setId(resultSet.getLong("id"));
            person.setFirstName(resultSet.getString("first_name"));
            person.setLastName(resultSet.getString("last_name"));
            person.setDOB(resultSet.getObject("DOB", LocalDate.class));
            return person;
        });
        return reader;
    }

    @Bean
    public JdbcCursorItemReader<Account> accountReaderFromDatabase(DataSource dataSource) {
        JdbcCursorItemReader<Account> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, owner, balance FROM Accounts");
        reader.setRowMapper((resultSet, rowNum) -> {
            Account account = new Account();
            account.setId(resultSet.getLong("id"));
            account.setOwner(resultSet.getInt("owner"));
            account.setBalance(resultSet.getBigDecimal("balance"));
            return account;
        });
        return reader;
    }
    @Bean
    public JdbcCursorItemReader<Transaction> transactionReaderFromDatabase(DataSource dataSource) {
        JdbcCursorItemReader<Transaction> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, sender, receiver, date, amount FROM Transactions");
        reader.setRowMapper((resultSet, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(resultSet.getLong("id"));
            transaction.setSender(resultSet.getInt("sender"));
            transaction.setReceiver(resultSet.getInt("receiver"));
            transaction.setDate(resultSet.getObject("date", LocalDate.class));
            transaction.setAmount(resultSet.getBigDecimal("amount"));
            return transaction;
        });
        return reader;
    }

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
    public JdbcBatchItemWriter<RemovedPerson> removedPersonWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<RemovedPerson>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Removed_Persons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<RemovedTransaction> removedTransactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<RemovedTransaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Removed_Transactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<RemovedAccount> removedAccountWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<RemovedAccount>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Removed_Accounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }

}
