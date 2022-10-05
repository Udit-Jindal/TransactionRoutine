package com.pismo.txnroutine.exception;

public class InvalidOperationException extends TxnRoutineException
{

    public InvalidOperationException(String message)
    {
        super("Operation not valid. " + message);
    }
}
