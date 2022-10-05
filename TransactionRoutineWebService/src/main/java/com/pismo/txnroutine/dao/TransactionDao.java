package com.pismo.txnroutine.dao;

import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.model.Operation;
import com.pismo.txnroutine.model.Transaction;
import java.math.BigInteger;
import java.util.Calendar;

public interface TransactionDao
{

    Transaction create(Account accountId, Operation operation, double amount,
            Calendar eventDate) throws Exception;

    Transaction get(BigInteger transactionId) throws Exception;

    Boolean delete(BigInteger transactionId) throws Exception;

}
