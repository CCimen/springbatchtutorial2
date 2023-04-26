package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.JobCompletionNotificationListener;
import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import com.programvaruprojekt.springbatchtutorial.model.*;
import com.programvaruprojekt.springbatchtutorial.processors.AccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.PersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.TransactionItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Configuration
@EnableBatchProcessing(dataSourceRef = "dataSource", transactionManagerRef = "transactionManager")
public class BatchConfig extends DefaultBatchConfiguration {

    public static final String PERSONS_FILE_PATH = "persons.csv";
    public static final String ACCOUNTS_FILE_PATH = "accounts.csv";
    public static final String TRANSACTIONS_FILE_PATH = "transactions.csv";

    @Value("100")
    private Integer chunkSize;

    @Value("1000")
    private Integer chunkSizeTransactions;

    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }

    @Bean
    @Primary
    public Job loadDataJob(JobRepository jobRepository,
                           JobCompletionNotificationListener listener,
                           Step loadPersonDataStep, Step loadAccountDataStep, Step loadTransactionDataStep) {
        return new JobBuilder("loadDataJob", jobRepository)
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(loadPersonDataStep)
                .next(loadAccountDataStep)
                .next(loadTransactionDataStep)
                .end()
                .build();
    }

    @Bean
    public Job filteringJob(JobRepository jobRepository,
                            JobCompletionNotificationListener listener,
                            Step filteringPersonStep, Step filteringTransactionStep, Step filteringAccountStep) {
        return new JobBuilder("filteringJob", jobRepository)
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(filteringPersonStep)
                .next(filteringAccountStep)
                .next(filteringTransactionStep)
                .end()
                .build();
    }

    /* ------------------ load data steps (for loading data job) -------------------*/
    @Bean
    public Step loadPersonDataStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   JdbcBatchItemWriter<Person> personWriter) {
        StepBuilder stepBuilder = new StepBuilder("loadPersonDataStep", jobRepository);
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilder
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personReaderFromCSV())
                .writer(personWriter)
                .listener(loggingChunkListener());

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step loadTransactionDataStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<Transaction> transactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("loadTransactionDataStep", jobRepository);
        SimpleStepBuilder<Transaction, Transaction> simpleStepBuilder = stepBuilder
                .<Transaction, Transaction>chunk(chunkSizeTransactions, transactionManager)
                .reader(transactionReaderFromCSV())
                .writer(transactionWriter)
                .listener(loggingChunkListener());

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step loadAccountDataStep(DataSource dataSource,JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<Account> accountWriter) {
        StepBuilder stepBuilder = new StepBuilder("loadAccountDataStep", jobRepository);
        SimpleStepBuilder<Account, Account> simpleStepBuilder = stepBuilder
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(accountReaderFromCSV())
                .writer(accountWriter)
                .listener(loggingChunkListener());

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    /* ------------------ filtering Steps (for filtering job) -------------------*/

    @Bean
    public Step filteringPersonStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcBatchItemWriter<RemovedPerson> personWriter) {
        StepBuilder stepBuilder = new StepBuilder("filteringPersonStep", jobRepository);
        SimpleStepBuilder<RemovedPerson, RemovedPerson> simpleStepBuilder = stepBuilder
                .<RemovedPerson, RemovedPerson>chunk(chunkSize, transactionManager)
                .reader(personReaderFromDatabase())
                .processor(personProcessor())
                .writer(personWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step filteringTransactionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<RemovedTransaction> transactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("filteringTransactionStep", jobRepository);
        SimpleStepBuilder<RemovedTransaction, RemovedTransaction> simpleStepBuilder = stepBuilder
                .<RemovedTransaction, RemovedTransaction>chunk(chunkSizeTransactions, transactionManager)
                .reader(transactionReaderFromDatabase())
                .processor(transactionProcessor())
                .writer(transactionWriter)
                .listener(loggingChunkListener());
        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step filteringAccountStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<RemovedAccount> accountWriter) {
        StepBuilder stepBuilder = new StepBuilder("filteringAccountStep", jobRepository);
        SimpleStepBuilder<RemovedAccount, RemovedAccount> simpleStepBuilder = stepBuilder
                .<RemovedAccount, RemovedAccount>chunk(chunkSize, transactionManager)
                .reader(accountReaderFromDatabase())
                .writer(accountWriter)
                .listener(loggingChunkListener());
        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    /* ----------------- Readers from CSV (for loading data job) -----------------*/


    @Bean
    public FlatFileItemReader<Person> personReaderFromCSV() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(PERSONS_FILE_PATH));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames("id", "first_name", "last_name", "DOB");
                setQuoteCharacter('\'');
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Person.class);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                setCustomEditors(Collections.singletonMap(LocalDate.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) throws IllegalArgumentException {
                        setValue(LocalDate.parse(text, dateFormatter));
                    }
                }));
            }});
        }});
        return reader;
    }

    @Bean
    public FlatFileItemReader<Transaction> transactionReaderFromCSV() {
        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(TRANSACTIONS_FILE_PATH));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames("id", "sender", "receiver", "date", "amount");
                setQuoteCharacter('\''); // Add this line to set the quote character to a single quote
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Transaction.class);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                setCustomEditors(Collections.singletonMap(LocalDate.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) throws IllegalArgumentException {
                        setValue(LocalDate.parse(text, dateFormatter));
                    }
                }));
            }});
        }});
        return reader;
    }

    @Bean
    public FlatFileItemReader<Account> accountReaderFromCSV() {
        FlatFileItemReader<Account> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(ACCOUNTS_FILE_PATH));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames("id", "owner", "balance");
                setQuoteCharacter('\''); // Add this line to set the quote character to a single quote
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Account.class);
            }});
        }});
        return reader;
    }

    /*------------------- Readers from database (for filtering job) -----------------*/

    @Bean
    public JdbcCursorItemReader<RemovedPerson> personReaderFromDatabase() {
        JdbcCursorItemReader<RemovedPerson> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, first_name, last_name, DOB FROM Persons");
        reader.setRowMapper((resultSet, rowNum) -> {
            RemovedPerson person = new RemovedPerson();
            person.setId(resultSet.getLong("id"));
            person.setFirstName(resultSet.getString("first_name"));
            person.setLastName(resultSet.getString("last_name"));
            person.setDOB(resultSet.getObject("DOB", LocalDate.class));
            return person;
        });
        return reader;
    }
    @Bean
    public JdbcCursorItemReader<RemovedAccount> accountReaderFromDatabase() {
        JdbcCursorItemReader<RemovedAccount> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, owner, balance FROM Accounts");
        reader.setRowMapper((resultSet, rowNum) -> {
            RemovedAccount account = new RemovedAccount();
            account.setId(resultSet.getLong("id"));
            account.setOwner(resultSet.getInt("owner"));
            account.setBalance(resultSet.getBigDecimal("balance"));
            return account;
        });
        return reader;
    }
    @Bean
    public ItemReader<? extends RemovedTransaction> transactionReaderFromDatabase() {
        JdbcCursorItemReader<RemovedTransaction> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, sender, receiver, date, amount FROM Transactions");
        reader.setRowMapper((resultSet, rowNum) -> {
            RemovedTransaction transaction = new RemovedTransaction();
            transaction.setId(resultSet.getLong("id"));
            transaction.setSender(resultSet.getInt("sender"));
            transaction.setReceiver(resultSet.getInt("receiver"));
            transaction.setDate(resultSet.getObject("date", LocalDate.class));
            transaction.setAmount(resultSet.getBigDecimal("amount"));
            return transaction;
        });
        return reader;
    }

    /*-------------------------------- Processors for filtering job ---------------------------*/

    @Bean
    public PersonItemProcessor personProcessor() {
        System.out.println("personProcessor");
        return new PersonItemProcessor();
    }
    @Bean
    public TransactionItemProcessor transactionProcessor() {
        System.out.println("transactionProcessor");
        return new TransactionItemProcessor();
    }

    @Bean
    public AccountItemProcessor accountProcessor() {
        System.out.println("accountProcessor");
        return new AccountItemProcessor();
    }

    /*----------------------------- ClassifingWriters for loading data job ------------------------*/

    /*@Bean
    public ClassifierCompositeItemWriter<RemovedPerson> compositeItemWriter() {
        ClassifierCompositeItemWriter<RemovedPerson> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new CustomClassifier(writerA(), writerB()));
        return compositeItemWriter;
    }
    @Bean
    public ClassifierCompositeItemWriter<RemovedAccount> compositeItemWriter() {
        ClassifierCompositeItemWriter<RemovedAccount> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new CustomClassifier(writerA(), writerB()));
        return compositeItemWriter;
    }

    @Bean
    public ClassifierCompositeItemWriter<RemovedTransaction> compositeItemWriter() {
        ClassifierCompositeItemWriter<RemovedTransaction> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new CustomClassifier(writerA(), writerB()));
        return compositeItemWriter;
    }*/



    /*-------------------------------- Writers for loading data job ---------------------------*/

    @Bean
    public JdbcBatchItemWriter<Person> personWriterToPersons(DataSource dataSource) {
        System.out.println("personWriter");
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Persons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriterToTransactions(DataSource dataSource) {
        System.out.println("transactionWriter");
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Transactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Account> accountWriterToAccounts(DataSource dataSource) {
        System.out.println("accountWriter");
        return new JdbcBatchItemWriterBuilder<Account>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Accounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }

    /* ------------------------------- Processor for removed items ------------------------*/
    /*@Bean
    public ItemProcessor<Transaction, RemovedTransaction> transactionProcessor() {
        return transaction -> {
            RemovedTransaction removedTransaction = new RemovedTransaction();
            removedTransaction.setId(transaction.getId());
            removedTransaction.setSender(transaction.getSender());
            removedTransaction.setReceiver(transaction.getReceiver());
            removedTransaction.setDate(transaction.getDate());
            removedTransaction.setAmount(transaction.getAmount());

            // You can add any additional processing logic here if needed.

            return removedTransaction;
        };
    }*/

    /*-------------------------------- Writers for removed items (filtering job) ---------------------------*/
    @Bean
    public JdbcBatchItemWriter<RemovedPerson> personWriterToRemovedPersons(DataSource dataSource) {
        System.out.println("personWriterToDatabase");
        return new JdbcBatchItemWriterBuilder<RemovedPerson>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO RemovedPersons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<RemovedTransaction> transactionWriterToRemovedTransactions(DataSource dataSource) {
        System.out.println("transactionWriterToDatabase");
        return new JdbcBatchItemWriterBuilder<RemovedTransaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO RemovedTransactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<RemovedAccount> accountWriterToRemovedAccounts(DataSource dataSource) {
        System.out.println("accountWriterToDatabase");
        return new JdbcBatchItemWriterBuilder<RemovedAccount>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO RemovedAccounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }
}
