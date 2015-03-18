/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.util.Date;
/*    */ import org.apache.log4j.Appender;
/*    */ import org.apache.log4j.DailyRollingFileAppender;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
		 import org.apache.log4j.PatternLayout;
/*    */ 
/*    */ public abstract class GenericTransaction implements Runnable
/*    */ {
/*    */   public static String classReference;
/*    */   public static String id;
/*    */   protected static Logger log;
/* 25 */   protected Date ld_fecha = null;
/*    */   protected int firstmail;
/* 27 */   int li_time_minutes_alarm = 10;
/*    */ 
/*    */   protected static void setLogger()
/*    */   {
/* 34 */     Environment.log.debug(new StringBuilder("Setting Logger ").append(id).append(".log for ").append(classReference).toString());
/* 35 */     PatternLayout layout = new PatternLayout();
/* 36 */     layout.setConversionPattern("%d %X{thread-id} [%-5p] %m%n");
/* 37 */     DailyRollingFileAppender appender = new DailyRollingFileAppender();
/* 38 */     appender.setFile("./logs/" + id + ".log");
/* 39 */     appender.setDatePattern("'.'yyyy-MM-dd");
/* 40 */     appender.setLayout(layout);
/* 41 */     appender.activateOptions();
/* 42 */     appender.setName(id);
/* 43 */     log = Logger.getLogger(id);
/* 44 */     log.addAppender(appender);
/* 45 */     log.setLevel(Level.DEBUG);
/*    */   }
/*    */ 
/*    */   protected void closeLogger() {
/* 49 */     Appender appender = log.getAppender(id);
/* 50 */     log.removeAppender(appender);
/*    */   }
/*    */ 
/*    */   protected long freeMemory() {
/* 54 */     System.gc();
/* 55 */     return Runtime.getRuntime().freeMemory();
/*    */   }

		   public void run() {
				// TODO Auto-generated method stub
				
		   }
/*    */ }
