package com.sun.comprobantes.util;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



/**
 * @author Johnny Zurita Medranda
 * 26/09/2013
 */
public class Environment {
	static String classReference = "Environment";
	public static CtrlFile cf;
	public static Configuration c;
	public static Logger log;
	
	public static void setConfiguration(String configFile) throws IOException {
		c= loadInitialConfiguration(configFile);
	}
	
	public static Configuration loadInitialConfiguration(String file) throws IOException{
        Configuration configuration= null;
        ConfigurationFactory factory = null;
        try {
        	configuration= new PropertiesConfiguration(file);  
        	if(file.endsWith(".xml")){
        	   factory = new ConfigurationFactory(file);
			   configuration= factory.getConfiguration();
        	}else{
        	   configuration= new PropertiesConfiguration(file);
        	}
		} catch (ConfigurationException e) {
			System.err.println("**Error::::::::::"+classReference+".loadInitialConfiguration"+ e.getMessage());
		}
        return configuration;
	}
	
	public static void setCtrlFile(){
		System.out.println(Util.name_proyect+classReference+".setCtrlFile"+"Loading ControlFile "+Util.file_control);
		cf = new CtrlFile(Util.file_control);
	}
			
	public static Logger getLog(){
		return log;
	}
	
	public static void setLogger(String logConfigFile) {
		log = Logger.getLogger("file");
		PropertyConfigurator.configureAndWatch(logConfigFile);
	}


}

