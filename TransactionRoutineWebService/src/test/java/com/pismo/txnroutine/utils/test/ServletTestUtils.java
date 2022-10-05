package com.pismo.txnroutine.utils.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletTestUtils
{

    public final HttpServletRequest request;
    public final HttpServletResponse response;

    public final ByteArrayOutputStream byOS;

    public final ServletOutputStream srvltOS;

    public ServletTestUtils() throws IOException
    {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        byOS = new ByteArrayOutputStream();

        srvltOS = new ServletOutputStream()
        {
            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener)
            {
            }

            @Override
            public void write(int b) throws IOException
            {
            }

            @Override
            public void println(String str) throws IOException
            {
                byOS.write(str.getBytes());
            }
        };

        when(this.response.getOutputStream()).thenReturn(this.srvltOS);
    }
}
