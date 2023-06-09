package com.programvaruprojekt.springbatchtutorial.listener;


import com.programvaruprojekt.springbatchtutorial.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED !!!");

            /*
            jdbcTemplate.query("SELECT FIRST_NAME, LAST_NAME, DOB FROM Persons",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2),
                            LocalDate.parse(rs.getString(3)))
            ).forEach(person -> log.info("Found <{{}}> in the database.", person));

             */
        }
    }
}
