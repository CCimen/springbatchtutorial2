package com.programvaruprojekt.springbatchtutorial.processors.separateFilter;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import com.programvaruprojekt.springbatchtutorial.model.RemovedAccount;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;


public class FilterAccountItemProcessor implements ItemProcessor<Account, RemovedAccount> {
    private static final Logger log = LoggerFactory.getLogger(FilterAccountItemProcessor.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public RemovedAccount process(final Account account) {

        if(personRepository.existsById(account.getOwner())) {
            return null;
        }
        else {
            //Deletes from DB, writes to removed items DB
            RemovedAccount removed = new RemovedAccount(account.getId(), account.getOwner(), account.getBalance());
            accountRepository.delete(account);

            return removed;
        }
    }
}




