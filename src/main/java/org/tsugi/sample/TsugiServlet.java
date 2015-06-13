package org.tsugi.sample;

import java.io.*;

import java.util.Properties;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.tsugi.*;
import org.tsugi.util.TsugiUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TsugiServlet extends HttpServlet {

    private Log log = LogFactory.getLog(TsugiServlet.class);

    Tsugi tsugi = null;

    // Allow overriding from something like Spring
    public void setTsugi(Tsugi tsugi) 
    {
        this.tsugi = tsugi;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if ( tsugi == null ) tsugi = TsugiFactory.getTsugi();
        System.out.println("Tsugi init="+tsugi);
    }


    public void doPost (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        Launch launch = tsugi.getLaunch(req, res);
        if ( launch.isComplete() ) {
            launch.getOutput().flashSuccess("LTI Launch validated and redirected");
            log.info("LTI Launch validated and redirected...");
            return;
        }
        if ( ! launch.isValid() ) {
            throw new RuntimeException(launch.getErrorMessage());
        }

        HttpSession session = req.getSession();
        Output o = launch.getOutput();

        if ( req.getParameter("count") != null ) {
            try {
                Integer count = new Integer( (String) req.getParameter("count"));
                session.setAttribute("count", count);
                o.flashSuccess("POST set Counter="+count);
            } catch(Exception ex) {
                o.flashError(ex.getMessage());
            }
            o.postRedirect(null);
            return;
        }
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        PrintWriter out = res.getWriter();

        Launch launch = tsugi.getLaunch(req, res);
        if ( launch.isComplete() ) return;
        if ( ! launch.isValid() ) {
            out.println("<pre>");
            out.println("Launch is not valid");
            out.println(launch.getErrorMessage());
            out.println("Base String:");
            out.println(launch.getBaseString());
            out.println("</pre>");
            out.close();
            return;
        }

        // Start to handle our GET request
        Output o = launch.getOutput();

        Integer count = (Integer) session.getAttribute("count");
        if ( count == null ) count = 0;

        Properties versions = o.header(out);
        o.bodyStart(out);
        o.flashMessages(out);

        out.print("<p><form method=\"post\" action=\"");
        out.print(o.getPostUrl(null));
        out.println("\">");
        out.println(o.getHidden());
        out.print("Count: <input type=\"text\" name=\"count\" value=\"");
        out.print(count);
        out.println("\">");
        out.println("<input type=\"submit\">");
        out.println("</form></p>");

        // Increment our counter
        count++;
        session.setAttribute("count", count);

        out.println("<pre>");
        out.println("Welcome to hello world!");

        launch.getContext().getSettings().setSetting("count", count+"");

        out.print("<a href=\"");
        out.print(o.getGetUrl(null));
        out.println("\">Click here to see if we stay logged in with a GET</a>");

        out.println("Content Title: "+launch.getContext().getTitle());
        out.println("Context Settings: "+launch.getContext().getSettings().getSettingsJson());
        out.println("User Email: "+launch.getUser().getEmail());
        out.println("Link Title: "+launch.getLink().getTitle());
        out.println("Link Settings: "+launch.getLink().getSettings().getSettingsJson());
        out.println("Sourcedid: "+launch.getResult().getSourceDID());
        out.println("Service URL: "+launch.getService().getURL());
        out.println("A Spinner: <img src=\"");
        out.println(o.getSpinnerUrl());
        out.println("\">");
        out.println("");
        out.println("JavaScript library versions:");
        out.println(TsugiUtils.dumpProperties(versions));

        try {
            Connection c = launch.getConnection();
            out.println("Connection: "+c);
            DatabaseMetaData meta = c.getMetaData();
            String productName = meta.getDatabaseProductName();
            String productVersion = meta.getDatabaseProductVersion();
            String URL = meta.getURL();
            out.println("Connection product=" + productName+" version=" + productVersion);
            out.println("Connection URL=" + URL);
        } catch (Exception ex) {
            log.error("Unable to get connection metadata",ex);
            out.println("Unable to get connection metadata:"+ex.getMessage());
        }

        // Cheat and look at the internal data Tsugi maintains - this depends on
        // the JDBC implementation
        Properties sess_row = (Properties) session.getAttribute("lti_row");
        if ( sess_row != null ) {
            out.println("");
            out.println("Data from session (org.tsugi.impl.jdbc.Tsugi_JDBC)");
            String x = TsugiUtils.dumpProperties(sess_row);
            out.println(x);
        }
        
        out.println("</pre>");
        out.close();
    }
}
