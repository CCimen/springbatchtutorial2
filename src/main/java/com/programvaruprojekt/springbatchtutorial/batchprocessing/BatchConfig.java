package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.JobCompletionNotificationListener;
import com.programvaruprojekt.springbatchtutorial.listener.LoggingChunkListener;
import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.processors.AccountItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.PersonItemProcessor;
import com.programvaruprojekt.springbatchtutorial.processors.TransactionItemProcessor;
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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Bean
    public ChunkListener loggingChunkListener() {
        return new LoggingChunkListener();
    }

    @Bean
    @Primary
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
    public Job filteringJob(JobRepository jobRepository,
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


    /*
    @Bean
    public Step personStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcBatchItemWriter<Person> personWriter) {
        StepBuilder stepBuilder = new StepBuilder("personStep", jobRepository);
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilder
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step transactionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<Transaction> transactionWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionStep", jobRepository);
        SimpleStepBuilder<Transaction, Transaction> simpleStepBuilder = stepBuilder
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(transactionWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step accountStep(DataSource dataSource,JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<Account> accountWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountStep", jobRepository);
        SimpleStepBuilder<Account, Account> simpleStepBuilder = stepBuilder
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(accountReader(dataSource))
                .processor(accountProcessor())
                .writer(accountWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

     */

    /*
    @Bean
    public FlatFileItemReader<Person> personReader() {
        System.out.println("personReader");
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
    public FlatFileItemReader<Transaction> transactionReader() {
        System.out.println("transactionReader");
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
    public FlatFileItemReader<Account> accountReader(DataSource dataSource) {
        System.out.println("accountReader");
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

     */

    /*
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

     */

    /*
    @Bean
    public JdbcBatchItemWriter<Person> personWriter(DataSource dataSource) {
        System.out.println("personWriter");
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Persons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        System.out.println("transactionWriter");
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Transactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Account> accountWriter(DataSource dataSource) {
        System.out.println("accountWriter");
        return new JdbcBatchItemWriterBuilder<Account>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Accounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }

     */
}
