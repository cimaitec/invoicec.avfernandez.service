/*    */ package com.tradise.reportes.util.key;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ 
/*    */ public class DateTime
/*    */ {
/* 13 */   private Calendar fecha = null;
/*    */ 
/*    */   public DateTime()
/*    */   {
/* 17 */     this.fecha = Calendar.getInstance();
/*    */   }
/*    */ 
/*    */   public String getDate()
/*    */   {
/* 22 */     int ano = this.fecha.get(1);
/* 23 */     int mes = this.fecha.get(2) + 1;
/* 24 */     int dia = this.fecha.get(5);
/* 25 */     return ano + (mes < 10 ? "0" : "") + mes + (dia < 10 ? "0" : "") + dia;
/*    */   }
/*    */ 
/*    */   public String getTime()
/*    */   {
/* 30 */     int hor = this.fecha.get(11);
/* 31 */     int min = this.fecha.get(12);
/* 32 */     int sec = this.fecha.get(13);
/* 33 */     return (hor < 10 ? "0" : "") + hor + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
/*    */   }
/*    */ 
/*    */   public String getTimeM()
/*    */   {
/* 38 */     int hor = this.fecha.get(11);
/* 39 */     int min = this.fecha.get(12);
/* 40 */     int sec = this.fecha.get(13);
/* 41 */     int mil = this.fecha.get(14);
/* 42 */     return (hor < 10 ? "0" : "") + hor + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec + ":" + mil;
/*    */   }
/*    */ 
/*    */   public String getTimeminutos()
/*    */   {
/* 48 */     int hor = this.fecha.get(11);
/* 49 */     int min = this.fecha.get(12);
/* 50 */     int sec = this.fecha.get(13);
/* 51 */     return (hor < 10 ? "0" : "") + hor + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
/*    */   }
/*    */ }
