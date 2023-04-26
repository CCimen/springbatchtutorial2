package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import com.programvaruprojekt.springbatchtutorial.model.RemovedPerson;
import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.processors.FilterAccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterPersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.FilterTransactionItemProcessor;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import com.programvaruprojekt.springbatchtutorial.repository.TransactionRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@EnableBatchProcessing(dataSourceRef = "dataSource", transactionManagerRef = "transactionManager")
public class FilterBatchConfig extends DefaultBatchConfiguration {

    @Value("100")
    private Integer chunkSize;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private DataSource dataSource;

    @Bean
    public Step personStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcBatchItemWriter<Person> removedPersonWriter) {
        StepBuilder stepBuilder = new StepBuilder("personFilterStep", jobRepository);
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilder
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personReaderFromDatabase())
                .processor(personFilterProcessor())
                .writer(removedPersonWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step accountStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<Account> removedAccountWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountStep", jobRepository);
        SimpleStepBuilder<Account, Account> simpleStepBuilder = stepBuilder
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(accountReaderFromDatabase())
                .processor(accountFilterProcessor())
                .writer(removedAccountWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step transactionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<Transaction> removedTransactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionFilterStep", jobRepository);
        SimpleStepBuilder<Transaction, Transaction> simpleStepBuilder = stepBuilder
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionReaderFromDatabase())
                .processor(transactionFilterProcessor())
                .writer(removedTransactionWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }



    @Bean
    public JdbcCursorItemReader<Person> personReaderFromDatabase() {
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
    public JdbcCursorItemReader<Account> accountReaderFromDatabase() {
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
    public JdbcCursorItemReader<Transaction> transactionReaderFromDatabase() {
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
                .sql("INSERT INTO RemovedPersons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Transaction> removedTransactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO RemovedTransactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Account> removedAccountWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Account>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO RemovedAccounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }

}
