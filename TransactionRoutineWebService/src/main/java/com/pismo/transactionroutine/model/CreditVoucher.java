package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import java.util.Calendar;

public class CreditVoucher extends Transaction
{

    public CreditVoucher(Account account, double amount, Calendar createdDate) throws RequiredEntityMissingException
    {
        super(account, Operation.CREDIT_VOUCHER, ((amount > 0.0) ? amount : (amount * (-1))), createdDate);
    }
}
