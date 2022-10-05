package com.pismo.txnroutine.exception;

public class EntityDeleteFailException extends TxnRoutineException
{

    public EntityDeleteFailException(String message)
    {
        super("Entity delete failed. " + message);
    }
}
