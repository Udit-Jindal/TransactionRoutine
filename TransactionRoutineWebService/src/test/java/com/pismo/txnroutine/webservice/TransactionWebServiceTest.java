package com.pismo.txnroutine.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.txnroutine.dao.AccountDao;
import com.pismo.txnroutine.dao.TransactionDao;
import com.pismo.txnroutine.dao.impl.sql.AccountDaoSqlImpl;
import com.pismo.txnroutine.dao.impl.sql.TransactionDaoSqlImpl;
import static com.pismo.txnroutine.exception.CustomMessages.ACCOUNT_ID_MISMATCH;
import static com.pismo.txnroutine.exception.CustomMessages.AMOUNT_INVALID_FOR_OPERATION_TYPE;
import static com.pismo.txnroutine.exception.CustomMessages.AMOUNT_NOT_FOUND_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.OPERATION_TYPE_MISMATCH;
import static com.pismo.txnroutine.exception.CustomMessages.TXN_ID_NOT_PRESENT_OR_INVALID;
import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.model.Operation;
import com.pismo.txnroutine.utils.test.ServletTestUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.ServletConfig;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static org.junit.Assert.fail;
import org.mockito.Mockito;
import org.junit.Test;

public class TransactionWebServiceTest extends Mockito
{

    @Test
    public void testGetNotAllowed() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        TransactionsWebService txnWebService = new TransactionsWebService();

        txnWebService.init(mock(ServletConfig.class));
        txnWebService.doGet(test.request, test.response);

        verify(test.response).setStatus(SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void testNullPostParameters() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        String jsonString = "{}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonString.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        TransactionsWebService txnWebService = new TransactionsWebService();
        txnWebService.doPost(test.request, test.response);

        verify(test.response).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testNullAccountParameter() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        String jsonString = "\"operation_type_id\":4,"
                + "\"amount\":123.45}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonString.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        TransactionsWebService txnWebService = new TransactionsWebService();
        txnWebService.doPost(test.request, test.response);

        verify(test.response).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testInvalidOperationParameters() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        String jsonString = "{\"account_id\":1,\"operation_type_id\":10,"
                + "\"amount\":123.45}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonString.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        TransactionsWebService txnWebService = new TransactionsWebService();
        txnWebService.doPost(test.request, test.response);

        verify(test.response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    public void testInvalidAmountParameters() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        String jsonString = "{\"account_id\":1,\"operation_type_id\":10,"
                + "\"amount\":\"123.45\"}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonString.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        TransactionsWebService txnWebService = new TransactionsWebService();
        txnWebService.doPost(test.request, test.response);

        verify(test.response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    public void testNormalPurchasePostResource() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        TransactionsWebService txnWebService = new TransactionsWebService();

        testTransactionRequest(test, txnWebService,
                Operation.NORMAL_PURCHASE, 92.8);
    }

    @Test
    public void testInsPurchasePostResource() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        TransactionsWebService txnWebService = new TransactionsWebService();

        testTransactionRequest(test, txnWebService,
                Operation.INSTALLMENT_PURCHASE, 12.8);
    }

    @Test
    public void testWithdrawlPostResource() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        TransactionsWebService txnWebService = new TransactionsWebService();

        testTransactionRequest(test, txnWebService,
                Operation.WITHDRAWAL, 5.92);
    }

    @Test
    public void testCreditPostResource() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        TransactionsWebService txnWebService = new TransactionsWebService();

        testTransactionRequest(test, txnWebService,
                Operation.NORMAL_PURCHASE, -92.9);
    }

    private void testTransactionRequest(ServletTestUtils test,
            TransactionsWebService txnWebService, Operation op, Double amount)
            throws Exception
    {

        int accountId;

        try (Connection con = DriverManager.getConnection(txnWebService.sqlConString))
        {
            AccountDao accountDao = new AccountDaoSqlImpl(con);
            Account account = accountDao.create("TestDocument123456");

            accountId = account.getAccountId();
        }

        //This is done so that to safely delete the created test resources 
        //before failing the test case.
        boolean shouldFail = false;
        String failReason = null;

        String jsonString = "{\"account_id\":" + accountId + ","
                + "\"operation_type_id\":" + op.getValue() + ","
                + "\"amount\":" + amount + "}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonString.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        txnWebService.init(mock(ServletConfig.class));
        txnWebService.doPost(test.request, test.response);

        verify(test.response).setContentType("application/json");
        verify(test.response).setStatus(SC_CREATED);

        JsonNode json = new ObjectMapper().readTree(test.byOS.toString());

        String transactionId = json.get("transaction_id").asText();
        if (transactionId == null || transactionId.isEmpty())
        {
            shouldFail = true;
            failReason = TXN_ID_NOT_PRESENT_OR_INVALID;
        }

        int accountIdInDb = json.get("account_id").asInt();
        if (accountId != accountIdInDb)
        {
            shouldFail = true;
            failReason = ACCOUNT_ID_MISMATCH;
        }

        int opInDb = json.get("operation_type").asInt();
        if (opInDb != op.getValue())
        {
            shouldFail = true;
            failReason = String.format(OPERATION_TYPE_MISMATCH, op.getValue(), opInDb);
        }

        Double dbAmount = json.get("amount").asDouble();
        if (dbAmount == 0.0)
        {
            shouldFail = true;
            failReason = AMOUNT_NOT_FOUND_OR_INVALID;
        }

        switch (op)
        {
            case NORMAL_PURCHASE:
                if (dbAmount > 0)
                {
                    shouldFail = true;
                    failReason = AMOUNT_INVALID_FOR_OPERATION_TYPE;
                }
                break;

            case INSTALLMENT_PURCHASE:
                if (dbAmount > 0)
                {
                    shouldFail = true;
                    failReason = AMOUNT_INVALID_FOR_OPERATION_TYPE;
                }
                break;

            case WITHDRAWAL:
                if (dbAmount > 0)
                {
                    shouldFail = true;
                    failReason = AMOUNT_INVALID_FOR_OPERATION_TYPE;
                }
                break;

            case CREDIT_VOUCHER:
                if (dbAmount < 0)
                {
                    shouldFail = true;
                    failReason = AMOUNT_INVALID_FOR_OPERATION_TYPE;
                }
                break;
        }

        //Delete created account
        try (Connection con = DriverManager.getConnection(
                txnWebService.sqlConString))
        {
            TransactionDao transactionDao = new TransactionDaoSqlImpl(con);
            transactionDao.delete(new BigInteger(transactionId));
        }

        //Delete created transaction
        try (Connection con = DriverManager.getConnection(
                txnWebService.sqlConString))
        {
            AccountDao accountDao = new AccountDaoSqlImpl(con);
            accountDao.delete(accountId);
        }

        if (shouldFail)
        {
            fail(failReason);
        }
    }
}
