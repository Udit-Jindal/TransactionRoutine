package com.pismo.txnroutine.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.pismo.txnroutine.dao.AccountDao;
import com.pismo.txnroutine.dao.TransactionDao;
import com.pismo.txnroutine.dao.impl.sql.AccountDaoSqlImpl;
import com.pismo.txnroutine.dao.impl.sql.TransactionDaoSqlImpl;
import com.pismo.txnroutine.exception.CustomMessages;
import com.pismo.txnroutine.exception.EntityCreateFailException;
import com.pismo.txnroutine.exception.InvalidOperationException;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.model.Operation;
import com.pismo.txnroutine.model.Transaction;
import com.pismo.txnroutine.utils.WebUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@WebServlet(
        urlPatterns = "/transactions/*",
        loadOnStartup = 1
)
public class TransactionsWebService extends HttpServlet
{

    public final String sqlConString;

    public TransactionsWebService() throws ClassNotFoundException
    {
        String mySqlHostName = "localhost";
        String mySqlPort = "3306";
        String mySqlDbName = "txn_routine";
        String mySqlusername = "root";
        String mySqlpassword = "admin@12345";
        String mysqlUseSSL = "false";
//        String mySqlHostName = System.getenv("MYSQL_HOSTNAME");
//        String mySqlPort = System.getenv("MYSQL_PORT");
//        String mySqlDbName = System.getenv("MYSQL_DB_NAME");
//        String mySqlusername = System.getenv("MYSQL_USER");
//        String mySqlpassword = System.getenv("MYSQL_PASS");

        Class.forName("com.mysql.cj.jdbc.Driver");

        sqlConString = "jdbc:mysql://" + mySqlHostName + ":" + mySqlPort
                + "/" + mySqlDbName + "?user=" + mySqlusername
                + "&password=" + mySqlpassword + "&useSSL="
                + mysqlUseSSL + "&allowPublicKeyRetrieval=true";
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        try (ServletOutputStream out = response.getOutputStream())
        {
            try
            {
                response.setContentType("application/json");

                JsonNode json = WebUtils.getJsonBody(request);

                int accountId = json.get("account_id").asInt();
                Operation operation = Operation.parse(
                        json.get("operation_type_id").asInt());
                double amount = json.get("amount").asDouble();

                if (accountId == 0)
                {
                    throw new RequiredParameterMissingException(
                            CustomMessages.ACCOUNT_ID_NOT_PRESENT_OR_INVALID);
                }

                if (amount == 0.0)
                {
                    throw new RequiredParameterMissingException(
                            CustomMessages.AMOUNT_NOT_FOUND_OR_INVALID);
                }

                Connection con = DriverManager.getConnection(sqlConString);

                try
                {
                    con.setAutoCommit(false);

                    AccountDao accountDao = new AccountDaoSqlImpl(con);
                    Account account = accountDao.get(accountId);

                    TransactionDao transactionDao = new TransactionDaoSqlImpl(con);
                    Transaction transaction = transactionDao.create(account,
                            operation, amount, Calendar.getInstance());

                    con.commit();

                    response.setStatus(SC_CREATED);

                    out.println(transaction.getJsonString());
                }
                catch (SQLException ex)
                {
                    con.rollback();

                    throw new EntityCreateFailException(
                            CustomMessages.FAILED_TO_CREATE_TXN);
                }
                finally
                {
                    con.close();
                }
            }
            catch (InvalidOperationException
                    | RequiredParameterMissingException ex)
            {
                response.setStatus(SC_BAD_REQUEST);

                WebUtils.giveResponse(out, ex.getMessage());
            }
            catch (EntityCreateFailException ex)
            {
                response.setStatus(SC_CONFLICT);

                WebUtils.giveResponse(out, ex.getMessage());
            }
            catch (Exception ex)
            {
                response.setStatus(SC_INTERNAL_SERVER_ERROR);

                WebUtils.giveResponse(out, ex.getMessage());
            }
        }
    }

    @Override
    public void destroy()
    {

    }

    @Override
    public String getServletInfo()
    {
        return "Transaction web service running";
    }
}
