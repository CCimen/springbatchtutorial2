package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT")
    private long id;
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

    public Transaction(Integer sender, Integer receiver, LocalDate date, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.amount = amount;
    }
/*

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
*/

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }
}