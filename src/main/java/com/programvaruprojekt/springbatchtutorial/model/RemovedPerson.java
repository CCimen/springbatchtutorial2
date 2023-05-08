package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RemovedPersons")
public class RemovedPerson {

    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;
    @Column(name = "date_of_birth", nullable = false, columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    public RemovedPerson(long id, String firstName, String lastName, LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public RemovedPerson() {
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", DOB=" + dateOfBirth +
                '}';
    }
}
