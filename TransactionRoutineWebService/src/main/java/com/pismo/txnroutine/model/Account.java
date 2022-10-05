package com.pismo.txnroutine.model;

import static com.pismo.txnroutine.exception.CustomMessages.ACCOUNT_ID_NOT_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.DOCUMENT_ID_NOT_PRESENT_OR_INVALID;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;

public class Account
{

    private final int accountId;
    private final String documentNumber;

    public Account(int accountId, String documentNumber)
            throws RequiredParameterMissingException
    {
        //<editor-fold defaultstate="collapsed" desc="Data validation">
        if (accountId == 0)
        {
            throw new RequiredParameterMissingException(
                    ACCOUNT_ID_NOT_PRESENT_OR_INVALID);
        }

        if ((documentNumber == null) || documentNumber.isEmpty())
        {
            throw new RequiredParameterMissingException(
                    DOCUMENT_ID_NOT_PRESENT_OR_INVALID);
        }
        //</editor-fold>

        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
    public int getAccountId()
    {
        return accountId;
    }

    public String getDocumentNumber()
    {
        return documentNumber;
    }

    public String getJsonString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"account_id\":");
        sb.append(accountId);
        sb.append(",");

        sb.append("\"document_number\":");
        sb.append("\"");
        sb.append(documentNumber);
        sb.append("\"");

        sb.append("}");

        return sb.toString();
    }
    //</editor-fold>
}
