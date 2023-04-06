package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;


public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

    @Override
    public Transaction process(final Transaction transaction) throws Exception {
        final BigDecimal amount = transaction.getAmount().divide(BigDecimal.valueOf(2)); //halverar amount för test
        final Transaction transformedTransaction = new Transaction(transaction.getReceiver(), transaction.getSender(),
                transaction.getDate(), amount);
        log.info("Converting (" + transaction + ") into (" + transformedTransaction + ")");
        return transformedTransaction;
    }



}




