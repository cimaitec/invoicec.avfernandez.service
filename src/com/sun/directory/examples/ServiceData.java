package com.sun.directory.examples;
import java.io.*; 
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.ArrayList;
import java.util.List;
//import java.util.Properties;






import com.sun.DAO.InformacionTributaria;
import com.sun.businessLogic.validate.Emisor;
import com.sun.comprobantes.util.EmailSender;
//import com.tradise.reportes.entidades.FacCabDocumento;
//import com.tradise.reportes.servicios.ReporteSentencias;
import com.util.util.key.Environment;
import com.util.util.key.Util;
import com.sun.database.ConexionBase;

import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
	
public class ServiceData extends com.util.util.key.GenericTransaction {

	public static String classReference = "ServiceData";
	//public static String id = "1.0";
	public static StringBuilder SBmsj = null;
	public File fxml = null;	
	public static int contador;
	public static int numHilo;
	public static int sleepHilo;
	public static int sleepBloqueHilo;
	
	public static File[] contenido;
	
	public InfoEmpresa InforEmpresa = null;
	public static String databaseMotor=null;
	public static String databaseType=null;
	
	public static void iniServiceData(){
		// VPI - GBA
		ConexionBase.init();
	}
		
	public static void main(String args[]) throws Exception {
		Emisor emite = new Emisor();
		SBmsj = new StringBuilder();
		try {
			Environment.setConfiguration("facturacion.xml");
			Environment.setCtrlFile();
			ServiceData.iniServiceData();
			emite.insertaBitacora(emite, "", "<<<<<<Inicio del Invoice>>>>>", "", "", "", "");
		} catch (Exception ex) {
					SBmsj.append(classReference
					+ "::main>>FacturacionElectronica.Service::main::Proceso de Carga de Archivo Xml Configuraciones::::"
					+ ". Proceso de Emision de Documentos no se levanto.");
			
			int li_envio =enviaEmail("message_error", emite, SBmsj.toString(),"", null, null);
			String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite, "EX", "",SBmsj.toString()+" >> "+resultEmail, "", "", "");
			throw new Exception(SBmsj.toString());
		}
		String ruc = args[0];
		System.out.println("Ruc::" + ruc);
		if ((ruc == null) || ruc.equals("") || (ruc.length() < 13)) {
			SBmsj.append("Error::" + classReference
					+ ":: Debe enviar el parametro de Ruc Correcto. Ruc->"
					+ ruc + ". Proceso de Emision de Documentos no se levanto.");
			
			int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),
					"", "", null);
			String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite, "EX", "",SBmsj.toString()+" >> "+resultEmail, "", "", "");
			throw new Exception(SBmsj.toString());
		}

		emite = new Emisor();
		ServiceData.databaseType = Util.driverConection;
		InfoEmpresa infEmp = new InfoEmpresa();
		if (!emite.existeEmpresa(ruc)) {
			String mensaje = " Empresa no existe o no se encuentra Activa. Ruc->"
					+ ruc + ". Proceso de Emision de Documentos no se levanto.";
			int li_envio = enviaEmail("message_error", emite, mensaje, mensaje,
					null, null);
			String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite, "EX","", SBmsj.toString()+" >> "+resultEmail,  "", "", "");
			
			throw new Exception(mensaje);
		}

		if (ServiceData.databaseType.indexOf("postgresql") > 0)
			ServiceData.databaseType = "PostgreSQL";
		if (ServiceData.databaseType.indexOf("sqlserver") > 0)
			ServiceData.databaseType = "SQLServer";
		infEmp = emite.obtieneInfoEmpresa(ruc);
		numHilo = Integer.parseInt(Environment.c
				.getString("facElectronica.general.EMISION.numHilos"));
		sleepHilo = Integer.parseInt(Environment.c
				.getString("facElectronica.general.EMISION.sleepHilos"));
		sleepBloqueHilo = Integer.parseInt(Environment.c
				.getString("facElectronica.general.EMISION.sleepBloqueHilo"));
		Thread hilo = null;
		contador = 0;
		//Envio de correo para inicio de servicio
		ServiceDataHilo.enviaEmail("message_service_up",emite, "",(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())), null,null);
		while ((Environment.cf.readCtrl().equals("S"))) {
		
		
			try {
				contenido = FileDemo.busqueda(infEmp.getDirectorio(), ".xml");
				if (contenido != null) {
					if (contenido.length > 0) {
						// listAtencion = new ArrayList();
						for (int i = 0; i < contenido.length; i++) {
							// VPI Bucle principal que asigna archivo a los
							// hilos

							// VPI se agrega validacion para que verifique
							// archivo de control en cada vuelta
							if (Environment.cf.readCtrl().equals("N")) {
								break;
							}
							
							if (contador == numHilo) {
								hilo.join();
								new Thread().sleep(sleepBloqueHilo);
								// logPrin.debug(">>>---Limpiando Memoria");
								System.gc();

								contador = 0;
							}
							++contador;
							emite.insertaBitacora(emite, String.valueOf(contador) ,"Archivo a Procesar::"+contenido[i].getName(), "", "", "", "");
							System.out.println("Name File::"
									+ contenido[i].getName());
							File fileProcesar = new File(
									contenido[i].getAbsolutePath());

							// moviendo a ruta de Recibidos para procesarlo
							File fremove = new File(infEmp.getDirRecibidos()
									+ fileProcesar.getName());
							if (fremove.exists()) {
								fremove.delete();
							}

							fremove = new File(infEmp.getDirRecibidos()
									+ fileProcesar.getName().replace(".xml",
											"_backup.xml"));
							if (fremove.exists()) {
								fremove.delete();
							}

							if (fileProcesar
									.renameTo(new File(infEmp.getDirRecibidos()
											+ fileProcesar.getName()))) {

								fileProcesar = new File(
										infEmp.getDirRecibidos()
												+ fileProcesar.getName());

								if (fileProcesar.exists()) {
									emite.setFilexml(fileProcesar.getName());
									String fileGenerado = ArchivoUtils.archivoToString(fileProcesar.getAbsolutePath());
			
									ServiceDataHilo threadAtiende = new ServiceDataHilo(
											contador,
											infEmp,
											new Emisor(
													emite.getFilexml(),
													Integer.parseInt(infEmp
															.getContribEspecial()),
													infEmp.getObligContabilidad())
									);
									hilo = new Thread(threadAtiende);
									hilo.start();

									new Thread().sleep(sleepHilo);
								} else {
									emite.insertaBitacora(emite, String.valueOf(contador),"", "Error en Procesar::Archivo No Existe::"+contenido[i].getAbsolutePath()+" Asignado Hilo "+contador,  "", "", "");
								}
							} else {
								emite.insertaBitacora(emite, String.valueOf(contador) ,"","Error al Mover::Archivo No Existe::"+infEmp.getDirRecibidos()+fileProcesar.getName()+" Asignado Hilo "+contador,  "", "", "");
							}

							// VPI fin de bucle principal que asigna archivo a
							// los hilos
						}
						new Thread().sleep(sleepBloqueHilo);
					} else {
						new Thread().sleep(sleepBloqueHilo);
					}
				}
			} catch (Exception excep) {
				excep.printStackTrace();
				int li_envio = enviaEmail(
						"message_error",
						emite,
						"",
						"Proceso Invoice Error de Excepcion::"
								+ excep.toString(), "", "");
				
				String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
				emite.insertaBitacora(emite, "EX","", "Proceso Invoice Error de Excepcion::"+ excep.toString()+" >> "+resultEmail,  "", "", "");
			}
		}		
		emite.insertaBitacora(emite, "", "<<<<<<Termino del Invoice>>>>>>", "", "", "", "");
		//Envio de correo para inicio de servicio
		ServiceDataHilo.enviaEmail("message_service_down",emite, "", (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())), null,null);
	}
	
	
	public static void moveExist(String path, String filename, String pathMove){
	      File f = new File(path+filename);				      
		  if(f.exists()){
			  f.renameTo(new File(pathMove + filename));
		  }
		  f = new File(path+filename.replace(".xml", "_backup.xml"));
		  if(f.exists()){							  
			  f.renameTo(new File(pathMove + filename.replace(".xml", "_backup.xml")));
		  }
	}
	
		
	public static int moveFile(String absolutePathOrigen, String pathDestino){		 
   	 try{
	 File dataInputFile = new File(absolutePathOrigen); 
   	 File fileSendPath = new File(pathDestino, dataInputFile.getName());  
   	 dataInputFile.renameTo(fileSendPath);
   	 }catch(Exception e){
   		 return 0;
   	 }
   	 return 1;
	}
			
	public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf){
		String emailHost = null;
		String emailFrom = null;
		String emailTo = null;
		//String emailSubject = null;
		String emailMensaje = null;	
		String emailHelpDesk = null;
		//Host Mail Server
				emailHost = Environment.c.getString("facElectronica.alarm.email.host");
				//Enviado desde
				emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
				//Enviado para
				emailTo = Environment.c.getString("facElectronica.alarm.email.receivers-list");
				//Asunto
				//emailSubject = Environment.c.getString("facElectronica.alarm.email.subject");
				//Email HelpDesk
				emailHelpDesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		EmailSender emSend = new EmailSender(emailHost,emailFrom);
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);
		
		//String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String receivers = "";
		String user = Environment.c.getString("facElectronica.alarm.email.user");
		String port = Environment.c.getString("facElectronica.alarm.email.port");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		
		
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");				
		emSend.setPassword(clave);
		emSend.setSubject(subject);
		emSend.setUser(user);
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);
		emSend.setPort(port);
		
		
		emailTo = receivers;
		
		String noDocumento = "";		
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			//System.out.println("Envio de Email");
			noDocumento = emi.getInfEmisor().getCodEstablecimiento()+emi.getInfEmisor().getCodPuntoEmision()+emi.getInfEmisor().getSecuencial();
		}		
		emailMensaje = emailMensaje.replace("|FECHA|", (emi.getInfEmisor().getFecEmision()==null?"":emi.getInfEmisor().getFecEmision().toString()));
		emailMensaje = emailMensaje.replace("|NODOCUMENTO|", (noDocumento==null?"":noDocumento));	
		emailMensaje = emailMensaje.replace("|HELPDESK|", emailHelpDesk);
		emailMensaje = StringEscapeUtils.unescapeHtml(emailMensaje);
		if (ls_id_mensaje.equals("message_error"))
		{
			emailMensaje = emailMensaje.replace("|CabError|", "Hubo inconvenientes con");
			emailMensaje = emailMensaje.replace("|Mensaje|", mensaje_error);
		}
		if (ls_id_mensaje.equals("message_exito"))
		{
			emailMensaje = emailMensaje.replace("|CabMensaje|", " ");
		}
				
		if ((emailTo!=null) && (emailTo.length()>0)){
			//String[] partsMail = emailTo.split(";");
			//for(int i=0;i<partsMail.length;i++)
				//if (partsMail[i].length()>0){
					emSend.send(emailTo
							    //partsMail[i]
							, 
								subject,
								emailMensaje,
			  		  	        fileAttachXml,
			  		  	        fileAttachPdf);
				//}
		}		
		return 0;
	}
	

	
}
