
A simple servlet that that shows basic use of the Tsugi library.

[![Apereo Incubating badge](https://img.shields.io/badge/apereo-incubating-blue.svg?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAABmJLR0QA%2FwD%2FAP%2BgvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH4QUTEi0ybN9p9wAAAiVJREFUKM9lkstLlGEUxn%2Fv%2B31joou0GTFKyswkKrrYdaEQ4cZAy4VQUS2iqH%2BrdUSNYmK0EM3IkjaChnmZKR0dHS0vpN%2FMe97TIqfMDpzN4XkeDg8%2Fw45R1XNAu%2Fe%2BGTgAqLX2KzAQRVGytLR0jN2jqo9FZFRVvfded66KehH5oKr3dpueiMiK915FRBeXcjo9k9K5zLz%2B3Nz8EyAqX51zdwGMqp738NSonlxf36Cn7zX9b4eYX8gSBAE1Bw9wpLaW%2BL5KWluukYjH31tr71vv%2FU0LJ5xzdL3q5dmLJK7gON5wjEQizsTkFMmeXkbHxtHfD14WkbYQaFZVMzk1zfDHERrPnqGz4wZ1tYfJ5%2FPMLOYYW16ltrqKRDyOMcYATXa7PRayixSc4%2FKFRhrqjxKGIWVlZVQkqpg1pYyvR%2BTFF2s5FFprVVXBAAqq%2F7a9uPKd1NomeTX4HXfrvZ8D2F9dTSwWMjwywueJLxQKBdLfZunue0Mqt8qPyMHf0HRorR0ArtbX1Zkrly7yPNnN1EyafZUVZLJZxjNLlHc%2BIlOxly0RyktC770fDIGX3vuOMAxOt19vJQxD%2BgeHmE6liMVKuNPawlZ9DWu2hG8bW1Tuib0LgqCrCMBDEckWAVjKLetMOq2ZhQV1zulGVFAnohv5wrSq3tpNzwMR%2BSQi%2FyEnIl5Ehpxzt4t6s9McRdGpIChpM8Y3ATXbkKdEZDAIgqQxZrKo%2FQUk5F9Xr20TrQAAAABJRU5ErkJggg%3D%3D)](https://www.apereo.org/content/projects-currently-incubation)

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

    HTTP ERROR: 500
    This tool must be launched using LTI
    RequestURI=/tsugi-servlet/hello

It does not talk to the database if it rejects the request. 

Launching with LTI
------------------

To launch this app, go to:

    https://online.dr-chuck.com/sakai-api-test/lms.php

Launch to:

    http://localhost:8080/tsugi-servlet/hello
    12345 / secret

If all goes well for the launch, your Java Tsugi application
will be showing in the window with some kind of output like:

    Welcome to hello world!
    Click here to see if we stay logged in with a GET
    Content Title: Introduction to Programming
    Context Settings: {"zap":"1234","count":"1"}

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

Other Source Code
-----------------

Tsugi Second-level API library

https://github.com/tsugiproject/tsugi-java

Tsugi low-level API library (formerly sakai-basicltiutil)

https://github.com/tsugiproject/tsugi-util

