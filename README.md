# cics-java-liberty-appstate

RESTful application state browser

This project provides a simple RESTful application state browser for Liberty using the WebSphere JMX Mbeans support.


#Pre-reqs

    CICS TS V5.2 or later
    Eclipse with WebSphere Developer Tools and CICS Explorer SDK installed

#Configuration

The sample  code can be added to a dynamic web project and deployed into a CICS Liberty JVM server as a web archive (WAR).


To deploy, the sample code needs to be added to a dynamic Web project along with the sample web.xml
Then add the following Liberty features: monitor-1.0 and restconnector-1.0. Once deployed RESTful GET requests can be sent with a web browser 
using the following syntax assuming the project was deployed with the com.ibm.cicsdev.jmxquery web context root:
* http://host:port/com.ibm.cicsdev.jmxquery/app/state - To list all available web applications
* http://host:port/com.ibm.cicsdev.jmxquery/app/state/<app>  - To list the state of an individual web application

Requests will return a JSON object containing the application and state as reported by the WebSphere:service=com.ibm.websphere.application.ApplicationMBean

For example:
{"appName":"hello","State":"STOPPED"}



#Reference material
* Creating RESTful applications with CICS Liberty (https://developer.ibm.com/cics/2016/03/11/java-for-cics-developing-restful-web-services-in-liberty-with-jax-rs/ "Creating RESTful applications with CICS Liberty").
* Accessing Liberty’s JMX REST APIs (https://developer.ibm.com/wasdev/docs/accessing-libertys-jmx-rest-apis "Accessing Liberty’s JMX REST APIs").


