package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

    @Override
    public Transaction process(final Transaction transaction) throws Exception {
        /*
        final BigDecimal amount = transaction.getAmount().divide(BigDecimal.valueOf(2));
        final Transaction transformedTransaction = new Transaction(transaction.getReceiver(), transaction.getSender(),
                transaction.getDate(), amount);
        //log.info("Converting (" + transaction + ") into (" + transformedTransaction + ")");
        return transformedTransaction;

         */

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

        if (months > 18) {
            //log.info("Filtering transaction older than 18 months: " + transaction);
            // TODO Add logic to store the transaction in another place.
            return null;
        }
        else {
            //Writes to DB
            return transaction;
        }
    }
}

