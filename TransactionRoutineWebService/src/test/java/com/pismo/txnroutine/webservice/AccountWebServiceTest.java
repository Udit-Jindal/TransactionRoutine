package com.pismo.txnroutine.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.txnroutine.dao.AccountDao;
import com.pismo.txnroutine.dao.impl.sql.AccountDaoSqlImpl;
import static com.pismo.txnroutine.exception.CustomMessages.DOCUMENT_ID_NOT_PRESENT_OR_INVALID;
import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.utils.test.ServletTestUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.ServletConfig;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.fail;
import org.mockito.Mockito;
import org.junit.Test;

public class AccountWebServiceTest extends Mockito
{

    @Test
    public void testGetURIParsing() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        when(test.response.getOutputStream()).thenReturn(test.srvltOS);

        AccountsWebService accWebService = new AccountsWebService();
        accWebService.init(mock(ServletConfig.class));

        accWebService.doGet(test.request, test.response);

        verify(test.request).getRequestURI();
    }

    @Test
    public void testGetEntityNotFound() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        when(test.request.getRequestURI()).thenReturn("/accounts/-1");

        AccountsWebService accWebService = new AccountsWebService();

        accWebService.init(mock(ServletConfig.class));
        accWebService.doGet(test.request, test.response);

        verify(test.response).setStatus(SC_NOT_FOUND);
    }

    @Test
    public void testGetEntityFound() throws Exception
    {
        AccountsWebService accWebService = new AccountsWebService();

        try (Connection con = DriverManager.getConnection(accWebService.sqlConString))
        {
            AccountDao accountDao = new AccountDaoSqlImpl(con);
            Account account = accountDao.create("TestDocument123456");

            int accountId = account.getAccountId();

            ServletTestUtils test = new ServletTestUtils();

            when(test.request.getRequestURI()).thenReturn("/accounts/" + accountId);

            accWebService.init(mock(ServletConfig.class));
            accWebService.doGet(test.request, test.response);

            verify(test.response).setStatus(SC_OK);
        }
    }

    @Test
    public void testPostResource() throws Exception
    {
        ServletTestUtils test = new ServletTestUtils();

        String jsonCreate = "{\"document_number\":\"12345678900\"}";

        BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        jsonCreate.getBytes(Charset.forName("UTF-8")))));

        when(test.request.getReader()).thenReturn(bfReader);

        AccountsWebService accWebService = new AccountsWebService();

        accWebService.init(mock(ServletConfig.class));
        accWebService.doPost(test.request, test.response);

        verify(test.response).setContentType("application/json");
        verify(test.response).setStatus(SC_CREATED);

        JsonNode json = new ObjectMapper().readTree(test.byOS.toString());

        Integer accountId = json.get("account_id").asInt();

        String documentNumber = json.get("document_number")
                .asText().replaceAll("\"", "");
        if ((documentNumber == null) || documentNumber.isEmpty())
        {
            fail(DOCUMENT_ID_NOT_PRESENT_OR_INVALID);
        }

        //Delete created resource
        try (Connection con = DriverManager.getConnection(
                accWebService.sqlConString))
        {
            AccountDao accountDao = new AccountDaoSqlImpl(con);
            accountDao.delete(accountId);
        }
    }
}
