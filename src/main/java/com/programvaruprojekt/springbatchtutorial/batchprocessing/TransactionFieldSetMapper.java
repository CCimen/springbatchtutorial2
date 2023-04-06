package com.programvaruprojekt.springbatchtutorial.batchprocessing;

import com.programvaruprojekt.springbatchtutorial.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // Replace this with the correct date format in your CSV file
        Transaction transaction = new Transaction();
        transaction.setSender(fieldSet.readInt("sender"));
        transaction.setReceiver(fieldSet.readInt("receiver"));
        transaction.setDate(LocalDate.parse(fieldSet.readString("date"), formatter));
        transaction.setAmount(fieldSet.readBigDecimal("amount"));
        return transaction;
    }

}
