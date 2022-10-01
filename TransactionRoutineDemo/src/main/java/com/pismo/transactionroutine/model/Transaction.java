package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import java.math.BigInteger;
import java.util.Calendar;

public abstract class Transaction
{

    // Taken transactionId as BigInt since there can be a lot of transactions.
    private final BigInteger transactionId;
    private final Account account;
    private final Operation operation;
    private final double amount;
    private final Calendar createdDate;

    public Transaction(Account account, Operation operation, double amount, Calendar createdDate) throws RequiredEntityMissingException
    {
        if (account == null)
        {
            throw new RequiredEntityMissingException("Account information is not present.");
        }

        if (amount == 0.0)
        {
            throw new RequiredEntityMissingException("Amount is not present or is invalid.");
        }

        if (createdDate == null)
        {
            throw new RequiredEntityMissingException("Created date information is not present.");
        }

        this.transactionId = getNextTxnId();
        this.account = account;
        this.operation = operation;
        this.amount = amount;
        this.createdDate = createdDate;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
    public BigInteger getTransactionId()
    {
        return transactionId;
    }

    public Account getAccount()
    {
        return account;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public Double getAmount()
    {
        return amount;
    }

    public Calendar getCreatedDate()
    {
        return createdDate;
    }
    //</editor-fold>

    private static synchronized BigInteger getNextTxnId()
    {
        return new BigInteger("");
    }
}
