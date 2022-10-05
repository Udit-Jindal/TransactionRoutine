package com.pismo.txnroutine.dao.impl.sql;

import com.pismo.txnroutine.dao.AccountDao;
import static com.pismo.txnroutine.exception.CustomMessages.ACCOUNT_ID_NOT_FOUND;
import static com.pismo.txnroutine.exception.CustomMessages.FAILED_TO_CREATE_ACCOUNT;
import com.pismo.txnroutine.exception.EntityCreateFailException;
import com.pismo.txnroutine.exception.EntityNotFoundException;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import com.pismo.txnroutine.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDaoSqlImpl implements AccountDao
{

    Connection con;

    public AccountDaoSqlImpl(Connection con)
    {
        this.con = con;
    }

    @Override
    public Account create(String documentNumber)
            throws EntityCreateFailException, SQLException,
            EntityNotFoundException, RequiredParameterMissingException
    {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO Account (document_number) Values(?)");

        stmt.setString(1, documentNumber);

        stmt.execute();

        stmt = con.prepareStatement("SELECT LAST_INSERT_ID()");

        ResultSet result = stmt.executeQuery();
        if (!result.next())
        {
            throw new EntityCreateFailException(FAILED_TO_CREATE_ACCOUNT);
        }

        int accountId = result.getInt(1);

        return this.get(accountId);
    }

    @Override
    public Account get(int accountId) throws SQLException,
            EntityNotFoundException, RequiredParameterMissingException
    {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT account_id, document_number FROM Account "
                + "WHERE account_id = ?");

        stmt.setInt(1, accountId);

        ResultSet result = stmt.executeQuery();
        if (!result.next())
        {
            throw new EntityNotFoundException(String.format(
                    ACCOUNT_ID_NOT_FOUND, String.valueOf(accountId)));
        }

        return new Account(result.getInt(1), result.getString(2));
    }

    @Override
    public Boolean delete(int accountId)
            throws EntityCreateFailException, SQLException,
            EntityNotFoundException, RequiredParameterMissingException
    {
        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM Account WHERE account_id=?");

        stmt.setInt(1, accountId);

        return stmt.execute();
    }
}
