package com.pismo.txnroutine.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

public class WebUtils
{

    public static JsonNode getJsonBody(HttpServletRequest request) throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();

        String line;
        while ((line = reader.readLine()) != null)
        {
            buffer.append(line);
        }

        String reqDataString = buffer.toString();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(reqDataString);
    }

    public static void giveResponse(ServletOutputStream out, String message)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("\"description\":");
        sb.append("\"");
        sb.append(message);
        sb.append("\"");

        sb.append("}");

        try
        {
            out.println(sb.toString());
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex);
        }
    }
}
