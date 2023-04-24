package com.programvaruprojekt.springbatchtutorial.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.programvaruprojekt.springbatchtutorial.model.Person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class PersonItemProcessor implements ItemProcessor<Person, Person>{
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);


    public Person process(final Person person) throws Exception {
        /*
        final long id = person.getId();
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(id, firstName, lastName, person.getDOB());

        //log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;

         */

        LocalDate currentDate = LocalDate.now();
        LocalDate dob = person.getDOB();
        long years = ChronoUnit.YEARS.between(dob, currentDate);

        if (years < 18) {
            //log.info("Filtering person younger than 18 years: " + person);
            // TODO Add logic to store the person in another place.
            return null;
        }
        else {
            //Writes to DB
            return person;
        }
    }
}


