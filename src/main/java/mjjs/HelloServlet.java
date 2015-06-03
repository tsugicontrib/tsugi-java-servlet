package mjjs;

import java.io.*;

import java.util.Properties;
import java.util.Enumeration;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class HelloServlet extends HttpServlet {
  public void doGet (HttpServletRequest req,
                     HttpServletResponse res)
    throws ServletException, IOException
  {
    PrintWriter out = res.getWriter();
    out.println("<pre>");
    out.println("Welcome to hello world!");
    out.println("");
    out.println("Reading /application.properties ...");

    Properties prop = new Properties();
    InputStream in = getClass().getResourceAsStream("/application.properties");
System.out.println("in="+in);
    if ( in == null ) {
        out.println("Missing application.properties in the war.");
    } else {
        prop.load(in);
        in.close();
    }

    String jdbc = prop.getProperty("mjjs.datasource.url");
    String username = prop.getProperty("mjjs.datasource.username");
    String password = prop.getProperty("mjjs.datasource.password");
    String className = prop.getProperty("mjjs.datasource.driverClassName");

    if ( jdbc == null | username == null || 
        password == null || className == null ) {
        out.println("Example properties:");
        out.println("mjjs.datasource.url=jdbc:mysql://localhost:8889/mjjs");
        out.println("mjjs.datasource.username=ltiuser");
        out.println("mjjs.datasource.password=ltipassword");
        out.println("mjjs.datasource.driverClassName=com.mysql.jdbc.Driver");
        out.println("</pre>");
        out.close();
        return;
    }

    try {
        Class.forName(className);
    } catch (ClassNotFoundException e) {
        out.println("Missing JDBC Driver: "+className);
        System.out.println("Missing JDBC Driver: "+className);
        e.printStackTrace();
        return;
    }

    Connection connection = null;
    try {
        connection = DriverManager
        .getConnection(jdbc, username, password);
 
    } catch (SQLException e) {
        out.println("Your database is missing or inaccessible");
        out.println("");
        out.println("CREATE DATABASE mjjs DEFAULT CHARACTER SET utf8;");
        out.println("GRANT ALL ON mjjs.* TO 'mjjsuser'@'localhost' IDENTIFIED BY 'mjjspassword';");
        out.println("GRANT ALL ON mjjs.* TO 'mjjsuser'@'127.0.0.1' IDENTIFIED BY 'mjjspassword';");
        e.printStackTrace();
        return;
    }
 
    if (connection == null) {
        out.println("Connection Failed!");
        return;
    }

    Statement stmt = null;
    String query = "SELECT name FROM mjjs LIMIT 5;";

    try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int count = 0;
        while (rs.next()) {
            String name = rs.getString("name");
            out.println("name="+name);
            count++;
        }
        out.println("Successfully read "+count+" rows from the database");
    } catch (SQLException e ) {
        out.println("Your database table is either missing or incorectly formatted");
        out.println("");
        out.println("CREATE TABLE mjjs (name TEXT) ENGINE = InnoDB DEFAULT CHARSET=utf8;");
        out.println("INSERT INTO mjjs (name) VALUES ('tsugi');");
        e.printStackTrace();
    }

    try {
        if (stmt != null) { stmt.close(); }
        connection.close();
    } catch (SQLException e ) {
        e.printStackTrace();
    }

    out.println("</pre>");
    out.close();
  }
}
