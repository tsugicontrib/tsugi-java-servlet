
A simple servlet that uses Maven, JDBC, and Jetty to say 'Hello world'

Build / Run
-----------

    mvn clean compile install jetty:run

Then navigate to 

    http://localhost:8080/mjjs/hello

Errors
------

If you end up with this error, 

    No plugin found for prefix 'jetty' in the current project and in the
    plugin groups [org.apache.maven.plugins, org.codehaus.mojo] available
    from the repositories

add this to your

    ~/.m2/settings.xml

    <settings>
        <pluginGroups>
            <pluginGroup>org.mortbay.jetty</pluginGroup>
        </pluginGroups>
    </settings>

