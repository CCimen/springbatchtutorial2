package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedTransactionRepository extends JpaRepository<Transaction, Long> {

}
