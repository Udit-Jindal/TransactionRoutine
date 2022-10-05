package com.pismo.txnroutine.exception;

public class CustomMessages
{

    // Accounts
    public static final String ACCOUNT_ID_NOT_PRESENT_OR_INVALID
            = "Account id is not present or is invalid.";
    public static final String DOCUMENT_ID_NOT_PRESENT_OR_INVALID
            = "Document number is not present or is invalid.";
    public static final String FAILED_TO_CREATE_ACCOUNT
            = "Failed to create account";
    public static final String ACCOUNT_ID_NOT_FOUND
            = "Account with Id = %s not found";
    public static final String ACCOUNT_ID_MISMATCH
            = "Account is not what was expected. \nInput Id=%s \n Id in DB=%s";

    // Transaction
    public static final String TXN_ID_NOT_PRESENT_OR_INVALID
            = "Transaction id is not present or is invalid.";
    public static final String ACCOUNT_INFO_NOT_PRESENT_OR_INVALID
            = "Account information is not present.";
    public static final String AMOUNT_NOT_PRESENT_OR_INVALID
            = "Amount is not present or is invalid.";
    public static final String CREATED_DATE_PRESENT_OR_INVALID
            = "Created date information is not present.";
    public static final String FAILED_TO_CREATE_TXN
            = "Failed to create transaction";
    public static final String TRANSACTION_ID_NOT_FOUND
            = "Transaction with Id = %s not found";
    public static final String AMOUNT_NOT_FOUND_OR_INVALID
            = "Amount is not present or Invalid";
    public static final String AMOUNT_INVALID_FOR_OPERATION_TYPE
            = "Amount is not valid for the give operation type";

    // Operation
    public static final String UNKNOWN_OPERATION_TYPE
            = "Unknown Operation Type";
    public static final String OPERATION_TYPE_MISMATCH
            = "Operation is not what was expected. \nInput Op=%d \n Op in DB=%d";
}
