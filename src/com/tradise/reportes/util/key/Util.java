/*     */ package com.tradise.reportes.util.key;
/*     */ 
/*     */ import java.util.Date;
/*     */ import org.apache.commons.configuration.Configuration;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   public static final String FORMATO_FECHA_DB = "yyyy-MM-dd";
/*     */   public static final int EDITAR = 1;
/*  14 */   public static final String log_control = Environment.c.getString("facElectronica.log.control");
/*  15 */   public static final String file_control = Environment.c.getString("facElectronica.ctrl-on-off.file");
/*     */   public static final String name_proyect = "FACT_ELECTRONICA";
/*  19 */   public static final String driverConection = Environment.c.getString("facElectronica.database.driver");
/*  20 */   public static final String urlConection = Environment.c.getString("facElectronica.database.facturacion.url");
/*  21 */   public static final String userConection = Environment.c.getString("facElectronica.database.facturacion.user");
/*  22 */   public static final String passwordConection = Environment.c.getString("facElectronica.database.facturacion.password");
/*     */ 
/*  25 */   public static final String host = Environment.c.getString("facElectronica.alarm.email.host");
/*  26 */   public static final String from = Environment.c.getString("facElectronica.alarm.email.sender");
/*  27 */   public static final String list_email = Environment.c.getString("facElectronica.alarm.email.receivers-list");
/*  28 */   public static final String subject = Environment.c.getString("facElectronica.alarm.email.subject");
/*  29 */   public static final String pieMensaje = Environment.c.getString("facElectronica.alarm.email.final-message");
/*  30 */   public static final String enablemail = Environment.c.getString("facElectronica.alarm.email.enable");
/*  31 */   //public static final int time_mail = Environment.c.getInt("facElectronica.alarm.email.time-mail");
/*     */ 
/*     */   public static double calcTimeMin(Date ld_fechaInicial, Date ld_fechaFinal)
/*     */   {
/*     */     try
/*     */     {
/* 124 */       long fechaIni = ld_fechaInicial.getTime();
/* 125 */       long fechaFin = ld_fechaFinal.getTime();
/* 126 */       double minutes = fechaFin - fechaIni;
/* 127 */       return minutes / 60000.0D;
/*     */     } catch (Exception e) {
/* 129 */       e.printStackTrace();
/* 130 */     }return -1.0D;
/*     */   }
/*     */ }
