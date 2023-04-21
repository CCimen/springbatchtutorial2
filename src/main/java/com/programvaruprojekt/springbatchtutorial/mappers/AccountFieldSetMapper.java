package com.programvaruprojekt.springbatchtutorial.mappers;

import com.programvaruprojekt.springbatchtutorial.model.Account;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


/*public class AccountFieldSetMapper implements FieldSetMapper<Account> {

    @Override
    public Account mapFieldSet(FieldSet fieldSet) throws BindException {
        Account account = new Account();
        account.setId(fieldSet.readInt("id"));
        account.setOwner(fieldSet.readInt("owner"));
        account.setBalance(fieldSet.readBigDecimal("balance"));
        return account;
    }
}*/
