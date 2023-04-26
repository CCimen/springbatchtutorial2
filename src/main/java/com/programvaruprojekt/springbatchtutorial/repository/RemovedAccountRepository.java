package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.RemovedAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedAccountRepository extends JpaRepository<RemovedAccount, Long> {
}
