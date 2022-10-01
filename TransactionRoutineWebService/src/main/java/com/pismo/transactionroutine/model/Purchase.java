package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import java.util.Calendar;

public class Purchase extends Transaction
{

    public Purchase(Account account, double amount, Calendar createdDate) throws RequiredEntityMissingException
    {
        super(account, Operation.NORMAL_PURCHASE, ((amount < 0.0) ? amount : (amount * (-1))), createdDate);
    }
}
