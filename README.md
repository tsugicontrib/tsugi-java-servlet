
A simple servlet that that shows basic use of the Tsugi library.

Pre-Setup
---------

Here are the pre-steps:

* Install [PHP Tsugi](https://github.com/csev/tsugi) and get it setup and working.
Your PHP installation must be up and running as the Tsugi Sevlet just uses the PHP
Tsugi database.

* Checkout and compile the [Java Tsugi Library](https://github.com/csev/tsugi-java) so
that it is in your maven repository

Build / Run
-----------

If you are running on Windows, you need to change the details in the file

    ./src/main/resources/tsugi.properties

Change a line to be something like:

    tsugi.datasource.url=jdbc:mysql://localhost:3306/tsugi

Sorry about the Mac/MAMP-friendly defaults.

    mvn clean compile install jetty:run

Then navigate to 

    http://localhost:8080/tsugi-servlet/hello

It will say something like this:

    Welcome to hello world!
    Tsugi=org.tsugi.impl.jdbc.Tsugi_JDBC@64ef4b26
    launch=org.tsugi.base.BaseLaunch@103bdfa2
    Launch is not valid
    This tool must be launched using LTI
    Base String:
    null

It does not talk to the database if it rejects the request. 

Launching with LTI
------------------

We will launch the Java Servlet with the PHP Tsugi deveveloper's console.
Navigate to:

    http://localhost:8080/tsugi

Go into Developer mode - if the secret launch codes are not showing press 
"Toggle Data".  Change the Launch URL to be:

    http://localhost:8080/tsugi-servlet/hello

And press "Launch" at the top.  If all goes well, your Java Tsugi application
will be showing in the window with some kind of output like:

    Welcome to hello world!
    Tsugi=org.tsugi.impl.jdbc.Tsugi_JDBC@64ef4b26
    launch=org.tsugi.base.BaseLaunch@3cead1f9
    Content Title: Introduction to Programming
    User Email: inst@ischool.edu
    Link Title: Activity: attend
    Sourcedid: e10e575674e68bbcd873e2962f5a138b

If your database connection is mis-configured or not working in the Tsugi
servlet you will be splashed with plenty of traceback in the iframe:

    HTTP ERROR: 500
    Database server is down or tsugi database is missing
    RequestURI=/tsugi-servlet/hello
    Caused by:
    java.lang.RuntimeException: Database server is down or tsugi database is missing
    at org.tsugi.impl.jdbc.Tsugi_JDBC.getConnection(Tsugi_JDBC.java:82)
    at org.tsugi.impl.jdbc.Tsugi_JDBC.getLaunch(Tsugi_JDBC.java:114)
    at org.tsugi.base.BaseTsugi.getLaunch(BaseTsugi.java:51)
    at org.tsugi.sample.TsugiServlet.doGet(TsugiServlet.java:50)
    ...

Fix the tsugi.properties and try again.

