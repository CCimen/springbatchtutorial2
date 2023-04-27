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
    @Column(name = "DOB", nullable = false, columnDefinition = "DATE")
    private LocalDate DOB;

    public RemovedPerson(long id, String firstName, String lastName, LocalDate DOB) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
    }

    public RemovedPerson() {
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", DOB=" + DOB +
                '}';
    }
}
