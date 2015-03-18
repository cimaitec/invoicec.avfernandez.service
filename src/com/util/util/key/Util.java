/**
 * 
 */
package com.util.util.key;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;

import com.sun.comprobantes.util.EmailSender;


public class Util {
	public static final String FORMATO_FECHA_DB = "yyyy-MM-dd";
	public static final int EDITAR = 1;
	
	//#######- Nombres de Arhivos Logs, Crtl -#######
	public static final String log_control=Environment.c.getString("facElectronica.log.control");
	public static final String file_control=Environment.c.getString("facElectronica.ctrl-on-off.file");
	public static final String name_proyect="FACT_ELECTRONICA";
	
	//#######- Parametros de Conexion Database -#######
	public static final String driverConection=Environment.c.getString("facElectronica.database.facturacion.driver");
	public static final String urlConection=Environment.c.getString("facElectronica.database.facturacion.url");
	public static final String userConection=Environment.c.getString("facElectronica.database.facturacion.user");
	public static final String passwordConection=Environment.c.getString("facElectronica.database.facturacion.password");		
	public static final String schemeKey = Environment.c.getString("facElectronica.database.facturacion.scheme-login");
	public static final String keyFile = Environment.c.getString("facElectronica.database.facturacion.keyFile");	
	
	//#######- Parametros de Mail-#######
	public static final String host=Environment.c.getString("facElectronica.alarm.email.host");
	public static final String from=Environment.c.getString("facElectronica.alarm.email.sender");
	public static final String list_email=Environment.c.getString("facElectronica.alarm.email.receivers-list");
	public static final String subject=Environment.c.getString("facElectronica.alarm.email.subject");
	public static final String pieMensaje=Environment.c.getString("facElectronica.alarm.email.final-message");
	public static final String enablemail= Environment.c.getString("facElectronica.alarm.email.enable");
	//public static final int time_mail= Environment.c.getInt("facElectronica.alarm.email.time-mail");
	
	public static final int timeWait= Environment.c.getInt("facElectronica.general.time-wait");
	public static final int timeOutRecepComp = Environment.c.getInt("facElectronica.general.ws.RecepcionComprobantes.timeOut");
	
	
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

