package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;


public class AccountItemProcessor implements ItemProcessor<Account, Account> {
    private static final Logger log = LoggerFactory.getLogger(AccountItemProcessor.class);

    @Override
    public Account process(final Account account) throws Exception {

        final BigDecimal balance = account.getBalance().divide(BigDecimal.valueOf(2));
        final Account transformedAccount = new Account(account.getId(), account.getOwner(), balance);
        return transformedAccount;
    }
}




