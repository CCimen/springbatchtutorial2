package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.RemovedPerson;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.programvaruprojekt.springbatchtutorial.model.Person;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class FilterPersonItemProcessor implements ItemProcessor<Person, RemovedPerson>{
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Autowired
    private PersonRepository personRepository;


    public RemovedPerson process(final Person person) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate dob = person.getDOB();
        long years = ChronoUnit.YEARS.between(dob, currentDate);

        if (years > 18) {
            return null;
        }
        else {
            //Deletes from DB, writes to removed items DB
            RemovedPerson removed = new RemovedPerson(person.getId(), person.getFirstName(), person.getLastName(), person.getDOB());
            personRepository.delete(person);

            return removed;
        }
    }
}


