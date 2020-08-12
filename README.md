# cics-java-liberty-appstate


This project provides a simple RESTful application state browser for Liberty using the WebSphere JMX Mbeans support.


## Pre-reqs

    CICS TS V5.2 or later
    Eclipse with WebSphere Developer Tools and CICS Explorer SDK installed

## Configuration

The sample  code can be added to a dynamic web project and deployed into a CICS Liberty JVM server as a web archive (WAR).


To configure the Liberty server add the following Liberty features: monitor-1.0, localConnector-1.0, jaxrs-1.1. Once deployed RESTful GET requests can be sent with a web browser 
using the following syntax assuming the project was deployed with the com.ibm.cicsdev.jmxquery web context root:
* http://host:port/com.ibm.cicsdev.jmxquery/state - To query all available web applications
* http://host:port/com.ibm.cicsdev.jmxquery/state/appName  - To query the state of an individual web application, where appName is the web application name 

Requests will return a JSON object containing the application and state as reported by the WebSphere:service=com.ibm.websphere.application.ApplicationMBean

For example:
```json
{"appName":"hello","State":"STOPPED"}
```



## Reference material
* [Accessing Liberty’s JMX REST APIs](https://developer.ibm.com/wasdev/docs/accessing-libertys-jmx-rest-apis)
* [Querying Liberty web applications using JMX](blog.md)


