package com.pismo.transactionroutine.exception;

public class RequiredEntityMissingException extends TxnRoutineException
{

    public RequiredEntityMissingException(String message)
    {
        super("Required Entity is missing. " + message);
    }
}
