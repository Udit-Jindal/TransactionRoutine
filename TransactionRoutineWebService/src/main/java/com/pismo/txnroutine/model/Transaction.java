package com.pismo.txnroutine.model;

import static com.pismo.txnroutine.exception.CustomMessages.ACCOUNT_INFO_NOT_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.AMOUNT_NOT_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.CREATED_DATE_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.UNKNOWN_OPERATION_TYPE;
import com.pismo.txnroutine.exception.InvalidOperationException;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static com.pismo.txnroutine.exception.CustomMessages.TXN_ID_NOT_PRESENT_OR_INVALID;

public abstract class Transaction
{

    // Taken transactionId as BigInt since there can be a lot of transactions.
    private final BigInteger transactionId;
    private final Account account;
    private final Operation operation;
    private final double amount;
    private final Calendar createdDate;

    public Transaction(BigInteger transactionId, Account account,
            Operation operation, double amount, Calendar createdDate)
            throws RequiredParameterMissingException
    {

        //<editor-fold defaultstate="collapsed" desc="Data validations">
        if (transactionId == null)
        {
            throw new RequiredParameterMissingException(
                    TXN_ID_NOT_PRESENT_OR_INVALID);
        }

        if (account == null)
        {
            throw new RequiredParameterMissingException(
                    ACCOUNT_INFO_NOT_PRESENT_OR_INVALID);
        }

        if (amount == 0.0)
        {
            throw new RequiredParameterMissingException(
                    AMOUNT_NOT_PRESENT_OR_INVALID);
        }

        if (createdDate == null)
        {
            throw new RequiredParameterMissingException(
                    CREATED_DATE_PRESENT_OR_INVALID);
        }
        //</editor-fold>

        this.transactionId = transactionId;
        this.account = account;
        this.operation = operation;
        this.amount = amount;
        this.createdDate = createdDate;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
    public BigInteger getTransactionId()
    {
        return transactionId;
    }

    public Account getAccount()
    {
        return account;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public Double getAmount()
    {
        return amount;
    }

    public Calendar getCreatedDate()
    {
        return createdDate;
    }

    public static Transaction getNewTxnInstance(BigInteger transactionId,
            Account account, Operation op, double amount,
            Calendar createdDate)
            throws RequiredParameterMissingException, InvalidOperationException
    {

        switch (op)
        {
            case NORMAL_PURCHASE:
                return new Purchase(transactionId, account, amount,
                        createdDate);

            case INSTALLMENT_PURCHASE:
                return new InstallmentPurchase(transactionId, account, amount,
                        createdDate);

            case WITHDRAWAL:
                return new Withdrawal(transactionId, account, amount,
                        createdDate);

            case CREDIT_VOUCHER:
                return new CreditVoucher(transactionId, account, amount,
                        createdDate);

            default:
                throw new InvalidOperationException(UNKNOWN_OPERATION_TYPE);
        }
    }

    public String getJsonString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"transaction_id\":");
        sb.append(transactionId);
        sb.append(",");

        sb.append("\"account_id\":");
        sb.append(account.getAccountId());
        sb.append(",");

        sb.append("\"operation_type\":");
        sb.append(operation.getValue());
        sb.append(",");

        sb.append("\"amount\":");
        sb.append(amount);
        sb.append(",");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        sb.append("\"event_date\":");
        sb.append("\"");
        sb.append(sdf.format(createdDate.getTime()));
        sb.append("\"");

        sb.append("}");

        return sb.toString();
    }
    //</editor-fold>
}
