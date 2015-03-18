/**
 * 
 */
package com.util.util.key;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


/**
 * @author Johnny Zurita Medranda.
 */
public abstract class GenericTransaction {
	public static String classReference;
	public static String id;
	protected static Logger log;
	
	//variables de alarmas 
	protected Date ld_fecha = null;
	protected int firstmail;
	int li_time_minutes_alarm = 10;
	/*
	public GenericTransaction(String id, String classReference){
		this.id = id;
		this.classReference = classReference;
	}*/
	protected static void setLogger(){
		Environment.log.debug(new StringBuffer().append("Setting Logger "+id+".log for "+classReference).toString());
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d %X{thread-id} [%-5p] %m%n");
		DailyRollingFileAppender appender = new DailyRollingFileAppender();
		appender.setFile("./logs/"+id+".log");
		appender.setDatePattern("'.'yyyy-MM-dd");
		appender.setLayout(layout);
		appender.activateOptions();
		appender.setName(id);
		log = Logger.getLogger(id);
		log.addAppender(appender);
		log.setLevel(Level.DEBUG);
	}
		
	protected void closeLogger(){
		Appender appender = log.getAppender(id);
		log.removeAppender(appender);
	}
	
	protected long freeMemory(){
		System.gc();
		return Runtime.getRuntime().freeMemory();
	}

}

