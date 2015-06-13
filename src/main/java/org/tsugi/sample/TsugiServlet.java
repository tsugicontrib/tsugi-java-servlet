package org.tsugi.sample;

import java.io.*;

import java.util.Properties;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.tsugi.*;
import org.tsugi.util.TsugiUtils;

public class TsugiServlet extends HttpServlet {

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
        doGet(req, res);
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Integer count = (Integer) session.getAttribute("count");
        if ( count == null ) count = 0;

        Launch launch = tsugi.getLaunch(req, res);
        if ( launch.isComplete() ) return;
        Output o = launch.getOutput();

        boolean isPost = "POST".equals(req.getMethod());
        if ( isPost && req.getParameter("count") != null ) {
            try {
                count = new Integer( (String) req.getParameter("count"));
                session.setAttribute("count", count);
                o.flashSuccess("POST set Counter="+count);
            } catch(Exception ex) {
                o.flashError(ex.getMessage());
            }
            o.postRedirect(null);
            return;
        }


        PrintWriter out = res.getWriter();

        Properties versions = o.header(out);
        o.bodyStart(out);
        session.setAttribute("count", count);
        o.flashMessages(out);

        out.print("<p><form method=\"post\" action=\"");
        out.print(launch.getOutput().getPostUrl(null));
        out.println("\">");
        out.println(launch.getOutput().getHidden());
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

        if ( ! launch.isValid() ) {
            out.println("Launch is not valid");
            out.println(launch.getErrorMessage());
            out.println("Base String:");
            out.println(launch.getBaseString());
            out.close();
            return;
        }

        out.println("Content Title: "+launch.getContext().getTitle());
        out.println("Context Settings: "+launch.getContext().getSettings().getSettingsJson());
        out.println("User Email: "+launch.getUser().getEmail());
        out.println("Link Title: "+launch.getLink().getTitle());
        out.println("Link Settings: "+launch.getLink().getSettings().getSettingsJson());
        out.println("Sourcedid: "+launch.getResult().getSourceDID());
        out.println("Service URL: "+launch.getService().getURL());
        out.println("");
        out.println("JavaScript library versions:");
        out.println(TsugiUtils.dumpProperties(versions));

        launch.getContext().getSettings().setSetting("count", count+"");

        out.print("<a href=\"");
        out.print(launch.getOutput().getGetUrl(null));
        out.print("\">Click here to see if we stay logged in with a GET</a>");
        out.println("</pre>");
        out.close();
    }
}
