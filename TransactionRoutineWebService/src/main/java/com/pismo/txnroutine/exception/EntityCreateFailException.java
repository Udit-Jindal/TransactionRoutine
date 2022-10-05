package com.pismo.txnroutine.exception;

public class EntityCreateFailException extends TxnRoutineException
{

    public EntityCreateFailException(String message)
    {
        super("Entity create failed. " + message);
    }
}
