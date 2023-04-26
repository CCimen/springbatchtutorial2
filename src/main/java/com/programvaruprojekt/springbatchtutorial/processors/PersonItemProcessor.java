package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.RemovedPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.programvaruprojekt.springbatchtutorial.model.Person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class PersonItemProcessor implements ItemProcessor<RemovedPerson, RemovedPerson> {
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);


    public RemovedPerson process(final RemovedPerson person) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate dob = person.getDOB();
        long years = ChronoUnit.YEARS.between(dob, currentDate);

        if (years < 18) {
            //TODO add logic to use another storage
            return null;
        }
        else {
            //delete person from the Person table
            return person;
        }
    }
}


