package com.pismo.txnroutine.exception;

public class EntityNotFoundException extends TxnRoutineException
{

    public EntityNotFoundException(String message)
    {
        super("Entity Not Found. " + message);
    }
}
