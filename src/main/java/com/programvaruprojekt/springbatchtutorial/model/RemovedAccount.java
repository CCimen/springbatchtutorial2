package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "RemovedAccounts")
public class RemovedAccount {
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

    // Add this constructor
    public RemovedAccount(long id, long owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

    // Keep the existing no-args constructor if present
    public RemovedAccount() {
    }

/*    // Getters and setters, if not already present
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
    }*/

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", owner=" + owner +
                ", balance=" + balance +
                '}';
    }
}