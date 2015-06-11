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
        PrintWriter out = res.getWriter();

        Launch launch = tsugi.getLaunch(req, res);
        if ( launch.isComplete() ) return;

        Output o = launch.getOutput();

        o.header(out);
        o.bodyStart(out);
        HttpSession session = req.getSession();
        Integer count = (Integer) session.getAttribute("count");
        if ( count == null ) count = 1;
        count++;
        session.setAttribute("count", count);
        o.flashMessages(out);
        if ( count % 2 == 0 ) {
            o.flashError("Counter="+count);
        } else {
            o.flashSuccess("Counter="+count);
        }

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
        out.println("User Email: "+launch.getUser().getEmail());
        out.println("Link Title: "+launch.getLink().getTitle());
        out.println("Sourcedid: "+launch.getResult().getSourceDID());
        out.println("Service URL: "+launch.getService().getURL());


        out.println("<a href=\"/tsugi-servlet/hello\">Click here to see if we stay logged in with a GET</a>");
        out.println("</pre>");
        out.close();
    }
}
