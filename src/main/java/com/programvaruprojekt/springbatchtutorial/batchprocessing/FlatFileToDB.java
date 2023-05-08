package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.listener.JobCompletionNotificationListener;
import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import com.programvaruprojekt.springbatchtutorial.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Configuration
public class FlatFileToDB extends DefaultBatchConfiguration {

    public static final String PERSONS_FILE_PATH = "persons_100k.csv";
    public static final String ACCOUNTS_FILE_PATH = "accounts_200k.csv";
    public static final String TRANSACTIONS_FILE_PATH = "transactions_1m.csv";

    @Value("100")
    private Integer chunkSize;


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
    public Step personLoadStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           RepositoryItemWriter<Person> personLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("personLoadStep", jobRepository);
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilder
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personLoadReader())
                .writer(personLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step accountLoadStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            RepositoryItemWriter<Account> accountLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("accountLoadStep", jobRepository);
        SimpleStepBuilder<Account, Account> simpleStepBuilder = stepBuilder
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(accountLoadReader())
                .writer(accountLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }
    @Bean
    public Step transactionLoadStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    RepositoryItemWriter<Transaction> transactionLoadWriter) {
        StepBuilder stepBuilder = new StepBuilder("transactionLoadStep", jobRepository);
        SimpleStepBuilder<Transaction, Transaction> simpleStepBuilder = stepBuilder
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionLoadReader())
                .writer(transactionLoadWriter);

        simpleStepBuilder.transactionManager(transactionManager);
        return simpleStepBuilder.build();
    }

    @Bean
    public FlatFileItemReader<Person> personLoadReader() {
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
    public FlatFileItemReader<Account> accountLoadReader() {
        FlatFileItemReader<Account> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(ACCOUNTS_FILE_PATH));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames("id", "owner", "balance");
                setQuoteCharacter('\'');
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Account.class);
            }});
        }});
        return reader;
    }
    @Bean
    public FlatFileItemReader<Transaction> transactionLoadReader() {
        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(TRANSACTIONS_FILE_PATH));
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer(",") {{
                setNames("id", "sender", "receiver", "date", "amount");
                setQuoteCharacter('\'');
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
    public RepositoryItemWriter<Person> personLoadWriter(PersonRepository personRepository) {
        RepositoryItemWriter<Person> writer = new RepositoryItemWriter<>();
        writer.setRepository(personRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public RepositoryItemWriter<Account> accountLoadWriter(AccountRepository accountRepository) {
        RepositoryItemWriter<Account> writer = new RepositoryItemWriter<>();
        writer.setRepository(accountRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public RepositoryItemWriter<Transaction> transactionLoadWriter(TransactionRepository transactionRepository) {
        RepositoryItemWriter<Transaction> writer = new RepositoryItemWriter<>();
        writer.setRepository(transactionRepository);
        writer.setMethodName("save");
        return writer;
    }

}

