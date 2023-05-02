package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findByReceiverIn(List<Long> accountIdsToRemove);
    List<Transaction> findBySenderIn(List<Long> accountIdsToRemove);


    //List<Transaction> findByReceiverOrSenderIs(List<Long> accountIdsToRemove);

}
