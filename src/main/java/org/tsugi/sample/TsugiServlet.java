package org.tsugi.sample;

import java.io.*;

import java.util.Properties;

import java.net.URLEncoder;

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
            PrintWriter out = res.getWriter();
            out.println("<pre>");
            out.println("Launch is not valid but nowhere to redirect");
            out.println(launch.getErrorMessage());
            out.println("Base String:");
            out.println(launch.getBaseString());
            out.println("</pre>");
            out.close();
            return;
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
            launch.postRedirect(null);
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
            throw new RuntimeException(launch.getErrorMessage());
        }

        // Start to handle our GET request
        Output o = launch.getOutput();

        Integer count = (Integer) session.getAttribute("count");
        if ( count == null ) count = 0;

        Properties versions = o.header(out);
        o.bodyStart(out);
        o.flashMessages(out);

        out.print("<p><form method=\"post\" action=\"");
        out.print(launch.getPostUrl(null));
        out.println("\">");
        out.println(launch.getHidden());
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

	// Dump out some stuff from the Request Object
	out.println("");
	out.println("<a href=\"http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html\" target=\"_blank\">HttpServletRequest</a> data:");
	out.println("req.getRequestURL()="+req.getRequestURL());
	out.println("req.getMethod()="+req.getMethod());
	out.println("req.getServletPath()="+req.getServletPath());
	out.println("req.getPathInfo()="+req.getPathInfo());
	out.println("req.getQueryString()="+req.getQueryString());


	out.println("");
        launch.getContext().getSettings().setSetting("count", count+"");

        out.print("<a href=\"");
        out.print(launch.getGetUrl(null)+"/zap");
        out.println("\">Click here to see if we stay logged in with a GET</a>");

	out.println("");
	out.println("Using the <a href=\"http://csev.github.io/tsugi-java/apidocs/index.html\" target=\"_blank\">Tsugi API</a>:");
        out.println("Content Title: "+launch.getContext().getTitle());
        out.println("Context Settings: "+launch.getContext().getSettings().getSettingsJson());
        out.println("User Email: "+launch.getUser().getEmail());
        out.println("isInstructor()="+launch.getUser().isInstructor());
        out.println("isTenantAdmin()="+launch.getUser().isTenantAdmin());
        out.println("Link Title: "+launch.getLink().getTitle());
        out.println("Link Settings: "+launch.getLink().getSettings().getSettingsJson());
        out.println("Sourcedid: "+launch.getResult().getSourceDID());
        // out.println("Service URL: "+launch.getService().getURL());
        out.println("A Spinner: <img src=\"");
        out.println(launch.getSpinnerUrl());
        out.println("\">");
        out.println("");
        out.println("JavaScript library versions:");
        out.println(TsugiUtils.dumpProperties(versions));

	out.println("");
	out.println("Using the provided JDBC connection:");
        Connection c = null;
        try {
            c = launch.getConnection();
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

	// Do a simple query just to see how it is done
	if ( c !=  null ) {
		Statement stmt = null;
		String query = "SELECT plugin_id, plugin_path FROM lms_plugins;";

		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int num = 0;
			while (rs.next()) {
				String plugin_path = rs.getString("plugin_path");
				out.println("plugin_path="+plugin_path);
				num++;
			}
			out.println("Successfully read "+num+" rows from the database");
		} catch (SQLException e ) {
			out.println("Problems reading database");
			out.println("INSERT INTO mjjs (name) VALUES ('tsugi');");
			e.printStackTrace();
		}
	}

        // Cheat and look at the internal data Tsugi maintains - this depends on
        // the JDBC implementation
        Properties sess_row = (Properties) session.getAttribute("lti_row");
        if ( sess_row != null ) {
            out.println("");
            out.println("Tsugi-managed internal session data (Warning: org.tsugi.impl.jdbc.Tsugi_JDBC only)");
            String x = TsugiUtils.dumpProperties(sess_row);
            out.println(x);
        }
        
        out.println("</pre>");

        // Do the Footer
        o.footerStart(out);
        out.println("<!-- App footer stuff goes here -->");
        o.footerEnd(out);

        out.close();
    }
}
