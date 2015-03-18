package com.sun.directory.examples;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import com.sun.DAO.ControlErrores;
import com.sun.businessLogic.validate.Emisor;
import com.sun.comprobantes.util.FormGenerales;
import com.util.webServices.EnvioComprobantesWs;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;



public class PingWebServices {

	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 */
	public static boolean ping(String url, int timeout) {
	    url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    } catch (IOException exception) {
	        return false;
	    }
	}
	
	
	public static void main(String[] args) {
	/*	if (ping("https://cel.sri.gob.ec",20000)){
			System.out.println("Url::Available");
		}else{
			System.out.println("Url::Sin Conexion");
		}*/

		 ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
   	  try{
   	  		respuestaRecepcion = solicitudRecepcion(null, null, null, null);					        	  
   	  }catch(Exception e){
   		  System.out.println("ERROR:"+e.toString());
   		  e.printStackTrace();
   		  
   		  if (respuestaRecepcion == null)
   			  respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
   		  	  respuestaRecepcion.setEstado("SIN-RESPUESTA");
   	  }
	}
	public static ec.gob.sri.comprobantes.ws.RespuestaSolicitud solicitudRecepcion(File archivoFirmado, 
			   Emisor emi, 
			   ArrayList<ControlErrores> ListErrorGeneral, 
			   ArrayList<ControlErrores> ListWarnGeneral)
		{
		ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = null;
		try{		
		String flagErrores = "";	
		respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
		
			RespuestaSolicitud respuesta = new RespuestaSolicitud();
			EnvioComprobantesWs cliente = null;
			try {       
			cliente = new EnvioComprobantesWs(FormGenerales.devuelveUrlWs(new Integer(1).toString() ,"RecepcionComprobantes"),
					   20000);
			cliente.webService(FormGenerales.devuelveUrlWs(new Integer(1).toString() ,"RecepcionComprobantes"));
			}catch(Exception exc){    	 
			 System.out.println("Error,java.Exception,EnvioComprobantesWs.obtenerRespuestaEnvio,"+exc.getMessage());
			 exc.printStackTrace();
			}
		}catch(Exception exc){
		respuestaRecepcion.setEstado(exc.getMessage());    
		}
	return respuestaRecepcion;		
	}	
	
	public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String ruc, 
   			String tipoComprobante, String claveDeAcceso, String urlWsdl, int timeout, 
   			ArrayList<ControlErrores> ListErrorGeneral, ArrayList<ControlErrores> ListWarnGeneral) throws Exception
{
RespuestaSolicitud respuesta = new RespuestaSolicitud();
EnvioComprobantesWs cliente = null;
try {       
 
 
cliente = new EnvioComprobantesWs(urlWsdl,timeout);
//respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0");


}catch(Exception exc){    	 
 System.out.println("Error,java.Exception,EnvioComprobantesWs.obtenerRespuestaEnvio,"+exc.getMessage());
 exc.printStackTrace();
}

//COMENTARIADO POR PRUEBAS 
return respuesta;
}
}
