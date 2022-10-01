package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import java.util.Calendar;

public class InstallmentPurchase extends Transaction
{

    public InstallmentPurchase(Account account, double amount, Calendar createdDate) throws RequiredEntityMissingException
    {
        super(account, Operation.INSTALLMENT_PURCHASE, ((amount < 0.0) ? amount : (amount * (-1))), createdDate);
    }

}
