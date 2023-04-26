package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.RemovedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedTransactionRepository extends JpaRepository<RemovedTransaction, Long> {

}
