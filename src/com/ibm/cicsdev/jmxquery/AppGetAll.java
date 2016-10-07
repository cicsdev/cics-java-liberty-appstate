/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2016 All Rights Reserved                       */       
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */     

package com.ibm.cicsdev.jmxquery;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Produces;
import com.ibm.json.java.JSONObject;

@javax.ws.rs.Path("/state/")
public class AppGetAll {

	/**
	 * The GET HTTP Method is used to get (or browse) all applications from a Liberty server
	 * This method will browse the ApplicationMBeans to find all applications and report their state
	 */
	@javax.ws.rs.GET
	@Produces("application/json")
	public JSONObject browseApps() throws Exception {

		// create a JSONObject to return
		JSONObject returnData = new JSONObject();
		
		// Create an MBean server
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();		

		// MBean for all ApplicationMBeans using wildcard for name
		ObjectName myAppMBean = new ObjectName("WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name=*");
				
		// Add all the Mbeans to the Set
		Set<ObjectName> appMBeans = new HashSet<ObjectName>();
		appMBeans.addAll(mbs.queryNames(myAppMBean, null));

		// iterate through the set and get the application and state and add to JSON return data
		for (ObjectName applicationMbean : appMBeans)
		{
			String name = applicationMbean.getKeyProperty("name");
			String state = (String) mbs.getAttribute(applicationMbean, "State");
			returnData.put(name, state);
		}		
		
		// return the JSON object
		return returnData;
	} 
	
}