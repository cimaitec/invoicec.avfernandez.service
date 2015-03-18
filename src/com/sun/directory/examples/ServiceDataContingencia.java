package com.sun.directory.examples;
import java.io.*; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import com.sun.DAO.BufferAtencion;
import com.sun.DAO.ControlErrores;
import com.sun.DAO.DetalleDocumento;
import com.sun.DAO.DetalleImpuestosRetenciones;
import com.sun.DAO.DetalleTotalImpuestos;
import com.sun.DAO.DocumentoImpuestos;
import com.sun.DAO.InformacionAdicional;
import com.sun.DAO.InformacionTributaria;
import com.sun.businessLogic.validate.Emisor;
import com.sun.businessLogic.validate.LeerDocumentos;
import com.sun.comprobantes.util.EmailSender;
import com.tradise.reportes.entidades.FacCabDocumento;
import com.tradise.reportes.entidades.FacDetDocumento;
import com.tradise.reportes.servicios.ReporteSentencias;
import com.util.util.key.Environment;
import com.util.util.key.Util;
import com.util.webServices.EnvioComprobantesWs;

//import ec.gob.sri.comprobantes.util.ArchivoUtils;
//import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import com.sun.comprobantes.util.FormGenerales;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
	
public class ServiceDataContingencia extends com.util.util.key.GenericTransaction {

	public static String classReference = "ServiceDataContingencia";
	public static StringBuilder SBmsj = null;
	public File fxml = null;	
	public static int contador;
	public static int numHilo;
	public static ArrayList<Emisor> Listcontenido = null;
	public static List listErrores = null;
	public static List listWarning = null;
	public static List listErroresEstados = null;
	public static List listWarningEstados = null;
	public static ArrayList<ControlErrores> ListErrorGeneral = null;
	public static ArrayList<ControlErrores> ListWarnGeneral = null;
	
	public InfoEmpresa InforEmpresa = null;
	public static List listAtencion = null;
	public static String databaseMotor=null;
	public static String databaseType=null;
	private static org.apache.log4j.Logger logContingencia = null;
	
	public static void iniServiceData(){	
		
		listErrores = Environment.c.getList("facElectronica.general.EMISION.error-wsdls.error-wsdl");		
		listWarning = Environment.c.getList("facElectronica.general.EMISION.warning-wsdls.warning-wsdl");
		listErroresEstados = Environment.c.getList("facElectronica.general.EMISION.error-wsdls.ESTADO");
		listWarningEstados = Environment.c.getList("facElectronica.general.EMISION.warning-wsdls.ESTADO");
		
	}
		
