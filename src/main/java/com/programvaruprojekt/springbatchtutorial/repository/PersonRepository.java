package com.programvaruprojekt.springbatchtutorial.repository;

import com.programvaruprojekt.springbatchtutorial.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
