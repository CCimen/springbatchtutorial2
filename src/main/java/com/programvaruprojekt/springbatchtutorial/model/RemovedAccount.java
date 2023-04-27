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
    @Column(name = "id", unique = true, columnDefinition = "BIGINT", updatable = false)
    private long id;
    @Column(name = "owner", nullable = false, columnDefinition = "BIGINT")
    private long owner;
    @Column(name = "balance", nullable = false, columnDefinition = "DECIMAL(13,4)")
    private BigDecimal balance;


    public RemovedAccount(long id, long owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

    public RemovedAccount() {
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