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
public class FlatFileToDBBatchConfig extends DefaultBatchConfiguration {

    public static final String PERSONS_FILE_PATH = "persons.csv";
    public static final String ACCOUNTS_FILE_PATH = "accounts.csv";
    public static final String TRANSACTIONS_FILE_PATH = "transactions.csv";

    @Value("100")
    private Integer chunkSize;


    @Bean
    public Step personLoadStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcBatchItemWriter<Person> personLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("personLoadStep", jobRepository);
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilder
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personLoadReader())
                .writer(personLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step transactionLoadStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcBatchItemWriter<Transaction> transactionLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionLoadStep", jobRepository);
        SimpleStepBuilder<Transaction, Transaction> simpleStepBuilder = stepBuilder
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionLoadReader())
                .writer(transactionLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step accountLoadStep(DataSource dataSource,JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcBatchItemWriter<Account> accountLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountLoadStep", jobRepository);
        SimpleStepBuilder<Account, Account> simpleStepBuilder = stepBuilder
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(accountLoadReader(dataSource))
                .writer(accountLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    @Bean
    public FlatFileItemReader<Person> personLoadReader() {
        System.out.println("personLoadReader");
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
    public FlatFileItemReader<Transaction> transactionLoadReader() {
        System.out.println("transactionLoadReader");
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
    public FlatFileItemReader<Account> accountLoadReader(DataSource dataSource) {
        System.out.println("accountLoadReader");
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

    @Bean
    public JdbcBatchItemWriter<Person> personLoadWriter(DataSource dataSource) {
        System.out.println("personLoadWriter");
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Persons (id, first_name, last_name, DOB) VALUES (:id, :firstName, :lastName, :DOB)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Transaction> transactionLoadWriter(DataSource dataSource) {
        System.out.println("transactionLoadWriter");
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Transactions (id, sender, receiver, date, amount) VALUES (:id, :sender, :receiver, :date, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Account> accountLoadWriter(DataSource dataSource) {
        System.out.println("accountLoadWriter");
        return new JdbcBatchItemWriterBuilder<Account>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO Accounts (id, owner, balance) VALUES (:id, :owner, :balance)")
                .dataSource(dataSource)
                .build();
    }

}

