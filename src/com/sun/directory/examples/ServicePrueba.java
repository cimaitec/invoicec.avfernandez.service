package com.sun.directory.examples;

import java.io.File;

import com.sun.DAO.InformacionTributaria;
import com.sun.businessLogic.validate.Emisor;
import com.tradise.reportes.entidades.FacCabDocumento;
import com.tradise.reportes.servicios.ReporteSentencias;
import com.util.util.key.Environment;
import com.util.util.key.Util;

public class ServicePrueba extends com.util.util.key.GenericTransaction {

	public static String classReference = "ServiceData";
	//public static String id = "1.0";
	public static StringBuilder SBmsj = null;
	public static File fxml = null;
	public static String fileBackup = null;
	
	public static String emailHost = null;
	public static String emailFrom = null;
	public static String emailTo = null;
	public static String emailSubject = null;
	public static String emailMensaje = null;	
	public static String emailHelpDesk = null;
	public static File[] contenido;
	
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
		//setLogger();
		
		SBmsj = new StringBuilder();
		
		//Archivo de Configuracion
			String name_xml="facturacion.xml";    
			    try{
			    	Environment.setConfiguration(name_xml);
					Environment.setCtrlFile();
					Environment.setLogger(Util.log_control);
					ServiceData.iniServiceData();
				  }catch(Exception ex){
					//System.out.println(classReference+"::main>>FacturacionElectronica.WebService::main::Proceso de Carga de Archivo Xml Configuraciones::::");
					  SBmsj.append(classReference+"::main>>FacturacionElectronica.Service::main::Proceso de Carga de Archivo Xml Configuraciones::::"+". Proceso de Emision de Documentos no se levanto.");
					  //int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),"",null, null);
					  //log.error(SBmsj.toString());
					  throw new Exception(SBmsj.toString());
		    }	
		
		
	    //System.out.println("Length::"+args.length);	
	    /*if (args.length==0){
	    	SBmsj.append("Error::"+classReference+"::Cantidad de Parametros Necesarias 1::Cantidad de Parametros ->"+args.length+". Proceso de Emision de Documentos no se levanto.");
	    	int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),"","", null);
	    	//log.error(SBmsj.toString());
			throw new Exception(SBmsj.toString());
		}
		*/
		String ruc = args[0];
		//String ruc = "0992531940001";
		System.out.println("Ruc::"+ruc);
		if ((ruc == null)||ruc.equals("")||(ruc.length()<13))
		{
			SBmsj.append("Error::"+classReference+":: Debe enviar el parametro de Ruc Correcto. Ruc->"+ruc+". Proceso de Emision de Documentos no se levanto.");
			//int li_envio = enviaEmail("message_error", emite, SBmsj.toString(),"","", null);		
			//log.error(SBmsj.toString());
			throw new Exception(SBmsj.toString());		
		}
		
		   
		
		emite = new Emisor();
		
		InformacionTributaria infTribAdic = new InformacionTributaria();
		
		if (!emite.existeEmpresa(ruc)){
	    	String mensaje = " Empresa no existe o no se encuentra Activa. Ruc->" +ruc+". Proceso de Emision de Documentos no se levanto.";
	    	//int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
	    	throw new Exception(mensaje);
	    }
			
	    infTribAdic = emite.obtieneInfoTributaria(ruc);
	    //Directorio es un archivo del sistema operativo
	    emailTo=infTribAdic.getMailEmpresa();
	    String directorio = infTribAdic.get_pathGenerados();
	    String generado = infTribAdic.get_pathGenerados();       
	    String recibidos = infTribAdic.get_pathInfoRecibida();
	    String rutaFirmado = infTribAdic.get_pathFirmados();
	    String dirAutorizados = infTribAdic.get_pathAutorizados();
	    String dirNoAutorizados = infTribAdic.get_pathNoAutorizados();
	    String dirContingencias = infTribAdic.get_PathCompContingencia();    
	    String dirFirmados = infTribAdic.get_pathFirmados();
	    String rucFirmante = infTribAdic.getRucFirmante();
	    
	    //C:\DataExpress\DMIRO\generados
	    
	    /*
	    String base = "C://DataExpress//DMIRO//";
	    String directorio = base+"generados//";
	    String generado = base+"generados//";       
	    String recibidos = base+"recibidos//";
	    String rutaFirmado = base+"firmados//";
	    String dirAutorizados = base+"autorizados//";
	    String dirNoAutorizados = base+"noautorizados//";
	    String dirContingencias = base+"firmados//";
	    String dirFirmados = base+"firmados//";
	   
	    String rutaFirma= "/usr/facElectronica/OroVerde/Guayaquil/firma/nelson_gabriel_rodriguez_martinez.p12";
	    String claveFirma= "NgRm2014";
	    String tipoFirma = "BCE_IKEY2032";
	    String pathReports = base+"generales//Jaspers//";
	    String pathXsd = base+"generales//XSD//";
	    */
	    String mailEmpresa = infTribAdic.getMailEmpresa();    
	    String rutaFirma= infTribAdic.get_PathFirma();
	    String claveFirma= infTribAdic.get_ClaveFirma();
	    String tipoFirma = infTribAdic.get_TipoFirma();
	    String pathReports = infTribAdic.get_pathJasper();
	    String pathXsd = infTribAdic.get_pathXsd();    
	    //"NgRm2014"    
		System.out.println("Directorio::"+directorio);	
		boolean flagFile = false;
		FacCabDocumento CabDoc=null;
		ReporteSentencias rpSen=null;
	    while ((Environment.cf.readCtrl().equals("S"))){
	        try{
	        	contenido = FileDemo.busqueda(directorio,".xml");
			    if(contenido.length>0){
					for (int i=0; i < contenido.length; i++) {
						  //System.out.println(contenido[i].getAbsolutePath());
						  String respuestaFirma = "";
						  String FileName = contenido[i].getName().replace(".xml", "");
						  System.out.println("FileName::"+FileName);
					}
					System.out.println("FileName::No file");
			    }	
	        }catch(Exception excep){
				excep.printStackTrace();
				//int li_envio = enviaEmail("message_error", emite, "", "Error de Excepcion::"+e.toString() ,"","");
				System.out.println("Pruebas");
			}
    	}
	}
}	