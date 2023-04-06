package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionFilterProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionFilterProcessor.class);

    @Override
    public Transaction process(final Transaction transaction) throws Exception {
        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

        if (months > 18) {
            //TODO: Lägg logs i en egen textfil

            log.info("Filtering transaction older than 18 months: " + transaction);
            // Add logic to store the transaction in another place.
            //TODO:
            return null;
        }
        else {
            //ingenting
            return transaction;
        }
    }
}
