/*     */ package com.tradise.reportes.util.key;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class Loader
/*     */ {
/*     */   static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
/*  20 */   private static boolean java1 = true;
/*     */ 
/*  22 */   private static boolean ignoreTCL = false;
/*     */ 
/*     */   static {
/*  25 */     ConfigLoader config = new ConfigLoader();
/*  26 */     String prop = config.getSystemProperty("java.version", null);
/*     */ 
/*  28 */     if (prop != null) {
/*  29 */       int i = prop.indexOf('.');
/*  30 */       if ((i != -1) && 
/*  31 */         (prop.charAt(i + 1) != '1')) {
/*  32 */         java1 = false;
/*     */       }
/*     */     }
/*  35 */     String ignoreTCLProp = config.getSystemProperty("log4j.ignoreTCL", null);
/*  36 */     if (ignoreTCLProp != null)
/*  37 */       ignoreTCL = config.toBoolean(ignoreTCLProp, true);
/*     */   }
/*     */ 
/*     */   public URL getResource(String resource)
/*     */   {
/*  61 */     ClassLoader classLoader = null;
/*  62 */     URL url = null;
/*     */     try
/*     */     {
/*  65 */       if (!java1) {
/*  66 */         classLoader = getTCL();
/*  67 */         if (classLoader != null) {
/*  68 */           Environment.log.debug("Trying to find [" + resource + "] using context classloader " + 
/*  69 */             classLoader + ".");
/*  70 */           url = classLoader.getResource(resource);
/*  71 */           if (url != null) {
/*  72 */             return url;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  79 */       classLoader = Loader.class.getClassLoader();
/*  80 */       if (classLoader != null) {
/*  81 */         Environment.log.debug("Trying to find [" + resource + "] using " + classLoader + 
/*  82 */           " class loader.");
/*  83 */         url = classLoader.getResource(resource);
/*  84 */         if (url != null)
/*  85 */           return url;
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/*  89 */       Environment.log.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
/*     */ 
/*  96 */       Environment.log.debug("Trying to find [" + resource + 
/*  97 */         "] using ClassLoader.getSystemResource().");
/*  98 */     }return ClassLoader.getSystemResource(resource);
/*     */   }
/*     */ 
/*     */   public boolean isJava1()
/*     */   {
/* 106 */     return java1;
/*     */   }
/*     */ 
/*     */   private ClassLoader getTCL()
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 119 */     Method method = null;
/*     */     try {
/* 121 */       method = Thread.class.getMethod("getContextClassLoader", null);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 124 */       return null;
/*     */     }
/*     */ 
/* 127 */     return (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */   }
/*     */ 
/*     */   public Class loadClass(String clazz)
/*     */     throws ClassNotFoundException
/*     */   {
/* 142 */     if ((java1) || (ignoreTCL))
/* 143 */       return Class.forName(clazz);
/*     */     try
/*     */     {
/* 146 */       return getTCL().loadClass(clazz);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/* 151 */     return Class.forName(clazz);
/*     */   }
/*     */ 
/*     */   public void configure()
/*     */   {
/*     */   }
/*     */ }
