package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Transactions")
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "INT")
    private Integer id;
    @Column(name = "sender", nullable = false, columnDefinition = "INT")
    private Integer sender;
    @Column(name = "receiver", nullable = false, columnDefinition = "INT")
    private Integer receiver;
    @Column(name = "date", nullable = false, columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(13,4)")
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(Integer id, Integer sender, Integer receiver, LocalDate date, BigDecimal amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.amount = amount;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}