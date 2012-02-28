# Demonstrate of using felix-config-admin

You can modify the configuration parameter through CLI.

Similar [EchoServer](../EchoServer) listens on port 3334, use putty to setup a raw connection to 127.0.0.1:3334, then update some key:value

# Using felix-web-console
 
*Didn't succeed to modify config on web console*

add following dependency, you can access felix web console at: [http://localhost:8080/system/console](http://localhost:8080/system/console)

        <dependency>
			<groupId>org.apache.felix</groupId>
			<artifactId>org.apache.felix.webconsole</artifactId>
			<version>3.1.8</version>
			<type>bundle</type>
		</dependency>
		<dependency>
		    <groupId>org.apache.felix</groupId>
		    <artifactId>org.apache.felix.http.bundle</artifactId>
		    <version>2.2.0</version>
		</dependency>


References
------------------------------
[felix-config-admin](http://felix.apache.org/site/apache-felix-config-admin.html)

[felix-web-console](http://felix.apache.org/site/apache-felix-web-console.html)