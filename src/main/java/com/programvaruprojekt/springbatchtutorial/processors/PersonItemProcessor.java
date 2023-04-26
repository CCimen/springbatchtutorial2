package com.programvaruprojekt.springbatchtutorial.processors;

import org.springframework.batch.item.ItemProcessor;
import com.programvaruprojekt.springbatchtutorial.model.Person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class PersonItemProcessor implements ItemProcessor<Person, Person>{

    public Person process(final Person person)  {

        LocalDate currentDate = LocalDate.now();
        LocalDate dob = person.getDOB();
        long years = ChronoUnit.YEARS.between(dob, currentDate);

        if (years < 18) {
            //TODO add logic to use another storage


            return null;
        }
        else {
            //Writes to DB
            return person;
        }
    }
}


