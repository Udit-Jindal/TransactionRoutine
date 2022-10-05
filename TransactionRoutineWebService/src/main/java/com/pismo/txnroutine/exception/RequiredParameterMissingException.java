package com.pismo.txnroutine.exception;

public class RequiredParameterMissingException extends TxnRoutineException
{

    public RequiredParameterMissingException(String message)
    {
        super("Required input parameter is missing. " + message);
    }
}
