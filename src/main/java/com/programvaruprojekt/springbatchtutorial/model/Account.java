package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(
            name = "account_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    @Column(name = "id", unique = true, columnDefinition = "INT", updatable = false)
    private int id;
    @Column(name = "owner", nullable = false, columnDefinition = "INT")
    private int owner;
    @Column(name = "balance", nullable = false, columnDefinition = "DECIMAL(13,4)")
    private BigDecimal balance;

    // Add this constructor
    public Account(int id, int owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

    // Keep the existing no-args constructor if present
    public Account() {
    }

    // Getters and setters, if not already present
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }
    public void setOwner(int owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}