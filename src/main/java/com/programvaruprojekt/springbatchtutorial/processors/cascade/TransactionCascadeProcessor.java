package com.programvaruprojekt.springbatchtutorial.processors.cascade;

import com.programvaruprojekt.springbatchtutorial.model.RemovedTransaction;
import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class TransactionCascadeProcessor implements ItemProcessor<Transaction, RemovedTransaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionCascadeProcessor.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public RemovedTransaction process(final Transaction transaction) {

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

          if ((months <= 18)
        ) {
            return null;
        }
        else {
              //Deletes from DB, writes to removed items DB
              RemovedTransaction removed = new RemovedTransaction(transaction.getId(), transaction.getSender(), transaction.getReceiver(), transaction.getDate(), transaction.getAmount());
              transactionRepository.delete(transaction);

              return removed;
        }

    }
}

