package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    //  @GeneratedValue(strategy = GenerationType.IDENTITY)
   /* @SequenceGenerator(
            name = "account_sequence",
            sequenceName = "student_sequence",
            initialValue = 6000000,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )

    */
    @Column(name = "id", unique = true, columnDefinition = "BIGINT", updatable = false)
    private long id;
    @Column(name = "owner", nullable = false, columnDefinition = "BIGINT")
    private long owner;
    @Column(name = "balance", nullable = false, columnDefinition = "DECIMAL(13,4)")
    private BigDecimal balance;


    public Account(long id, long owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

    public Account() {
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", owner=" + owner +
                ", balance=" + balance +
                '}';
    }
}