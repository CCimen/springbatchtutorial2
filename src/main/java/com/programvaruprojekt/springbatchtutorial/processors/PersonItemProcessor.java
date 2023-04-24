package com.programvaruprojekt.springbatchtutorial.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.programvaruprojekt.springbatchtutorial.model.Person;

import java.time.LocalDate;


public class PersonItemProcessor implements ItemProcessor<Person, Person>{
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);


    public Person process(final Person person) throws Exception {
        // If the person is under 18, throw an exception
        if (person.getDOB().plusYears(18).isBefore(LocalDate.now())) {
            return new Person(person.getFirstName(), person.getLastName(), person.getDOB());
        } else {
            return null;
        }
    }
}


