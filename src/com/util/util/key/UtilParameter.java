package com.util.util.key;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class UtilParameter {

	protected Logger log;
	
	protected void setLogger(){
		/*
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
		*/
	}
}
