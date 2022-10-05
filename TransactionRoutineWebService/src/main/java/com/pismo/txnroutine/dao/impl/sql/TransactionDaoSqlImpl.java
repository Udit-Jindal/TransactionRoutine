package com.pismo.txnroutine.dao.impl.sql;

import com.pismo.txnroutine.dao.TransactionDao;
import static com.pismo.txnroutine.exception.CustomMessages.TRANSACTION_ID_NOT_FOUND;
import com.pismo.txnroutine.exception.EntityCreateFailException;
import com.pismo.txnroutine.exception.EntityNotFoundException;
import com.pismo.txnroutine.exception.InvalidOperationException;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.model.Operation;
import com.pismo.txnroutine.model.Transaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import static com.pismo.txnroutine.exception.CustomMessages.FAILED_TO_CREATE_TXN;

public class TransactionDaoSqlImpl implements TransactionDao
{

    Connection con;

    public TransactionDaoSqlImpl(Connection con)
    {
        this.con = con;
    }

    @Override
    public Transaction create(Account account, Operation operation, double amount,
            Calendar eventDate) throws EntityCreateFailException, SQLException,
            EntityNotFoundException, RequiredParameterMissingException,
            InvalidOperationException
    {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO Transaction (account_id,operation_type,amount,"
                + "event_date) Values(?,?,?,?)");

        stmt.setInt(1, account.getAccountId());
        stmt.setInt(2, operation.getValue());
        stmt.setDouble(3, amount);
        stmt.setTimestamp(4, new Timestamp(eventDate.getTimeInMillis()));

        stmt.execute();

        stmt = con.prepareStatement("SELECT LAST_INSERT_ID()");

        ResultSet result = stmt.executeQuery();
        if (!result.next())
        {
            throw new EntityCreateFailException(FAILED_TO_CREATE_TXN);
        }

        BigInteger txnId = result.getBigDecimal(1).toBigInteger();

        return this.get(txnId);
    }

    @Override
    public Transaction get(BigInteger transactionId) throws SQLException,
            EntityNotFoundException, RequiredParameterMissingException,
            InvalidOperationException
    {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT txn_id, account_id, operation_type,amount,event_date "
                + "FROM Transaction WHERE txn_id = ?");

        BigDecimal txnId = new BigDecimal(transactionId);

        stmt.setBigDecimal(1, txnId);

        ResultSet result = stmt.executeQuery();
        if (!result.next())
        {
            throw new EntityNotFoundException(String.format(
                    TRANSACTION_ID_NOT_FOUND, String.valueOf(transactionId)));
        }

        Calendar evetTimeStamp = Calendar.getInstance();
        evetTimeStamp.setTimeInMillis(result.getTimestamp(5).getTime());

        return Transaction.getNewTxnInstance(result.getBigDecimal(1).toBigInteger(),
                (new AccountDaoSqlImpl(con)).get(result.getInt(2)),
                Operation.parse(result.getInt(3)),
                result.getDouble(4), evetTimeStamp);
    }

    @Override
    public Boolean delete(BigInteger transactionId) throws Exception
    {
        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM Transaction WHERE txn_id=?");

        BigDecimal txnId = new BigDecimal(transactionId);

        stmt.setBigDecimal(1, txnId);

        return stmt.execute();
    }

}
