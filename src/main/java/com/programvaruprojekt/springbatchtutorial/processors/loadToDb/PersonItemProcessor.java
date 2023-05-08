package com.programvaruprojekt.springbatchtutorial.processors.loadToDb;

import org.springframework.batch.item.ItemProcessor;
import com.programvaruprojekt.springbatchtutorial.model.Person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
 * Person processor. Not in use
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person>{

    public Person process(final Person person)  {

        LocalDate currentDate = LocalDate.now();
        LocalDate dateOfBirth = person.getDateOfBirth();
        long years = ChronoUnit.YEARS.between(dateOfBirth, currentDate);

        if (years < 18) {
            return null;
        }
        else {
            return person;
        }
    }
}


