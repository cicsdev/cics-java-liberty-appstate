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
import java.util.HashSet;
import java.util.Set;

public class AppConfig extends javax.ws.rs.core.Application
{
	
	//List the JAX-RS classes that contain annotations
	public Set<Class<?>> getClasses()
	{
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		classes.add(com.ibm.cicsdev.jmxquery.AppGet.class);
		classes.add(com.ibm.cicsdev.jmxquery.AppGetAll.class);
		
		return classes;
	}
}
