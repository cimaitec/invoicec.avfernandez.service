/*     */ package com.tradise.reportes.util.key;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class ConfigLoader
/*     */ {
/*  14 */   static String DELIM_START = "${";
/*  15 */   static char DELIM_STOP = '}';
/*  16 */   static int DELIM_START_LEN = 2;
/*  17 */   static int DELIM_STOP_LEN = 1;
/*     */ 
/*     */   public String[] concatanateArrays(String[] l, String[] r)
/*     */   {
/*  24 */     int len = l.length + r.length;
/*  25 */     String[] a = new String[len];
/*     */ 
/*  27 */     System.arraycopy(l, 0, a, 0, l.length);
/*  28 */     System.arraycopy(r, 0, a, l.length, r.length);
/*     */ 
/*  30 */     return a;
/*     */   }
/*     */ 
/*     */   public String convertSpecialChars(String s)
/*     */   {
/*  36 */     int len = s.length();
/*  37 */     StringBuffer sbuf = new StringBuffer(len);
/*     */ 
/*  39 */     int i = 0;
/*  40 */     while (i < len) {
/*  41 */       char c = s.charAt(i++);
/*  42 */       if (c == '\\') {
/*  43 */         c = s.charAt(i++);
/*  44 */         if (c == 'n') c = '\n';
/*  45 */         else if (c == 'r') c = '\r';
/*  46 */         else if (c == 't') c = '\t';
/*  47 */         else if (c == 'f') c = '\f';
/*  48 */         else if (c == '\b') c = '\b';
/*  49 */         else if (c == '"') c = '"';
/*  50 */         else if (c == '\'') c = '\'';
/*  51 */         else if (c == '\\') c = '\\';
/*     */       }
/*  53 */       sbuf.append(c);
/*     */     }
/*  55 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public String getSystemProperty(String key, String def)
/*     */   {
/*     */     try
/*     */     {
/*  72 */       return System.getProperty(key, def);
/*     */     } catch (Throwable e) {
/*  74 */       Environment.log.debug("Was not allowed to read system property \"" + key + "\".");
/*  75 */     }return def;
/*     */   }
/*     */ 
/*     */   public boolean toBoolean(String value, boolean dEfault)
/*     */   {
/* 103 */     if (value == null)
/* 104 */       return dEfault;
/* 105 */     String trimmedVal = value.trim();
/* 106 */     if ("true".equalsIgnoreCase(trimmedVal))
/* 107 */       return true;
/* 108 */     if ("false".equalsIgnoreCase(trimmedVal))
/* 109 */       return false;
/* 110 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public int toInt(String value, int dEfault)
/*     */   {
/* 115 */     if (value != null) {
/* 116 */       String s = value.trim();
/*     */       try {
/* 118 */         return Integer.valueOf(s).intValue();
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 121 */         Environment.log.error("[" + s + "] is not in proper int form.");
/* 122 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 125 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public String findAndSubst(String key, Properties props)
/*     */   {
/* 137 */     String value = props.getProperty(key);
/* 138 */     if (value == null)
/* 139 */       return null;
/*     */     try
/*     */     {
/* 142 */       return substVars(value, props);
/*     */     } catch (IllegalArgumentException e) {
/* 144 */       Environment.log.error("Bad option value [" + value + "].", e);
/* 145 */     }return value;
/*     */   }
/*     */ 
/*     */   public String substVars(String val, Properties props)
/*     */     throws IllegalArgumentException
/*     */   {
/* 224 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 226 */     int i = 0;
/*     */     while (true)
/*     */     {
/* 230 */       int j = val.indexOf(DELIM_START, i);
/* 231 */       if (j == -1)
/*     */       {
/* 233 */         if (i == 0) {
/* 234 */           return val;
/*     */         }
/* 236 */         sbuf.append(val.substring(i, val.length()));
/* 237 */         return sbuf.toString();
/*     */       }
/*     */ 
/* 240 */       sbuf.append(val.substring(i, j));
/* 241 */       int k = val.indexOf(DELIM_STOP, j);
/* 242 */       if (k == -1) {
/* 243 */         throw new IllegalArgumentException('"' + val + 
/* 244 */           "\" has no closing brace. Opening brace at position " + j + 
/* 245 */           '.');
/*     */       }
/* 247 */       j += DELIM_START_LEN;
/* 248 */       String key = val.substring(j, k);
/*     */ 
/* 250 */       String replacement = getSystemProperty(key, null);
/*     */ 
/* 252 */       if ((replacement == null) && (props != null)) {
/* 253 */         replacement = props.getProperty(key);
/*     */       }
/*     */ 
/* 256 */       if (replacement != null)
/*     */       {
/* 262 */         String recursiveReplacement = substVars(replacement, props);
/* 263 */         sbuf.append(recursiveReplacement);
/*     */       }
/* 265 */       i = k + DELIM_STOP_LEN;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void configure()
/*     */   {
/*     */   }
/*     */ }
