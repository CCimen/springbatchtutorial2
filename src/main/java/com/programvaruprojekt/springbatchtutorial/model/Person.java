package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;
    @Column(name = "DOB", nullable = false, columnDefinition = "DATE")
    private LocalDate DOB;

    public Person(String firstName, String lastName, LocalDate DOB) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
    }

    public Person() {

    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

}
