package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.Person;
import com.programvaruprojekt.springbatchtutorial.model.RemovedAccount;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class AccountItemProcessor implements ItemProcessor<RemovedAccount, RemovedAccount> {
    private static final Logger log = LoggerFactory.getLogger(AccountItemProcessor.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public RemovedAccount process(final RemovedAccount account) throws Exception {

        if(personRepository.findById(account.getOwner()).isEmpty()) {
            //TODO add logic to use another storage

            return null;
        }
        return account;
    }
}




