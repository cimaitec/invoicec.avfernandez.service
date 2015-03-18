package com.sun.comprobantes.util;

import java.util.Date;


/**
 * @author Raul Collahuazo G.
 *13/04/2012
 */
public class Util {
	public static final String FORMATO_FECHA_DB = "yyyy-MM-dd";
	public static final int EDITAR = 1;
	

	//#######- Parametros de Mail-#######
	public static final String host=Environment.c.getString("facElectronica.alarm.email.host");
	public static final String from=Environment.c.getString("desGSM.alarm.email.sender");
	public static final String list_email=Environment.c.getString("desGSM.alarm.email.receivers-list");
	//public static final String subject=Environment.c.getString("desGSM.alarm.email.subject");
	//public static final String pieMensaje=Environment.c.getString("desGSM.alarm.email.final-message");
	//public static final String enablemail= Environment.c.getString("desGSM.alarm.email.enable");
	//public static final int time_mail= Environment.c.getInt("desGSM.alarm.email.time-mail");
	
	//#######- Nombres de Arhivos Logs, Crtl -#######
	public static final String log_control=Environment.c.getString("desGSM.log.control");
	public static final String file_control=Environment.c.getString("desGSM.ctrl-on-off.file");
	public static final String name_proyect="FAC-ELECTRONICA";
	
	//#######- Calculo de tiempo entre dos fechas -#######
	public static double calcTimeMin(Date ld_fechaInicial, Date ld_fechaFinal){
		 try{
				  long fechaIni = ld_fechaInicial.getTime();
				  long fechaFin = ld_fechaFinal.getTime();  
				  double minutes = fechaFin - fechaIni;	 
				  return (minutes/(1000*60)); 
		 }catch(Exception e){
			 e.printStackTrace();
			 return -1;
		 }
	 }
	
			
	
}

