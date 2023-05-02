package com.programvaruprojekt.springbatchtutorial.processors.loadToDb;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;


public class AccountItemProcessor implements ItemProcessor<Account, Account> {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Account process(final Account account) {

        if(!personRepository.existsById(account.getOwner())) {
            //TODO add logic to use another storage
            return null;
        }
        return account;
    }
}




