package com.programvaruprojekt.springbatchtutorial.processors;

import com.programvaruprojekt.springbatchtutorial.model.RemovedAccount;
import com.programvaruprojekt.springbatchtutorial.model.RemovedTransaction;
import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import com.programvaruprojekt.springbatchtutorial.repository.AccountRepository;
import com.programvaruprojekt.springbatchtutorial.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class FilterTransactionItemProcessor implements ItemProcessor<Transaction, RemovedTransaction> {
    private static final Logger log = LoggerFactory.getLogger(FilterTransactionItemProcessor.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public RemovedTransaction process(final Transaction transaction) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate transactionDate = transaction.getDate();
        long months = ChronoUnit.MONTHS.between(transactionDate, currentDate);

          if ((months <= 18) &&
                  (accountRepository.existsById(Long.valueOf(transaction.getSender()))) &&
                  (accountRepository.existsById(Long.valueOf(transaction.getReceiver())))
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