	public static void main( String args[] ) throws Exception {
	// Buscar en el raiz del disco C 
	// Se coloca doble slash puesto que es un caracter de escape. 
	// en literales cadenas en java se realiza: 
	// \n -> retorno de carro 
	// \t -> tabulador 
	// \\ -> slash 
	// \" -> comillas
	int ln_result = 0;
	int li_result = 0;
	Emisor emite = null;
	String tipoDocumento ="";	
	String ambiente = "";
	String rucFile= "";
	String CodEstablecimiento= "";
	String CodPuntEmision= "";
	String secuencial= "";
	boolean flagServiceDisponible=false;
	//setLogger();
	SBmsj = new StringBuilder();
	String nivelLog = "ALL";
	//Archivo de Configuracion
			String name_xml="facturacionContingencia.xml";    
			    try{
			    	Environment.setConfiguration(name_xml);
					Environment.setCtrlFile();
					Environment.setLogger(Util.log_control);
					ServiceDataContingencia.iniServiceData();
					//logContingencia.debug(">>Lectura de Archivo xml de Configuracion->"+name_xml);
					//logContingencia.debug(">>Lectura de Archivo xml de Configuracion->"+name_xml);
				  }catch(Exception ex){
					  SBmsj.append(classReference+"::main>>FacturacionElectronica.Service::main::Proceso de Carga de Archivo Xml Configuraciones::::"+". Proceso de Emision de Documentos no se levanto.");
					  int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),"",null, null);
					  //logContingencia.error(">>message_error-->"+SBmsj.toString());
					  throw new Exception(SBmsj.toString());
		    }
		
	
	
	Properties props=new Properties();
	//props.setProperty("log4j.appender.file","org.apache.log4j.RollingFileAppender");
	props.setProperty("log4j.appender.file","org.apache.log4j.DailyRollingFileAppender");
	props.setProperty("log4j.appender.file.maxFileSize","200MB");
	props.setProperty("log4j.appender.file.maxBackupIndex","200");
	props.setProperty("log4j.appender.file.File","./logs/InvioceContingencia.log");
	props.setProperty("log4j.appender.file.DatePattern","'.'yyyy-MM-dd");
	props.setProperty("log4j.appender.file.threshold","debug");
	props.setProperty("log4j.appender.file.layout","org.apache.log4j.PatternLayout");
	//props.setProperty("log4j.appender.file.layout.ConversionPattern","%d -%m[%t]%-5p%-C[%-4L]%n");
	props.setProperty("log4j.appender.file.layout.ConversionPattern","%d %X{thread-id} [%-5p] %m%n");
	props.setProperty("log4j.appender.stdout","org.apache.log4j.ConsoleAppender");
	//props.setProperty("log4j.appender.stdout.Target"|,"System.out");
	props.setProperty("log4j.logger."+"Thread" + Thread.currentThread().getName(),nivelLog+", file");
	// props.setProperty("log4j.logger.LoadHandler","DEBUG, file");
	PropertyConfigurator.configure(props);
	logContingencia = Logger.getLogger("Thread" + Thread.currentThread().getName());
	logContingencia.debug(">>>>>>>>>>>>>>><<<<<<<<<<<<");
	iniServiceData();
	ListErrorGeneral = new ArrayList<ControlErrores>();
	ListWarnGeneral = new ArrayList<ControlErrores>();
	for (int i=0; i<listErrores.size();i++)
	 	{	ControlErrores ctrl= new ControlErrores(); 
	 		ctrl.setEstado(listErroresEstados.get(i).toString());
	 	    ctrl.setMensaje(listErrores.get(i).toString());
	 	    ctrl.setTipo("E");
		ListErrorGeneral.add(ctrl);
	 	}
	
	ListErrorGeneral=new ArrayList<ControlErrores>();
	for (int i=0; i<listWarning.size();i++)
	 	{	ControlErrores ctrl= new ControlErrores(); 
	 		ctrl.setEstado(listWarningEstados.get(i).toString());
	 	    ctrl.setMensaje(listWarning.get(i).toString());
	 	    ctrl.setTipo("W");
		ListErrorGeneral.add(ctrl);
	 	}
	
		    
	String ruc = args[0];
	logContingencia.info(">>ruc-->"+ruc);
	//String ruc = "0992531940001";
	System.out.println("Ruc::"+ruc);
	if ((ruc == null)||ruc.equals("")||(ruc.length()<13))
	{
		SBmsj.append("Error::"+classReference+":: Debe enviar el parametro de Ruc Correcto. Ruc->"+ruc+". Proceso de Emision de Documentos no se levanto.");
		int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),"","", null);		
		logContingencia.error(">>message_error-->"+SBmsj.toString());
		throw new Exception(SBmsj.toString());		
	}
	
	emite = new Emisor();
	ServiceDataContingencia.databaseType = Util.driverConection;
	logContingencia.info(">>databaseType-->"+ServiceDataContingencia.databaseType);
	InformacionTributaria infTribAdic = new InformacionTributaria();
	InfoEmpresa infEmp = new InfoEmpresa();
	if (!emite.existeEmpresa(ruc)){
    	String mensaje = " Empresa no existe o no se encuentra Activa. Ruc->" +ruc+". Proceso de Emision de Documentos no se levanto.";
    	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
    	logContingencia.error(">>message_error-->"+SBmsj.toString());
    	throw new Exception(mensaje);
    }
	if (ServiceDataContingencia.databaseType.indexOf("postgresql")>0)
		ServiceDataContingencia.databaseType = "PostgreSQL";
	if (ServiceDataContingencia.databaseType.indexOf("sqlserver")>0)
		ServiceDataContingencia.databaseType = "SQLServer";
	logContingencia.info(">>databaseType-->"+ServiceDataContingencia.databaseType);
	infEmp = emite.obtieneInfoEmpresa(ruc);    
    String estado = "", mensaje = "";
    //"NgRm2014"    
	System.out.println("Directorio::"+infEmp.getDirectorio());	
	logContingencia.info(">>Directorio-->"+infEmp.getDirectorio());
	boolean flagFile = false;
	FacCabDocumento CabDoc=null;
	ReporteSentencias rpSen=null;
	numHilo = 1;
	Thread hilo = null;
	contador = 0;
	//Envio de correo para inicio de servicio
	ServiceDataHilo.enviaEmail("message_service_up",emite, "",(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())), null,null);
    while ((Environment.cf.readCtrl().equals("S")))
	{

        try{
        	//contenido = FileDemo.busqueda(infEmp.getDirectorio(),".xml");
        	////////////////////////Testing Enlace SRI//////////////////////////////
        	File fileCont = new File(infEmp.getDirContingencias()+"Testing.xml");       	
	    	if (fileCont.exists()){
	    		logContingencia.debug(">>Delete File Testing-->"+infEmp.getDirContingencias()+"Testing.xml");
	    		fileCont.delete();
	    	}	    	
	    	  ambiente = Environment.c.getString("facElectronica.general.EMISION.ambiente");				        	  
        	  File fileFirmado = ArchivoUtils.stringToArchivo(infEmp.getDirContingencias()+"Testing.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><prueba>jzurita<prueba>");
        	  logContingencia.debug(">>Create File Testing-->"+infEmp.getDirContingencias()+"Testing.xml");
        	  ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();	      	  
      	  	  try{
	      	  		respuestaRecepcion = PruebaEnvioSolicitudRecepcion(fileFirmado, ruc,"01","091454545445",ambiente, ListErrorGeneral, ListWarnGeneral);      	  		
	      	  		logContingencia.debug(">>PruebaEnvioSolicitudRecepcion-->"+respuestaRecepcion);
	      	  		
	      	  }catch(Exception e){
	      		logContingencia.error(">>PruebaEnvioSolicitudRecepcion Error-->"+e.getMessage());
	      		flagServiceDisponible = false;
	      	  }
      	  if (respuestaRecepcion.getEstado()!=null){  
      	  //respuestaRecepcion.setEstado("The server sent HTTP status code 502: Bad Gateway");
        	  if (respuestaRecepcion.getEstado().indexOf("Transaction rolled back")>=0){
        		  System.out.println(emite.toString()+"ESTADO:"+respuestaRecepcion.getEstado());
        		  respuestaRecepcion.setEstado("SIN-RESPUESTA");
        	  }else if (respuestaRecepcion.getEstado().indexOf("Failed to acquire a permit within")>=0){
        		  System.out.println(emite.toString()+"ESTADO:"+respuestaRecepcion.getEstado());
        		  respuestaRecepcion.setEstado("SIN-RESPUESTA");
        	  }else if (respuestaRecepcion.getEstado().indexOf("The server sent HTTP status code 502: Bad Gateway")>=0){
        		  System.out.println(emite.toString()+"ESTADO:"+respuestaRecepcion.getEstado());
        		  respuestaRecepcion.setEstado("REPROCESO");
        	  }else if (respuestaRecepcion.getEstado().toUpperCase().indexOf("TIMEOUT")>=0){
        		  System.out.println(emite.toString()+"ESTADO:"+respuestaRecepcion.getEstado());
        		  respuestaRecepcion.setEstado("SIN-RESPUESTA");
        	  }else if (respuestaRecepcion.getEstado().indexOf("Failed to access the WSDL")>=0){
	        		  System.out.println(emite.toString()+"ESTADO:"+respuestaRecepcion.getEstado());
	        		  respuestaRecepcion.setEstado("SIN-RESPUESTA");
        	  }        	  
        	  
        	  if (respuestaRecepcion.getEstado().equals("DEVUELTA"))
    	  			flagServiceDisponible = true;
      	  }else{
      		flagServiceDisponible = false;
      	  }
      	  	  ////////////////////////Testing Enlace SRI//////////////////////////////
      	  	if (flagServiceDisponible){          	
        	ArrayList<Emisor> Listcontenido = emite.getTrxContingencia(ruc,"CT");
        	logContingencia.error(">>getTrxContingencia-->"+Listcontenido.size()+" Registros");
        	if (Listcontenido !=null){
		    if(Listcontenido.size()>0){
		    	listAtencion = new ArrayList();
		    for (int i=0; i < Listcontenido.size(); i++) {
		    	
		    	logContingencia.debug("---------------------------------------------------------");
		    	logContingencia.debug("Registro a Procesa...");
		    	logContingencia.debug("Ambiente::"+Listcontenido.get(i).getAmbiente());
		    	logContingencia.debug("Ruc::"+ruc);
		    	logContingencia.debug("CodEstablecimiento::"+Listcontenido.get(i).getCodEstablecimiento());
		    	logContingencia.debug("CodPuntoEmision::"+Listcontenido.get(i).getCodPuntoEmision());
		    	logContingencia.debug("CodigoDocumento::"+Listcontenido.get(i).getCodigoDocumento());
		    	logContingencia.debug("Secuencial::"+Listcontenido.get(i).getSecuencial());
		    	logContingencia.debug("Fecha Encolada::"+Listcontenido.get(i).getFechaEncolada());
		    	logContingencia.debug("Ruta::"+Listcontenido.get(i).getRuta());
		    	logContingencia.debug("NameFile::"+Listcontenido.get(i).getNameFile());
		    	
		    	if (contador == numHilo){
		    		//hilo.join();
		    		//new Thread().sleep(4000);
		    		System.gc();
		    		int contEjecutandose = 0;
		    		boolean flagCtrl = true;
		    		contador=0;
		    	}		    	
		    	++contador ;
		    	logContingencia.debug("Xml a Procesar::"+Listcontenido.get(i).getFilexml());
		    	
		    	fileCont = new File(infEmp.getDirContingencias()+Listcontenido.get(i).getFilexml());
		    	if (fileCont.exists()){
		    		fileCont.delete();
		    		logContingencia.debug("Delete Xml::"+infEmp.getDirContingencias()+Listcontenido.get(i).getFilexml());
		    	}
		    	
		    	int li_resultado = emite.UpdateEstadoContingencia("PC",
				    											  Listcontenido.get(i).getAmbiente(), 
				    											  ruc, 
				    											  Listcontenido.get(i).getCodigoDocumento(), 
				    											  Listcontenido.get(i).getCodEstablecimiento(), 
				    											  Listcontenido.get(i).getCodPuntoEmision(), 
				    											  Listcontenido.get(i).getSecuencial());
		    	
		    	logContingencia.debug("UpdateEstadoContingencia::PC");
		    	fileCont = ArchivoUtils.stringToArchivo(infEmp.getDirectorio()+Listcontenido.get(i).getFilexml(), Listcontenido.get(i).getXml());
		    	logContingencia.debug("UpdateEstadoContingencia::PC");
		    	logContingencia.debug("---------------------------------------------------------");
		    }
		    	new Thread().sleep(10000);
		    }else{
				//System.out.println("No Hay archivos que procesar...");
		    	new Thread().sleep(Util.timeWait);
		    }
			}        	
        	}else{
        		new Thread().sleep(50000);
        	}
			}catch(Exception excep){
				excep.printStackTrace();
				//int li_envio = enviaEmail("message_error", emite, "", "Error de Excepcion::"+e.toString() ,"","");
				System.out.println("Pruebas");
			}
    	}
    	//Envio de correo para inicio de servicio
  		ServiceDataHilo.enviaEmail("message_service_down",emite, "",(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())), null,null);
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
		
	public static void delFile(Emisor emite, String rutaFirmado, String generado, String dirNoAutorizados){
		//Eliminacion de Archivos				        		  				        		  
		  File eliminar = new File(rutaFirmado+emite.getFilexml());
  	  if (eliminar.exists()) {
  		  eliminar.delete();
  	  }
  	  File fileDel = new File(generado+emite.getFileTxt());
	  copiarXml2(fileDel.getAbsolutePath(),dirNoAutorizados+fileDel.getName());	
  	  //Eliminacion de Archivos				        		  				        		  
		  eliminar = new File(generado+emite.getFilexml());
  	  if (eliminar.exists()) {
  		  eliminar.delete();
  	  }
  	  
  	  eliminar = new File(generado+emite.getFileXmlBackup());
  	  if (eliminar.exists()) {
  		  eliminar.delete();
  	  }
  	  			            	  
  	  				            	  
  	  if (fileDel.exists()) {
  		  fileDel.delete();
  	  }
  	  System.out.println("Delete File");
	}
	//***********************//////////////////////////////////////////////////////************************************//
		/*									   	Envios al Sri por WebServices											  */
		//***********************//////////////////////////////////////////////////////************************************//	
		public static ec.gob.sri.comprobantes.ws.RespuestaSolicitud PruebaEnvioSolicitudRecepcion(File archivoFirmado, 
																								   String ruc,
																								   String codigoDoc,
																								   String claveAcceso,
																								   //String Urls,
																								   String ambiente,
																								   ArrayList<ControlErrores> ListErrorGeneral, 
																								   ArrayList<ControlErrores> ListWarnGeneral) throws Exception
		{
			ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = null;
			try{		
			//String flagErrores = "";	
				respuestaRecepcion = new ec.gob.sri.comprobantes.ws.RespuestaSolicitud();
		    	respuestaRecepcion = EnvioComprobantesWs.obtenerRespuestaEnvio(archivoFirmado, 
		    																   ruc, 
		    																   codigoDoc, 
		    																   claveAcceso, 
		    																   FormGenerales.devuelveUrlWs(new Integer(ambiente).toString() ,"RecepcionComprobantes"),
		    																   Util.timeOutRecepComp);
			}catch(Exception exc){
				respuestaRecepcion.setEstado(exc.getMessage());  
				throw new Exception(respuestaRecepcion.getEstado());
		    }
			return respuestaRecepcion;		
		}	
	
	public static int copiarXml(String fileName){
		String fileBackup = null;
		try{
		  File fileOrigen = new File(fileName);
	      File fileDestino = new File(fileName.replace(".xml", "_backup.xml"));	      
	      if (fileOrigen.exists()) {	    	  
	    	  InputStream in = new FileInputStream(fileOrigen);
	    	  OutputStream out = new FileOutputStream(fileDestino);
	    	  byte[] buf = new byte[1024];int len; while ((len = in.read(buf)) > 0) {  out.write(buf, 0, len);}
	    	  in.close();
	    	  out.close();
	    	  fileBackup = fileName.replace(".xml", "_backup.xml");
	    	  return 1;
	    	  
	      }
	      else{
	    	  return 0;
	      }
		}catch(IOException e){
			return -1;
		}
	}
	
	
	public static int copiarXml2(String fileNameOrigen, String fileNameDestino){
		try{
		  File fileOrigen = new File(fileNameOrigen);
		  
	      File fileDestino = new File(fileNameDestino);	      
	      if (fileOrigen.exists()) {	    	  
	    	  InputStream in = new FileInputStream(fileOrigen);
	    	  OutputStream out = new FileOutputStream(fileDestino);
	    	  byte[] buf = new byte[1024];int len; while ((len = in.read(buf)) > 0) {  out.write(buf, 0, len);}
	    	  in.close();
	    	  out.close();
	    	  return 1;
	    	  
	      }
	      else{
	    	  return 0;
	      }
		}catch(IOException e){
			return -1;
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
	
	
	
	
	public static int enviaEmailCliente(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf, String emailCliente){
		String emailHost = null;
		String emailFrom = null;
		String emailTo = null;
		String emailSubject = null;
		String emailMensaje = null;	
		String emailHelpDesk = null;
		String resultEnvioMail = "";
		//Host Mail Server
				emailHost = Environment.c.getString("facElectronica.alarm.email.host");
				//Enviado desde
				emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
				//Enviado para
				emailTo = Environment.c.getString("facElectronica.alarm.email.receivers-list");
				//Asunto
				emailSubject = Environment.c.getString("facElectronica.alarm.email.subject");
				//Email HelpDesk
				emailHelpDesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
				
		EmailSender emSend = new EmailSender(emailHost,emailFrom);		
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);		
		String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		
		String user = Environment.c.getString("facElectronica.alarm.email.user");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		String receivers = "";		
		if (ambiente.equals("PRUEBAS")){
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");
		}else{
			String emailCli = emi.getInfEmisor().getMailCliente();
			if (!emailCli.equals("email@email.com")){
				  receivers = emailCliente;
			}else{
				receivers =null;			}
		}	
		if (receivers!=null){
		emSend.setPassword(clave);
		emSend.setSubject(subject);
		emSend.setUser(user);		
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);		
		emailCliente = receivers;
		String noDocumento = "";		
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			System.out.println("Envio de Email");
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
		if ((emailCliente!=null) && (emailCliente.length()>0)){
			String[] partsMail = emailCliente.split(";");
			//for(int i=0;i<partsMail.length;i++)
				//if (partsMail[i].length()>0){
			resultEnvioMail = emSend.send(emailCliente
							//partsMail[i]
						, 
							subject, 
		  		  	        emailMensaje,
		  		  	        fileAttachXml,
		  		  	        fileAttachPdf);
				//}
		}
		}
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else
			System.out.println("Error de Envio de Mail::"+resultEnvioMail);
			return -1;
	}	
	
	public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf){
		String emailHost = null;
		String emailFrom = null;
		String emailTo = null;
		String emailSubject = null;
		String emailMensaje = null;	
		String emailHelpDesk = null;
		//Host Mail Server
				emailHost = Environment.c.getString("facElectronica.alarm.email.host");
				//Enviado desde
				emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
				//Enviado para
				emailTo = Environment.c.getString("facElectronica.alarm.email.receivers-list");
				//Asunto
				emailSubject = Environment.c.getString("facElectronica.alarm.email.subject");
				//Email HelpDesk
				emailHelpDesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		EmailSender emSend = new EmailSender(emailHost,emailFrom);
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);
		
		String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String receivers = "";
		String user = Environment.c.getString("facElectronica.alarm.email.user");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		
		
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");				
		emSend.setPassword(clave);
		emSend.setSubject(subject);
		emSend.setUser(user);
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);
		emailTo = receivers;
		
		String noDocumento = "";
		if (emi != null){
		if (emi.getInfEmisor() != null){
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			System.out.println("Envio de Email");
			noDocumento = emi.getInfEmisor().getCodEstablecimiento()+emi.getInfEmisor().getCodPuntoEmision()+emi.getInfEmisor().getSecuencial();
		}		
		}
		
		emailMensaje = emailMensaje.replace("|FECHA|", (emi.getInfEmisor().getFecEmision()==null?"":emi.getInfEmisor().getFecEmision().toString()));
		emailMensaje = emailMensaje.replace("|NODOCUMENTO|", (noDocumento==null?"":noDocumento));	
		emailMensaje = emailMensaje.replace("|HELPDESK|", emailHelpDesk);
		emailMensaje = StringEscapeUtils.unescapeHtml(emailMensaje);
		}
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
			String[] partsMail = emailTo.split(";");
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
	
	
	public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error){
		String emailHost = null;
		String emailFrom = null;
		String emailTo = null;
		String emailSubject = null;
		String emailMensaje = null;	
		String emailHelpDesk = null;
		String resultEnvioMail = "";
		//Host Mail Server
				emailHost = Environment.c.getString("facElectronica.alarm.email.host");
				//Enviado desde
				emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
				//Enviado para
				emailTo = Environment.c.getString("facElectronica.alarm.email.receivers-list");
				//Asunto
				emailSubject = Environment.c.getString("facElectronica.alarm.email.subject");
				//Email HelpDesk
				emailHelpDesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		EmailSender emSend = new EmailSender(emailHost,emailFrom);
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);
		String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		
		String receivers = "";
		
		receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");				
		emSend.setPassword(clave);
		emSend.setSubject(subject);
		emSend.setUser(emailFrom);
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);
		emailTo = receivers;
		String noDocumento = "";		
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			System.out.println("Envio de Email");
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
			String[] partsMail = emailTo.split(";");
			for(int i=0;i<partsMail.length;i++)
				if (partsMail[i].length()>0){
					resultEnvioMail =emSend.send(partsMail[i], 
								subject, 
			  		  	        emailMensaje);
				}
		}	
		
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else
			System.out.println("Error de Envio de Mail::"+resultEnvioMail);
			return -1;
	}
	

}
