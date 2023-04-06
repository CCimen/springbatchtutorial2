package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonFieldSetMapper extends BeanWrapperFieldSetMapper<Person> {

    public PersonFieldSetMapper() {
        setTargetType(Person.class);
    }

    @Override
    public Person mapFieldSet(FieldSet fieldSet) throws BindException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // Replace this with the correct date format in your CSV file
        Person person = new Person();
        person.setFirstName(fieldSet.readString("first_name"));
        person.setLastName(fieldSet.readString("last_name"));
        person.setDOB(LocalDate.parse(fieldSet.readString("DOB"), formatter));
        return person;
    }
    
}
