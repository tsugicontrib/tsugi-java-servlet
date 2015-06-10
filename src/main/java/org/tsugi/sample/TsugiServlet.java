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

import org.tsugi.Tsugi;
import org.tsugi.TsugiFactory;
import org.tsugi.Launch;

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
        out.println("<pre>");
        out.println("Welcome to hello world!");

        HttpSession session = req.getSession();
        Integer count = (Integer) session.getAttribute("count");
        if ( count == null ) count = 1;
        count++;
        out.println("Counter="+count);
        session.setAttribute("count", count);
        out.println("");
        out.println("Tsugi="+tsugi);
        Launch launch = tsugi.getLaunch(req, res);
        out.println("launch="+launch);
        if ( launch.isComplete() ) return;
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
