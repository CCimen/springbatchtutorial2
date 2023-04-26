package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.RemovedTransaction;
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


public class TransactionItemProcessor implements ItemProcessor<RemovedTransaction, RemovedTransaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public RemovedTransaction process(final RemovedTransaction transaction) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

        log.info("sender: " + accountRepository.findById(Long.valueOf(transaction.getSender())));
        log.info("receiver: " + accountRepository.findById(Long.valueOf(transaction.getReceiver())));

          if ((months > 18) ||
                (accountRepository.findById(Long.valueOf(transaction.getSender())).isEmpty()) ||
                (accountRepository.findById(Long.valueOf(transaction.getReceiver())).isEmpty())
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

