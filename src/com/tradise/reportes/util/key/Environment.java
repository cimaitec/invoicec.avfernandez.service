/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import org.apache.commons.configuration.Configuration;
/*    */ import org.apache.commons.configuration.ConfigurationException;
/*    */ import org.apache.commons.configuration.ConfigurationFactory;
/*    */ import org.apache.commons.configuration.PropertiesConfiguration;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ 
/*    */ public class Environment
/*    */ {
/* 20 */   static String classReference = "Environment";
/*    */   public static CtrlFile cf;
/*    */   public static Configuration c;
/*    */   public static Logger log;
/*    */ 
/*    */   public static void setConfiguration(String configFile)
/*    */     throws IOException
/*    */   {
/* 26 */     c = loadInitialConfiguration(configFile);
/*    */   }
/*    */ 
/*    */   public static Configuration loadInitialConfiguration(String file) throws IOException {
/* 30 */     Configuration configuration = null;
/* 31 */     ConfigurationFactory factory = null;
/*    */     try {
/* 33 */       configuration = new PropertiesConfiguration(file);
/* 34 */       if (file.endsWith(".xml")) {
/* 35 */         factory = new ConfigurationFactory(file);
/* 36 */         configuration = factory.getConfiguration();
/*    */       } else {
/* 38 */         configuration = new PropertiesConfiguration(file);
/*    */       }
/*    */     } catch (ConfigurationException e) {
/* 41 */       System.err.println("**Error::::::::::" + classReference + ".loadInitialConfiguration" + e.getMessage());
/*    */     }
/* 43 */     return configuration;
/*    */   }
/*    */ 
/*    */   public static void setCtrlFile() {
/* 47 */     System.out.println("FACT_ELECTRONICA" + classReference + ".setCtrlFile" + "Loading ControlFile " + Util.file_control);
/* 48 */     cf = new CtrlFile(Util.file_control);
/*    */   }
/*    */ 
/*    */   public static Logger getLog() {
/* 52 */     return log;
/*    */   }
/*    */ 
/*    */   public static void setLogger(String logConfigFile) {
/* 56 */     log = Logger.getLogger("file");
/* 57 */     PropertyConfigurator.configureAndWatch(logConfigFile);
/*    */   }
/*    */ }
