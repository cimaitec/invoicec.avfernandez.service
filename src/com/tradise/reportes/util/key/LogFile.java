/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.io.FileWriter;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class LogFile
/*    */ {
/* 16 */   private PrintWriter log = null;
/* 17 */   private DateTime fecha = new DateTime();
/*    */ 
/*    */   public LogFile() throws Exception
/*    */   {
/* 21 */     this.fecha = new DateTime();
/* 22 */     this.log = new PrintWriter(new FileWriter(this.fecha.getDate() + ".log", true), true);
/*    */   }
/*    */ 
/*    */   public LogFile(String prefix) throws Exception
/*    */   {
/* 27 */     this.fecha = new DateTime();
/* 28 */     this.log = new PrintWriter(new FileWriter(prefix + this.fecha.getDate() + ".log", true), true);
/*    */   }
/*    */ 
/*    */   public LogFile(String prefix, String complement) throws Exception
/*    */   {
/* 33 */     this.fecha = new DateTime();
/* 34 */     this.log = new PrintWriter(new FileWriter(prefix + this.fecha.getDate() + complement + ".log", true), true);
/*    */   }
/*    */ 
/*    */   public void close() throws Exception
/*    */   {
/* 39 */     this.log.close();
/*    */   }
/*    */ 
/*    */   public void println(String message)
/*    */   {
/* 44 */     this.log.println(message);
/*    */   }
/*    */ 
/*    */   public void printlnTime(String message)
/*    */   {
/* 49 */     this.fecha = new DateTime();
/* 50 */     this.log.println(message + " - " + this.fecha.getTime());
/*    */   }
/*    */ 
/*    */   public void printlnTime2(String message1, String message2)
/*    */   {
/* 55 */     this.fecha = new DateTime();
/* 56 */     this.log.println(message1 + " - " + this.fecha.getTime() + " - " + message2);
/*    */   }
/*    */ }
