package com.programvaruprojekt.springbatchtutorial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "RemovedTransactions")
public class RemovedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT")
    private long id;
    @Column(name = "sender", nullable = false, columnDefinition = "INT")
    private int sender;
    @Column(name = "receiver", nullable = false, columnDefinition = "INT")
    private int receiver;
    @Column(name = "date", nullable = false, columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(13,4)")
    private BigDecimal amount;

    public RemovedTransaction(int sender, int receiver, LocalDate date, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.amount = amount;
    }

    public RemovedTransaction() {
    }

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