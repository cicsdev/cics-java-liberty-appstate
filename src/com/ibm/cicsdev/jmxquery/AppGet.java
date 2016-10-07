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

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;

import com.ibm.json.java.JSONObject;
//import com.ibm.websphere.application.ApplicationMBean;

@javax.ws.rs.Path("/state/{appName}")
public class AppGet {

	/**
	 * The GET HTTP Method is used to get (or browse) application status
	 *  This method will browse the ApplicationMBeans to find the application and report its state
	 */
	@javax.ws.rs.GET
	@Produces("application/json")
	public JSONObject browseApps(@javax.ws.rs.PathParam("appName") String appName) throws Exception {

		// create an JSONObject to return
		JSONObject returnData = new JSONObject();
   
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		ObjectName myAppMBean = new ObjectName(
				"WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name="+appName);
				if (mbs.isRegistered(myAppMBean)) {
					String state = (String) mbs.getAttribute(myAppMBean, "State");	
					
					// put the name of the app
					returnData.put("appName", appName);

					// put the state
					returnData.put("State", state);
				} else {
					// return error
					returnData.put("appName", appName);
					returnData.put("Error", "NOT FOUND");			
				}		
		
		// return the object
		return returnData;
	}
	
}