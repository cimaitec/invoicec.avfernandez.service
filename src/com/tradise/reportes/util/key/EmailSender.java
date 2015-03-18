/*     */ package com.tradise.reportes.util.key;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ import java.util.Properties;
/*     */ import javax.mail.BodyPart;
import javax.mail.Message;
/*     */ import javax.mail.Message.RecipientType;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
/*     */ 
/*     */ public class EmailSender
/*     */ {
/*  26 */   private String from = null;
/*  27 */   private String host = null;
/*     */   private Logger log;
/*     */   private int firstmail;
/*     */ 
/*     */   public EmailSender(String host, String from)
/*     */   {
/*  33 */     this.host = host;
/*  34 */     this.from = from;
/*     */   }
/*     */ 
/*     */   public EmailSender(Logger log) {
/*  38 */     this.log = log;
/*     */   }
/*     */ 
/*     */   public String send(String to, String subject, String message)
/*     */   {
/*  43 */     String result = "Enviado";
/*     */     try {
/*  45 */       Properties prop = System.getProperties();
/*  46 */       prop.put("mail.smtp.host", this.host);
/*  47 */       Session sesion = Session.getDefaultInstance(prop, null);
/*  48 */       MimeMessage msg = new MimeMessage(sesion);
/*  49 */       msg.setFrom(new InternetAddress(this.from));
/*  50 */       String[] receivers = to.split(";");
/*  51 */       for (int i = 0; i < receivers.length; i++) {
/*  52 */         msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers[i]));
/*     */       }
/*  54 */       msg.setSubject(subject);
/*  55 */       BodyPart messageBodyPart = new MimeBodyPart();
/*  56 */       messageBodyPart.setText(message);
/*  57 */       Multipart multipart = new MimeMultipart();
/*  58 */       multipart.addBodyPart(messageBodyPart);
/*  59 */       msg.setContent(multipart);
/*  60 */       Transport.send(msg);
/*     */     }
/*     */     catch (MessagingException me) {
/*  63 */       result = me.toString();
/*     */     }
/*  65 */     return result;
/*     */   }
/*     */ 
/*     */   public String Envia_Mensaje(String cuerpo)
/*     */   {
/*  72 */     String result = "";
/*  73 */     if (Util.enablemail.equals("Y")) {
/*  74 */       EmailSender e = new EmailSender(Util.host, Util.from);
/*  75 */       result = e.send(Util.list_email, Util.subject, cuerpo + Util.pieMensaje);
/*  76 */       if (result.equals("Enviado"))
/*     */       {
/*  78 */         System.out.println("Estado del correo: " + result);
/*     */       }
/*  80 */       else System.err.println("No se procedio a enviar el correo " + result);
/*     */ 
/*     */     }
/*     */ 
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   public Date Verifica_Tiempo_Mensaje(String cuerpo, Date ld_fecha, int firstmail)
/*     */   {
/*  96 */     String result = "";
/*     */ 
/*  98 */     int li_time_minutes_alarm = 0;
/*  99 */     String mensaje = "";
/*     */     try {
/* 101 */       li_time_minutes_alarm = 10000;
/*     */     } catch (Exception e) {
/* 103 */       li_time_minutes_alarm = 10;
/*     */     }
/*     */ 
/* 106 */     double fecha_i = Util.calcTimeMin(ld_fecha, new Date());
/* 107 */     if ((fecha_i > li_time_minutes_alarm) || (firstmail == 1)) {
/*     */       try
/*     */       {
/* 110 */         EmailSender e = new EmailSender(Util.host, Util.from);
/*     */ 
/* 112 */         result = e.Envia_Mensaje(cuerpo);
/* 113 */         if (result.equals("Enviado")) {
/* 114 */           this.log.info("**Se procedio a enviar el correo a los siguientes destinatarios: " + Util.list_email);
/* 115 */           ld_fecha = new Date();
/*     */ 
/* 117 */           setFirstmail(0);
/*     */         } else {
/* 119 */           this.log.info("**No se puede enviar correos a los destinatarios: " + Util.list_email);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 123 */         System.err.println("microcontainer..GenericTransaction::**Error de conexion-->" + e.getMessage());
/* 124 */         this.log.error("microcontainer..GenericTransaction::**Error de conexion-->" + e.getMessage());
/*     */       }
/*     */     }
/*     */     else {
/* 128 */       System.out.println("**El tiempo  minimo de envio entre correo no ha sido superado");
/*     */     }
/*     */ 
/* 132 */     return ld_fecha;
/*     */   }
/*     */ 
/*     */   public int getFirstmail() {
/* 136 */     return this.firstmail;
/*     */   }
/*     */ 
/*     */   public void setFirstmail(int firstmail) {
/* 140 */     this.firstmail = firstmail;
/*     */   }
/*     */ }
