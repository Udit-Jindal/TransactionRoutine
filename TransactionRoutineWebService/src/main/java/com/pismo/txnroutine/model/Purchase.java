package com.pismo.txnroutine.model;

import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import java.math.BigInteger;
import java.util.Calendar;

public class Purchase extends Transaction
{

    public Purchase(BigInteger transactionId, Account account, double amount,
            Calendar createdDate) throws RequiredParameterMissingException
    {
        super(transactionId, account, Operation.NORMAL_PURCHASE,
                ((amount < 0.0) ? amount : (amount * (-1))), createdDate);
    }
}
