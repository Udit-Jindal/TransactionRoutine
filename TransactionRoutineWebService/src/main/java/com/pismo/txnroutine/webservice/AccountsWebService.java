package com.pismo.txnroutine.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.pismo.txnroutine.dao.AccountDao;
import com.pismo.txnroutine.dao.impl.sql.AccountDaoSqlImpl;
import static com.pismo.txnroutine.exception.CustomMessages.ACCOUNT_ID_NOT_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.DOCUMENT_ID_NOT_PRESENT_OR_INVALID;
import static com.pismo.txnroutine.exception.CustomMessages.FAILED_TO_CREATE_ACCOUNT;
import com.pismo.txnroutine.exception.EntityCreateFailException;
import com.pismo.txnroutine.exception.EntityNotFoundException;
import com.pismo.txnroutine.exception.RequiredParameterMissingException;
import com.pismo.txnroutine.model.Account;
import com.pismo.txnroutine.utils.WebUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(
        urlPatterns = "/accounts/*",
        loadOnStartup = 1
)
public class AccountsWebService extends HttpServlet
{

    public final String sqlConString;

    public AccountsWebService() throws ClassNotFoundException
    {
        String mySqlHostName = System.getenv("MYSQL_HOSTNAME");
        String mySqlPort = System.getenv("MYSQL_PORT");
        String mySqlDbName = System.getenv("MYSQL_DB_NAME");
        String mySqlusername = System.getenv("MYSQL_USER");
        String mySqlpassword = System.getenv("MYSQL_PASS");

        Class.forName("com.mysql.cj.jdbc.Driver");

        sqlConString = "jdbc:mysql://" + mySqlHostName + ":" + mySqlPort
                + "/" + mySqlDbName + "?user=" + mySqlusername
                + "&password=" + mySqlpassword
                + "&useSSL=false&allowPublicKeyRetrieval=true";
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        try (ServletOutputStream out = response.getOutputStream())
        {

            try
            {
                response.setContentType("application/json");

                Integer inputAccountId = null;

                //<editor-fold defaultstate="collapsed" desc="Get AccountId from URI">
                String[] uriParts = request.getRequestURI().split("/");

                for (int i = 0; i < uriParts.length; i++)
                {
                    if (!uriParts[i].equalsIgnoreCase("accounts"))
                    {
                        continue;
                    }

                    if (i + 1 == uriParts.length)
                    {
                        throw new RequiredParameterMissingException(
                                "Account Id is not present or invalid");
                    }

                    inputAccountId = Integer.parseInt(uriParts[i + 1]);
                    break;
                }
                //</editor-fold>

                if (inputAccountId == null)
                {
                    throw new RequiredParameterMissingException(
                            ACCOUNT_ID_NOT_PRESENT_OR_INVALID);
                }

                try (Connection con = DriverManager.getConnection(sqlConString))
                {
                    AccountDao accountDao = new AccountDaoSqlImpl(con);
                    Account account = accountDao.get(inputAccountId);

                    response.setStatus(SC_OK);

                    out.println(account.getJsonString());
                }
            }
            catch (RequiredParameterMissingException ex)
            {
                response.setStatus(SC_BAD_REQUEST);

                WebUtils.giveResponse(out, ex.getMessage());
            }
            catch (EntityNotFoundException ex)
            {
                response.setStatus(SC_NOT_FOUND);
            }
            catch (Exception ex)
            {
                response.setStatus(SC_INTERNAL_SERVER_ERROR);

                WebUtils.giveResponse(out, ex.getMessage());
            }
        }
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

                String documentNumber = json.get("document_number").asText();
                if (documentNumber == null)
                {
                    throw new RequiredParameterMissingException(
                            DOCUMENT_ID_NOT_PRESENT_OR_INVALID);
                }

                Connection con = DriverManager.getConnection(sqlConString);

                try
                {
                    con.setAutoCommit(false);

                    AccountDao accountDao = new AccountDaoSqlImpl(con);
                    Account account = accountDao.create(documentNumber);

                    con.commit();

                    response.setStatus(SC_CREATED);

                    out.println(account.getJsonString());
                }
                catch (SQLException ex)
                {
                    con.rollback();

                    throw new EntityCreateFailException(FAILED_TO_CREATE_ACCOUNT);
                }
                finally
                {
                    con.close();
                }
            }
            catch (RequiredParameterMissingException ex)
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
    public String getServletInfo()
    {
        return "Account web service running";
    }
}
