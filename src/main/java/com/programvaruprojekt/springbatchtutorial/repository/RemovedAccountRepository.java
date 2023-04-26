package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedAccountRepository extends JpaRepository<Account, Long> {
}
