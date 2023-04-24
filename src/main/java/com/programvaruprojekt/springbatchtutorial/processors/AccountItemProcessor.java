package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class AccountItemProcessor implements ItemProcessor<Account, Account> {
    private static final Logger log = LoggerFactory.getLogger(AccountItemProcessor.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Account process(final Account account) throws Exception {

        if(!personRepository.existsById(account.getOwner())) {
            //TODO add logic to use another storage
            return null;
        }
        return account;
    }
}




