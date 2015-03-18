/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileReader;
/*    */ 
/*    */ public class CtrlFile
/*    */ {
/* 14 */   private String filename = "";
/*    */ 
/*    */   public CtrlFile(String fileName) {
/* 17 */     this.filename = fileName;
/*    */   }
/*    */ 
/*    */   public String readCtrl() {
/* 21 */     String result = "S";
/*    */     try {
/* 23 */       FileReader filectr = new FileReader(this.filename);
/* 24 */       BufferedReader reader = new BufferedReader(filectr);
/* 25 */       String aux = reader.readLine();
/* 26 */       if (!aux.equals("S")) {
/* 27 */         result = "N";
/*    */       }
/* 29 */       reader.close();
/*    */     } catch (Exception e) {
/* 31 */       result = "N";
/*    */     }
/* 33 */     return result;
/*    */   }
/*    */ }
