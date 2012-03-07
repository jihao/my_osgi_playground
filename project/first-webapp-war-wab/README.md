# Demonstrate of a classic war running as wab (web application bundle) 

# How to transfer a war to wab?
at first this is just a normal java web application, you can start by > mvn tomcat7:run

* modify pom.xml, add OSGi dependencies, add OSGi bundle related parent pom
* modify pom.xml, customize maven-bundle-plugin part, I followed this guide [http://www.javabeat.net/articles/378-writing-an-osgi-web-application-1.html](http://www.javabeat.net/articles/378-writing-an-osgi-web-application-1.html) in someway. 
* 

Not working: http://localhst:8080/first-webapp-war-wab 



