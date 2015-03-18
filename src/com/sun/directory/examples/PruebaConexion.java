package com.sun.directory.examples;

import java.io.File;
import java.util.ArrayList;

import com.sun.DAO.ControlErrores;
import com.sun.businessLogic.validate.Emisor;
import com.sun.comprobantes.util.FormGenerales;
import com.util.webServices.EnvioComprobantesWs;

public class PruebaConexion {

	
	
	  public static void main(String[] args) {
		  ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = null;
		  ArrayList<ControlErrores> ListErrorGeneral = new ArrayList<ControlErrores>();
		  ArrayList<ControlErrores> ListWarnGeneral = new ArrayList<ControlErrores>();
		  Emisor emisor = new Emisor();
		  emisor.getInfEmisor().setAmbiente(1);
		  File archivoFirmado = new File("prueba.xml");
		  try{
		  		respuestaRecepcion = solicitudRecepcion(archivoFirmado, emisor, ListErrorGeneral, ListWarnGeneral);					        	  
		  }catch(Exception e){
			  e.printStackTrace();
			  if (respuestaRecepcion == null)
				  respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
			  respuestaRecepcion.setEstado("SIN-RESPUESTA");
		  }
		  System.out.println("Respuesta::"+respuestaRecepcion);
		  System.out.println("Estado::"+respuestaRecepcion.getEstado());
	  }
		//***********************//////////////////////////////////////////////////////************************************//
		/*									   	Envios al Sri por WebServices											  */
		//***********************//////////////////////////////////////////////////////************************************//	
		public static ec.gob.sri.comprobantes.ws.RespuestaSolicitud solicitudRecepcion(File archivoFirmado, 
																					   Emisor emi, 
																					   ArrayList<ControlErrores> ListErrorGeneral, 
																					   ArrayList<ControlErrores> ListWarnGeneral) throws Exception
		{
			ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = null;
			String flagErrores = "";	
				respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
		    	respuestaRecepcion = EnvioComprobantesWs.obtenerRespuestaEnvio(archivoFirmado, 
		    																   emi.getInfEmisor().getRuc(), 
		    																   emi.getInfEmisor().getCodDocumento(), 
		    																   emi.getInfEmisor().getClaveAcceso(), 
		    																   FormGenerales.devuelveUrlWs(new Integer(emi.getInfEmisor().getAmbiente()).toString() ,"RecepcionComprobantes"),
		    																   20000);
	        
			return respuestaRecepcion;		
		}		  
}
