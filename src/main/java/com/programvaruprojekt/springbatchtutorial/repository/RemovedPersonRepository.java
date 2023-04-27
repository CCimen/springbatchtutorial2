package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.RemovedPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedPersonRepository extends JpaRepository<RemovedPerson, Long> {
}
