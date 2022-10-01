package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import java.util.Calendar;

public class Withdrawal extends Transaction
{
    public Withdrawal(Account account, double amount, Calendar createdDate) throws RequiredEntityMissingException
    {
        super(account, Operation.WITHDRAWAL, ((amount < 0.0) ? amount : (amount * (-1))), createdDate);
    }
}
