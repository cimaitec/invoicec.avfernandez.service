/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileReader;
/*    */ 
/*    */ public class Configuration
/*    */ {
/*  9 */   private BufferedReader environment = null;
/* 10 */   private String fileName = "";
/*    */ 
/*    */   public Configuration(String propertyFile) throws Exception
/*    */   {
/* 14 */     this.fileName = propertyFile;
/*    */   }
/*    */ 
/*    */   public String getParameter(String parameter) throws Exception
/*    */   {
/* 19 */     String value = "";
/* 20 */     String result = "";
/* 21 */     int inicio = 0;
/* 22 */     this.environment = new BufferedReader(new FileReader(this.fileName));
/* 23 */     while ((value = this.environment.readLine()) != null)
/*    */     {
/* 25 */       if (value.indexOf(parameter) != -1)
/*    */       {
/* 27 */         inicio = value.indexOf("=") + 1;
/* 28 */         if (inicio > 0)
/*    */         {
/* 30 */           result = value.substring(inicio).trim();
/* 31 */           break;
/*    */         }
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 37 */     this.environment.close();
/* 38 */     return result;
/*    */   }
/*    */ }
