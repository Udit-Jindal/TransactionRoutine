package com.pismo.transactionroutine.model;

import com.pismo.transactionroutine.exception.RequiredEntityMissingException;

public class Account
{

    private final int accountId;
    private final long documentNumber;

    public Account(long documentNumber) throws RequiredEntityMissingException
    {
        if (documentNumber == 0l)
        {
            throw new RequiredEntityMissingException("Document number is not present or is invalid.");
        }

        this.accountId = getNextAccId();
        this.documentNumber = documentNumber;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
    public int getAccountId()
    {
        return accountId;
    }

    public long getDocumentNumber()
    {
        return documentNumber;
    }
    //</editor-fold>

    private static synchronized int getNextAccId()
    {
        return 1;
    }
}
