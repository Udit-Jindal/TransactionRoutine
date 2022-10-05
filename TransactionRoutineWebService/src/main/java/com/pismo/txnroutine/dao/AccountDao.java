package com.pismo.txnroutine.dao;

import com.pismo.txnroutine.model.Account;

public interface AccountDao
{

    Account create(String documentNumber) throws Exception;

    Account get(int accountId) throws Exception;

    Boolean delete(int accountId) throws Exception;
}
