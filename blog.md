Querying Liberty web applications using JMX
=============================================

Author: Phil Wakelin

### JMX based monitoring

The WebSphere Liberty monitor-1.0 feature provides a useful addition to
the JMX based monitoring functions available in a JVM. It provides for
the monitoring of any web application deployed into a Liberty server
giving information about status and performance. This feature has been
supported in CICS Liberty since CICS TS V5.2, and in conjunction with
the restConnector-1.0 feature allows remote RESTful access to any of the
MBeans available in a JVM.

If you want to build some automation to access any of the MBeans then
you can use the restConnector-1.0 support to provide remote RESTful
access. This gives access from the Java JConsole on a remote workstation
(see this
[WASdev](https://developer.ibm.com/wasdev/docs/article_howto_remotejmx/)
article) or alternatively you can also use the restConnector-1.0
directly from a RESTful client to access any of the JMX MBeans (see this
[WASdev](https://developer.ibm.com/wasdev/docs/mbeans-using-the-liberty-jmx-rest-connector)
article).

However, it is also really easy just to develop a custom web application
to expose the information required using the localConnector-1.0 feature.
Using this approach you can use the inbuilt JAX-RS support in Liberty to
create a RESTful web application to query application state, this can
then be used in an automation system via the HTTP curl tool provided
with the USS ported tools. This approach also has the advantage that you
can customize the security of this application using standard web
application security controls in your web.xml, rather than rely on the
SSL and administration roles required when using the restConnector-1.0
support.

## Building the application

To create a RESTful application we will need a class for each type of
query and also a configuration class. To keep the queries simple we will
create just two classes. One to handle a generic GET request to browse
all applications, and another to GET a specific application and its
status. This will allow us to encode the function within the URL path
and use a GET for both operations.

### GET a specific application

To start with we will create the **AppGet** class to get the status of a
specific application, this will have the `browseApps()`method to browse
the application status. To make this a RESTful client we will use the
JAXRS `@Path` annotation to mark the base URL path `/state/` that will
be mapped to this class. We then use` @GET` annotation to mark what type
of HTTP method will be used, and then parse the application name from
the URL using the `@PathParam` annotation.

This means our application can be queried using the following URL syntax
where **webapp** is the web context under which this application is
deployed, and **appName** is the name of the target web application to
be queried:

`http://hostname:port/webapp/state/appName`

The listing below shows our sample AppGet class, which can be downloaded
from the reference material provided with this article.

``` javax
@javax.ws.rs.Path("/state/{appName}")
public class AppGet {

    @javax.ws.rs.GET
    @Produces("application/json")
    public JSONObject browseApps(@javax.ws.rs.PathParam("appName") String appName) throws Exception {

        JSONObject returnData = new JSONObject();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName myAppMBean = new ObjectName(
            "WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name="+appName);
                if (mbs.isRegistered(myAppMBean)) {
                    String state = (String) mbs.getAttribute(myAppMBean, "State");    
                    returnData.put("appName", appName);
                    returnData.put("State", state);
                } else {
                    returnData.put("appName", appName);
                    returnData.put("Error", "NOT FOUND");           
                }       
        return returnData;
    }
}
```

This is explained below using the line numbers from the listing:

**9.** Here we create an instance of an `MBeanServer` using the
`ManagementFactory.getPlatformMBeanServer()` which registers each MBean
in the MBeanServer with its ObjectName

**10.** Next we need to name the actual MBean we want to target, and in
our case this is the WebSphere provided
`com.ibm.websphere.application.ApplicationMBean`. The full list of
WebSphere provides MBeans is provided
[here](http://www.ibm.com/support/knowledgecenter/SSD28V_8.5.5/com.ibm.websphere.wlp.core.doc/ae/rwlp_mbeans_list.html?lang=en),
but there are also a wide variety of other MBeans provided by the JVM
which can be browsed using JConsole. We also need to pass in the name of
our web application using the name attribute taken from the **appName**
path parameter on the URL.

**11.** Next we just need to verify the MBean is registered in our
MBeanSever, and get the State attribute which will either be STOPPED,
STARTING, STARTED, PARTIALY_STARTED, STOPPING, INSTALLED see
[ApplicationMBean.getState()](http://www.ibm.com/support/knowledgecenter/SSD28V_8.5.5/com.ibm.websphere.javadoc.liberty.doc/com.ibm.websphere.appserver.api.basics_1.2-javadoc/index.html?cp=SSD28V_8.5.5&lang=en)

Lastly in **14.** and **15.**, we just add this information to our
JSONObject map which will be returned to our RESTful client

### GET all applications

Next we will create the **AppGetAll** class to get the status of all
applications, this will also have the `browseApps `method to browse the
application status. To differentiate this class we will use the
`@Path `annotation but remove the `@PathParam` annotation so that this
will only be invoked if the URL omits the application name as follows:

`http://hostname:port/webapp/state`

The listing below shows our sample AppGetAll class, which can be
downloaded from the reference material provided with this article.

```javax
@javax.ws.rs.Path("/state/")
public class AppGetAll {

    @javax.ws.rs.GET
    @Produces("application/json")
    public JSONObject browseApps() throws Exception {

        JSONObject returnData = new JSONObject();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();       
        ObjectName myAppMBean = new ObjectName("WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name=*");
        Set<ObjectName> appMBeans = new HashSet<ObjectName>();
        appMBeans.addAll(mbs.queryNames(myAppMBean, null));
        for (ObjectName applicationMbean : appMBeans)
        {
            String name = applicationMbean.getKeyProperty("name");
            String state = (String) mbs.getAttribute(applicationMbean, "State");
            returnData.put(name, state);
        }       
        return returnData;
    }

}
```

The differences between the previous sample are explained by reference
to the sample line numbers:

**10.** The name attribute passed to the ApplicationMbean in the
ObjectName is set to` name=*` which will return a set of MBeans for all
registered applications.
**12.** The `MBeanServer.queryNames()` method is used to find all MBeans
matching this ObjectName.
**13**. The set of MBeans is iterated through and the `MBeanServer`
`getKeyProperty()` and `getAttribute()` methods are used to extract the
name and the state and add to the JSONObject Map in **17.**

## Deploying the application

To deploy the application you will also need to create a JAXRS
configuration class and set the `javax.ws.rs.Application` initialization
parameter in the web.xml. You can find further details on how to do this
in [this](https://developer.ibm.com/cics/2016/03/11/java-for-cics-developing-restful-web-services-in-liberty-with-jax-rs/)
article.

## Automating the query

The HTTP command line tool is provided for USS with the USS [ported
tools](http://www-03.ibm.com/systems/z/os/zos/features/unix/port_tools.html).
To query an application you can simply issue the following
`curl` command using the -s silent option to remove statistics and then
piping through `iconv` to convert to the EBCDIC encoding of your choice.

```
    curl -s http://host:port/com.ibm.cicsdev.jmxquery/app/state/hello| iconv -t 1047 -f iso8859-1
```
To automate this you can then use a simple ksh shell script which can
take as input the name of the application and the desired state and use
the UNIX grep command to filter for the desired state. The script needs
to ensure the correct return code is set and returned to the calling
process. An example is shown below in the following listing.

```
#!/bin/ksh
cmd="curl -s http://host:port/com.ibm.cicsdev.jmxquery/app/state/$1| iconv -t 1047 -f iso8859-1 | grep $2"
eval $cmd
rc=$?
 if [ $rc != 0 ]; then
    print "Error:("$rc") application "$1" state is not: "$2
    exit $rc
 fi

 if [ $rc = 0 ]; then
    print "OK:("$rc") application "$1" state is: "$2
    exit $rc
 fi
```

This will produce output as follows:

    > curltest hello STARTED
    {"appName":"hello","State":"STARTED"}
    OK:(0) application hello state is: STARTED

The last step is to automate this as part of a batch process, and
BPXBATCH can be used to do launch the script above, and will return 00
in the STEP1 if the script executes and returns OK.

```
 //BPXCURL JOB (999,TSOO), CLASS=A, MSGCLASS=A,          
 // REGION=100M,MSGLEVEL=(1,1)                                                            
 //* JMX MBean web app status query
 //STEP1 EXEC PGM=BPXBATCH                               
 //STDENV   DD *                                         
 //STDOUT   DD   SYSOUT=*                                
 //STDERR   DD   SYSOUT=*                                
 //STDPARM  DD *                                          
 SH /u/wakelin/bin/curltest hello STARTED
 /*                                                         
```

### References

-   RESTful web application state browser -- [sample
    code](https://github.com/cicsdev/cics-java-liberty-appstate)
-   Developing RESTful web services in Liberty with JAX-RS --
    [tutorial](https://developer.ibm.com/cics/2016/03/11/java-for-cics-developing-restful-web-services-in-liberty-with-jax-rs/)
-   USS [ported
    tools](http://www.ibm.com/systems/z/os/zos/features/unix/port_tools.html)
