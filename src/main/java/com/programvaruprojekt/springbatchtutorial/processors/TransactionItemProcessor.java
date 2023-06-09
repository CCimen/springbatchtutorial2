package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Transaction process(final Transaction transaction) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

          if ((months > 18) ||
                  (!accountRepository.existsById(Long.valueOf(transaction.getSender()))) ||
                  (!accountRepository.existsById(Long.valueOf(transaction.getReceiver())))
        ) {

            //TODO add logic to use another storage
            return null;
        }
        else {
            //Writes to DB
            return transaction;
        }

    }
}

