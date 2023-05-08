package com.programvaruprojekt.springbatchtutorial.processors.loadToDb;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
 * Transaction processor. Not in use
 */
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Transaction process(final Transaction transaction) {

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

          if ((months > 18) ||
                  (!accountRepository.existsById((long) transaction.getSender())) ||
                  (!accountRepository.existsById((long) transaction.getReceiver()))
        ) {
            return null;
        }
        else {
            return transaction;
        }

    }
}

