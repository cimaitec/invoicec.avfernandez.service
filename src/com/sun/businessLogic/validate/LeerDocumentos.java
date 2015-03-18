package com.sun.businessLogic.validate;

import com.sun.format.FormatoLine;
import com.sun.format.Formatos;
//import com.cimait.util.key.ArchivoUtils;
import com.util.webServices.EnvioComprobantesWs;
import com.util.util.key.Environment;
import com.util.util.key.Util;
import com.sun.comprobantes.util.EmailSender;
//iimport com.sun.Dao.Destinatarios;
import com.sun.DAO.Destinatarios;
import com.sun.DAO.DetalleDocumento;
import com.sun.DAO.DetalleTotalImpuestos;
import com.sun.DAO.DetalleTotalImpuestosRetenciones;
import com.sun.DAO.DetallesAdicionales;
import com.sun.DAO.DocumentoImpuestos;
import com.sun.DAO.InformacionAdicional;
import com.sun.DAO.InformacionTributaria;
import com.util.util.key.Environment;
import com.util.util.key.Util;
import com.util.webServices.EnvioComprobantesWs;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import java.io.PrintStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LeerDocumentos extends com.util.util.key.GenericTransaction{ 
  private static StringTokenizer tokensLine;
  private static Hashtable<String, FormatoLine> ConfigParam = new Hashtable();
  private static ArrayList<Formatos> ListFormat;
  private PrivateKey privateKey;
  private Provider provider;
  private static SimpleDateFormat formatFecha;  
  public static ArrayList ListDetalleDocumento = new ArrayList();    
  public static ArrayList ListDocumentoImpuestos = new ArrayList();
  public static ArrayList ListDocumentoDetAdicionales = new ArrayList();
  public static ArrayList ListInformacionMotivo = new ArrayList();
  public static ArrayList ListDetTotalImpRetenciones = new ArrayList();
  public static ArrayList ListDetTotalImp = new ArrayList();
  public static ArrayList ListInformacionAdicional = new ArrayList();
  public static ArrayList ListDestinatarios = new ArrayList();
  
  public static String classReference = "LeerDocumentos";
  
	public static String emailHost = null;
	public static String emailFrom = null;
	public static String emailTo = null;
	public static String emailSubject = null;
	public static String emailMensaje = null;	
	public static String emailHelpDesk = null;  
  
  public static int procesarDatosWebServicesLines(String datos, String separadorLine)
  {
    int li_result = 0;
    StringBuilder strb = new StringBuilder();
    Formatos form = null;
    ListFormat = new ArrayList();
    if ((separadorLine == null) || (separadorLine.length() <= 0)) {
      separadorLine = "\n";
    }
    int i = 0;
    tokensLine = new StringTokenizer(datos, separadorLine);
    while (tokensLine.hasMoreTokens()) {
      String dat = tokensLine.nextToken();
      System.out.println("dat:" + dat);
      strb = new StringBuilder(dat);
      i++;
      System.out.println("Size:" + strb);
      form = new Formatos(strb.substring(0, 2), i, strb.toString());
      ListFormat.add(form);
    }
    return li_result;
  }

  public static Emisor procesarDatosWebServicesFiles(String filepath, String separadorLine, String tipo_documento, Emisor e) throws Exception
  {
    int li_result = 0;
    int i = 0;
    int idxtoken = 0;
    int ln_count_det = 1;
    ListDetalleDocumento.clear();    
    ListDocumentoImpuestos.clear();
    ListDocumentoDetAdicionales.clear();
    ListInformacionMotivo.clear();
    ListDestinatarios.clear();
    
    File f = new File(filepath);
    BufferedReader entrada = null, entrada2=null;
    InformacionTributaria infTrib = new InformacionTributaria();
    Emisor emite = e;
    SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
    Date fecha = null;
    
    String[] matrizLineaCont;
    String idLineaCont = "";
    String[] matrizLinea = null;
    String idLinea = "";
    int contLine = 0;
    int contLineDE = 0;
    int contLineDA = 0;
    int flagLinea = 0;
    
    String linea = "";
    try{    	
    	entrada2 = new BufferedReader(new FileReader(f));
	    while (entrada2.ready()) {    	
	    	try{
	        	linea = entrada2.readLine();
	        	matrizLineaCont = splitTotokens(linea, separadorLine);
	        	idLineaCont = matrizLineaCont[0];
	    	}catch(Exception excep){
	    		flagLinea = 1; 
	    	}
	        if (flagLinea==0){               
		        if (idLineaCont.equals("VE")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("IT")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("IC")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("T")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("TIR")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("TI")){
		        	contLine++;
		        }
		        if (idLineaCont.equals("DE")){
		        	contLine++;
		        	contLineDE++;
		        }
		        if (idLineaCont.equals("IM")){
		        	contLine++;
		        } 
		        if (idLineaCont.equals("DA")){
		        	contLine++;
		        } 
		        if (idLineaCont.equals("IA")){
		        	contLine++;
		        } 
		        if (idLineaCont.equals("MO")){
		        	contLine++;
		        } 
		        if (idLineaCont.equals("DEST")){
		        	contLine++;
		        }
	        }
	    }
	    System.out.println("Cantidad de Lineas-" + contLine);
	    if (entrada2 != null)
	        entrada2.close();
    }catch (Exception exfile) {
    	li_result = 4;
    	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+exfile.getMessage()+"|"+exfile.getCause());
    }
    entrada = new BufferedReader(new FileReader(f));
    ValidateField validformat = new ValidateField();
    String LineaAnt = "";
    DetalleDocumento detDocumentImp = new DetalleDocumento();
	DocumentoImpuestos DocImp = new DocumentoImpuestos();
	DetallesAdicionales DetAdic = new DetallesAdicionales();    
    i = 0;
    flagLinea=0;
    while (entrada.ready()) {
      i++;
      try{
      	linea = entrada.readLine();
      	System.out.println(i + "-" + linea);
        idxtoken = 0;
        matrizLinea = splitTotokens(linea, separadorLine);
        idLinea = matrizLinea[0];
	  }catch(Exception excp){
	  		flagLinea = 1; 
	  }
      if (flagLinea==0){            
      
      System.out.println("idLinea:" + idLinea);
      
      //VE Version
      if (idLinea.equals("VE"))
      {
    	
    	  Environment.c.getString("facElectronica.formatos.documentos.fields."+idLinea.toUpperCase()+".tipoComprobante.");
        validformat = setCampoFormato(matrizLinea, 1, "tipoComprobante", "String", "", 100, "N");
        emite.getInfEmisor().setTipoComprobante(validformat.getValueString());
        System.out.println("tipoComprobante::" + emite.getInfEmisor().getTipoComprobante());

        validformat = setCampoFormato(matrizLinea, 2, "idComprobante", "String", "", 20, "N");
        emite.getInfEmisor().setIdComprobante(validformat.getValueString());
        System.out.println("idComprobante::" + emite.getInfEmisor().getIdComprobante());

        validformat = setCampoFormato(matrizLinea, 3, "Version", "String", "", 20, "S", "1.0.0");
        emite.getInfEmisor().setVersion(validformat.getValueString());
        System.out.println("Version::" + emite.getInfEmisor().getVersion());
      }
      //IT Información Tributaria.
      if (idLinea.equals("IT"))
      {
        validformat = setCampoFormato(matrizLinea, 1, "Ambiente", "Int", "", 0, "S", "1");
        emite.getInfEmisor().setAmbiente(validformat.getValueInt());
        System.out.println("Ambiente::" + emite.getInfEmisor().getAmbiente());

        validformat = setCampoFormato(matrizLinea, 2, "tipoEmision", "Int", "", 0, "S", "1");
        emite.getInfEmisor().setTipoEmision(validformat.getValueString());
        System.out.println("tipoEmision::" + emite.getInfEmisor().getTipoEmision());

        validformat = setCampoFormato(matrizLinea, 3, "RazonSocial", "String", "", 300, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;
      	  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setRazonSocial(validformat.getValueString());
        System.out.println("RazonSocial::" + emite.getInfEmisor().getRazonSocial());

        validformat = setCampoFormato(matrizLinea, 4, "NombreComercial", "String", "", 300, "N");
        emite.getInfEmisor().setNombreComercial(validformat.getValueString());
        System.out.println("NombreComercial::" + emite.getInfEmisor().getNombreComercial());

        validformat = setCampoFormato(matrizLinea, 5, "Ruc", "String", "", 13, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setRuc(validformat.getValueString());
        System.out.println("Ruc::" + emite.getInfEmisor().getRuc());

        validformat = setCampoFormato(matrizLinea, 6, "claveAcceso", "String", "", 49, "N");
        emite.getInfEmisor().setClaveAcceso(validformat.getValueString());
        System.out.println("claveAcceso::" + emite.getInfEmisor().getClaveAcceso());

        validformat = setCampoFormato(matrizLinea, 7, "CodDocumento", "String", "", 2, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setCodDocumento(validformat.getValueString());
        System.out.println("CodDocumento::" + emite.getInfEmisor().getCodDocumento());

        validformat = setCampoFormato(matrizLinea, 8, "CodEstablecimiento", "String", "", 3, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setCodEstablecimiento(validformat.getValueString());
        System.out.println("CodEstablecimiento::" + emite.getInfEmisor().getCodEstablecimiento());

        validformat = setCampoFormato(matrizLinea, 9, "CodPuntoEmision", "String", "", 3, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setCodPuntoEmision(validformat.getValueString());
        System.out.println("CodPuntoEmision::" + emite.getInfEmisor().getCodPuntoEmision());

        validformat = setCampoFormato(matrizLinea, 10, "Secuencial", "String", "", 9, "N");
        int numero = Integer.parseInt(validformat.getValueString());
        Formatter fmt = new Formatter();
		fmt.format("%09d",numero);		
        emite.getInfEmisor().setSecuencial(fmt.toString());
        System.out.println("Secuencial::" + emite.getInfEmisor().getSecuencial());

        validformat = setCampoFormato(matrizLinea, 11, "DirMatriz", "String", "", 300, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setDireccionMatriz(validformat.getValueString());
        System.out.println("DirMatriz::" + emite.getInfEmisor().getDireccionMatriz());

        validformat = setCampoFormato(matrizLinea, 12, "Mail", "String", "", 300, "N");
        emite.getInfEmisor().setMailEmpresa(validformat.getValueString());
        System.out.println("Mail::" + emite.getInfEmisor().getMailEmpresa());
      }     

      //IC Información del Comprobante.
      if (idLinea.equals("IC"))
      {
        validformat = setCampoFormato(matrizLinea, 1, "fecEmision", "Date", "dd/MM/yyyy", 0, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        System.out.println("validformat.getValueDate()::"+validformat.getValueDate());
        emite.getInfEmisor().setFecEmision(validformat.getValueString());
        System.out.println("fecEmision::" + emite.getInfEmisor().getFecEmision());

        validformat = setCampoFormato(matrizLinea, 2, "direccionEstablecimiento", "String", "", 300, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setDireccionEstablecimiento(validformat.getValueString());
        System.out.println("direccionEstablecimiento::" + emite.getInfEmisor().getDireccionEstablecimiento());

        validformat = setCampoFormato(matrizLinea, 3, "contribEspecial", "String", "", 5, "N");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setContribEspecial(validformat.getValueInt());
        System.out.println("contribEspecial::" + emite.getInfEmisor().getContribEspecial());

        validformat = setCampoFormato(matrizLinea, 4, "obligContabilidad", "String", "", 2, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setObligContabilidad(validformat.getValueString());
        System.out.println("obligContabilidad::" + emite.getInfEmisor().getObligContabilidad());

        validformat = setCampoFormato(matrizLinea, 5, "tipoIdentificacion", "String", "", 2, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }else{
        	emite.getInfEmisor().setTipoIdentificacion(validformat.getValueString());
        	System.out.println("tipoIdentificacion::" + emite.getInfEmisor().getTipoIdentificacion());
        }

        validformat = setCampoFormato(matrizLinea, 6, "guiaRemision", "String", "", 17, "N");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setGuiaRemision(validformat.getValueString());
        System.out.println("guiaRemision::" + emite.getInfEmisor().getGuiaRemision());

        validformat = setCampoFormato(matrizLinea, 7, "razonSocialComp", "String", "", 300, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setRazonSocialComp(validformat.getValueString());
        System.out.println("razonSocialComp::" + emite.getInfEmisor().getRazonSocialComp());

        validformat = setCampoFormato(matrizLinea, 8, "identificacionComp", "String", "", 13, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setIdentificacionComp(validformat.getValueString());
        System.out.println("identificacionComp::" + emite.getInfEmisor().getIdentificacionComp());

        validformat = setCampoFormato(matrizLinea, 9, "moneda", "String", "", 15, "S");
        if (validformat.getLi_error() == 1) {
        	li_result = 2;
        	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setMoneda(validformat.getValueString());
        System.out.println("moneda::" + emite.getInfEmisor().getMoneda());

        if ((tipo_documento.equals("04")) || (tipo_documento.equals("05")) || (tipo_documento.equals("06")))
        {
          validformat = setCampoFormato(matrizLinea, 10, "rise", "String", "", 40, "N");
          if (validformat.getLi_error() == 1) {
        	  li_result = 2;
          	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
          }
          emite.getInfEmisor().setRise(validformat.getValueString());
          System.out.println("rise::" + emite.getInfEmisor().getRise());
        }

        if ((tipo_documento.equals("04")) || (tipo_documento.equals("05"))){
        validformat = setCampoFormato(matrizLinea, 11, "codDocModificado", "String", "", 2, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }        
        emite.getInfEmisor().setCodDocModificado(validformat.getValueString());
        System.out.println("codDocModificado::" + emite.getInfEmisor().getCodDocModificado());
        }
        
        if ((tipo_documento.equals("04")) || (tipo_documento.equals("05"))){
	        //Ojo Informar a Isabel
	        validformat = setCampoFormato(matrizLinea, 12, "numDocModificado", "String", "", 17, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }
	        emite.getInfEmisor().setNumDocModificado(validformat.getValueString());
	        System.out.println("numDocModificado::" + emite.getInfEmisor().getNumDocModificado());
        }
        
        if ((tipo_documento.equals("04")) || (tipo_documento.equals("05"))){
        validformat = setCampoFormato(matrizLinea, 13, "fechaEmisionDocSustento", "Date", "dd/MM/yyyy", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setFecEmisionDoc(validformat.getValueString());
        System.out.println("fechaEmisionDocSustento::" + emite.getInfEmisor().getFecEmisionDoc());        
        }

        validformat = setCampoFormato(matrizLinea, 14, "valorModificado", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setValorModificado(validformat.getValueDouble());
        System.out.println("valorModificado::" + emite.getInfEmisor().getValorModificado());

        if (tipo_documento.equals("04"))
        {
	        validformat = setCampoFormato(matrizLinea, 15, "motivo", "String", "", 300, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }
        }
                
        emite.getInfEmisor().setMotivo(validformat.getValueString());
        System.out.println("motivo::" + emite.getInfEmisor().getMotivo());

        if (tipo_documento.equals("07")){        
        validformat = setCampoFormato(matrizLinea, 15, "motivo", "String", "", 300, "S");
        validformat = setCampoFormato(matrizLinea, 16, "periodoFiscal", "Date", "MM/yyyy", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        //Comentariado por ser OroVerde
        /*
        emite.getInfEmisor().setPeriodoFiscal(validformat.getValueDate());
        */
        System.out.println("periodoFiscal::" + emite.getInfEmisor().getPeriodoFiscal());        
        }        
        if (tipo_documento.equals("06")){
	        validformat = setCampoFormato(matrizLinea, 17, "DirPartida", "String", "", 300, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }
	        emite.getInfEmisor().setDirPartida(validformat.getValueString());
	        System.out.println("DirPartida::" + emite.getInfEmisor().getDirPartida());
        }
        if (tipo_documento.equals("06")){
        validformat = setCampoFormato(matrizLinea, 18, "razonSocialTransportista", "String", "", 300, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        }
        emite.getInfEmisor().setRazonSocTransp(validformat.getValueString());
        System.out.println("razonSocialTransportista::" + emite.getInfEmisor().getRazonSocTransp());

        if (tipo_documento.equals("06")){
        validformat = setCampoFormato(matrizLinea, 19, "tipoIdentificacionTransportista", "Int", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setTipoIdentTransp(validformat.getValueInt());
        System.out.println("tipoIdentificacionTransportista::" + emite.getInfEmisor().getTipoIdentTransp());
        }
        if (tipo_documento.equals("06")){
        validformat = setCampoFormato(matrizLinea, 20, "rucTransportista", "String", "", 13, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setRucTransp(validformat.getValueString());
        System.out.println("rucTransportista::" + emite.getInfEmisor().getRucTransp());
        }
        validformat = setCampoFormato(matrizLinea, 21, "aux1", "String", "", 300, "N");
        emite.getInfEmisor().setAux1(validformat.getValueString());
        System.out.println("aux1::" + emite.getInfEmisor().getAux1());

        validformat = setCampoFormato(matrizLinea, 22, "aux2", "String", "", 300, "N");
        emite.getInfEmisor().setAux2(validformat.getValueString());
        System.out.println("aux2::" + emite.getInfEmisor().getAux2());
        
        if (tipo_documento.equals("06")){        
        validformat = setCampoFormato(matrizLinea, 23, "fechaInicioTransporte", "Date", "dd/MM/yyyy", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setFechaIniTransp(validformat.getValueString());
        System.out.println("fechaInicioTransporte::" + emite.getInfEmisor().getFechaIniTransp());

        validformat = setCampoFormato(matrizLinea, 24, "fechaFinTransporte", "Date", "dd/MM/yyyy", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setFechaFinTransp(validformat.getValueString());
        System.out.println("fechaFinTransporte::" + emite.getInfEmisor().getFechaFinTransp());
        
        validformat = setCampoFormato(matrizLinea, 24, "placa", "String", "", 20, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setPlaca(validformat.getValueString());
        System.out.println("placa::" + emite.getInfEmisor().getPlaca());
        }
      }
      //T Totales del Comprobante.
      if (idLinea.equals("T"))
      {
        validformat = setCampoFormato(matrizLinea, 1, "subTotal12", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setSubTotal12(validformat.getValueDouble());
        System.out.println("subTotal12::" + emite.getInfEmisor().getSubTotal12());

        validformat = setCampoFormato(matrizLinea, 2, "SubTotal0", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setSubTotal0(validformat.getValueDouble());
        System.out.println("SubTotal0::" + emite.getInfEmisor().getSubTotal0());

        validformat = setCampoFormato(matrizLinea, 3, "subTotalNoSujeto", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setSubTotalNoSujeto(validformat.getValueDouble());
        System.out.println("subTotalNoSujeto::" + emite.getInfEmisor().getSubTotalNoSujeto());

        validformat = setCampoFormato(matrizLinea, 4, "totalSinImpuestos", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setTotalSinImpuestos(validformat.getValueDouble());
        System.out.println("totalSinImpuestos::" + emite.getInfEmisor().getTotalSinImpuestos());

        validformat = setCampoFormato(matrizLinea, 5, "totalDescuento", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setTotalDescuento(validformat.getValueDouble());
        System.out.println("totalDescuento::" + emite.getInfEmisor().getTotalDescuento());

        validformat = setCampoFormato(matrizLinea, 6, "totalICE", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setTotalICE(validformat.getValueDouble());
        System.out.println("totalICE::" + emite.getInfEmisor().getTotalICE());

        validformat = setCampoFormato(matrizLinea, 7, "totalIva12", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setTotalIva12(validformat.getValueDouble());
        System.out.println("totalIva12::" + emite.getInfEmisor().getTotalIva12());

        validformat = setCampoFormato(matrizLinea, 8, "importeTotal", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setImporteTotal(validformat.getValueDouble());
        System.out.println("importeTotal::" + emite.getInfEmisor().getImporteTotal());

        validformat = setCampoFormato(matrizLinea, 9, "propina", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setPropina(validformat.getValueDouble());
        System.out.println("propina::" + emite.getInfEmisor().getPropina());

        validformat = setCampoFormato(matrizLinea, 10, "importePagar", "Double", "", 0, "S");
        if (validformat.getLi_error() == 1) {
          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
        }
        emite.getInfEmisor().setImportePagar(validformat.getValueDouble());
        System.out.println("importePagar::" + emite.getInfEmisor().getImportePagar());
      }
      //JZURITA
	      //TIR Total de  Impuestos Retenidos. 
	      if (idLinea.equals("TIR"))
	      {
	        //Comprobante de Retencion    	  
	    	if (emite.getInfEmisor().getTipoComprobante().equals("07")){    		    	    		
	    		DetalleTotalImpuestosRetenciones totImpRetTmp = new DetalleTotalImpuestosRetenciones();    		
		        validformat = setCampoFormato(matrizLinea, 1, "codImpRetenidos", "Int", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setCodigo(validformat.getValueInt());
		        	System.out.println("codImpRetenidos::" + totImpRetTmp.getCodigo());
		        }	        
		
		        validformat = setCampoFormato(matrizLinea, 2, "codRetencion", "String", "", 5, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setCodigoRetencion(validformat.getValueString());
		        	//emite.getInfEmisor().setCodRetencion(validformat.getValueString());
			        System.out.println("codRetencion::" + totImpRetTmp.getCodigoRetencion());	        	
		        }

		        validformat = setCampoFormato(matrizLinea, 3, "baseImponible", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setBaseImponible(validformat.getValueDouble());
			        //emite.getInfEmisor().setBaseImponible(validformat.getValueDouble());
			        System.out.println("baseImponible::" +  totImpRetTmp.getBaseImponible());
		        }	        
		        
		        validformat = setCampoFormato(matrizLinea, 4, "porcRetenido", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setPorcentajeRetener(validformat.getValueDouble());
		        	//emite.getInfEmisor().setPorcRetenido(validformat.getValueDouble());
			        System.out.println("porcRetenido::" + totImpRetTmp.getPorcentajeRetener());
		        }
		        
		
		        validformat = setCampoFormato(matrizLinea, 5, "valorRetenido", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setValorRetenido(validformat.getValueDouble());
			        //emite.getInfEmisor().setValorRetenido(validformat.getValueDouble());
			        System.out.println("valorRetenido::" + totImpRetTmp.getValorRetenido());
		        }
		        if ((tipo_documento.equals("06"))||(tipo_documento.equals("07"))){
		        validformat = setCampoFormato(matrizLinea, 6, "codDocSustento", "Int", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setCodDocSustento(validformat.getValueInt());
		        	//emite.getInfEmisor().setCodDocSustento(validformat.getValueInt());
			        System.out.println("codDocSustento::" + totImpRetTmp.getCodDocSustento());
		        }
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 7, "numDocSustento", "Long", "", 0, "N");
		        //emite.getInfEmisor().setNumDocSustento(validformat.getValueLong());
		        totImpRetTmp.setNumDocSustento(validformat.getValueLong());
		        System.out.println("numDocSustento::"+totImpRetTmp.getNumDocSustento());
		        
		        validformat = setCampoFormato(matrizLinea, 8, "fechaEmisionDocSustento", "Date", "dd/MM/yyyy", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	totImpRetTmp.setFechaEmisionDocSustento(validformat.getValueDate());
			        System.out.println("fechaEmisionDocSustento::"+totImpRetTmp.getFechaEmisionDocSustento());
		        }
		        ListDetTotalImpRetenciones.add(totImpRetTmp);	        
		        emite.getInfEmisor().setListDetDetImpuestosRetenciones(ListDetTotalImpRetenciones);	        
	    	}
	      }

	      //TI  Totales de Impuestos.
	      if (idLinea.equals("TI"))
	      {
	        DetalleTotalImpuestos totImpTmp = new DetalleTotalImpuestos();
	
	        validformat = setCampoFormato(matrizLinea, 1, "codTotalImpuestos", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		    }else{
		    		totImpTmp.setCodTotalImpuestos(validformat.getValueInt());
			        System.out.println("codTotalImpuestos::"+totImpTmp.getCodTotalImpuestos());
		    }
	        
	        validformat = setCampoFormato(matrizLinea, 2, "codPorcentImp", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		    }else{
		    		totImpTmp.setCodPorcentImp(validformat.getValueInt());
			        System.out.println("codPorcentImp::"+totImpTmp.getCodPorcentImp());
		    }
	        
	        validformat = setCampoFormato(matrizLinea, 3, "tarifaImp", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		    }else{
		    		totImpTmp.setTarifaImp(validformat.getValueDouble());
			        System.out.println("tarifaImp::"+totImpTmp.getTarifaImp());
		    }
	        
	        validformat = setCampoFormato(matrizLinea, 4, "baseImponibleImp", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		    }else{
		    		totImpTmp.setBaseImponibleImp(validformat.getValueDouble());
			        System.out.println("baseImponibleImp::"+totImpTmp.getBaseImponibleImp());
		    }        
	
	        validformat = setCampoFormato(matrizLinea, 5, "valorImp", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		    }else{
		    		totImpTmp.setValorImp(validformat.getValueDouble());
			        System.out.println("valorImp::"+totImpTmp.getValorImp());
		    }        
	
	        validformat = setCampoFormato(matrizLinea, 6, "impuestoImp", "String", "", 3, "N");        
			totImpTmp.setImpuestoImp(validformat.getValueString());
	        System.out.println("impuestoImp::"+totImpTmp.getImpuestoImp());	    
	
	        ListDetTotalImp.add(totImpTmp);
	        emite.getInfEmisor().setListDetDetImpuestos(ListDetTotalImp);
	      }
	      
	      
	    if (idLinea.equals("DE")){
	    	detDocumentImp = new DetalleDocumento();
	    	if (!(tipo_documento.equals("06"))){
		    	validformat = setCampoFormato(matrizLinea, 1, "lineaDetalleDE", "Int", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setLineaFactura(validformat.getValueInt());	        	
			        System.out.println("lineaDetalleDE::"+detDocumentImp.getLineaFactura());
		        }
		        
		    	validformat = setCampoFormato(matrizLinea, 2, "codigoPrincipal", "String", "", 25, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setCodigoPrincipal(validformat.getValueString());	        	
			        System.out.println("codigoPrincipal::"+detDocumentImp.getCodigoPrincipal());
		        }
	
		        validformat = setCampoFormato(matrizLinea,3, "codigoAuxiliar", "String", "", 25, "N");
		        detDocumentImp.setCodigoAuxiliar(validformat.getValueString());
		        System.out.println("codigoAuxiliar::"+detDocumentImp.getCodigoAuxiliar());
	
		        validformat=setCampoFormato(matrizLinea,4, "descripcion", "String", "", 300, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setDescripcion(validformat.getValueString());
			        System.out.println("descripcion::"+detDocumentImp.getDescripcion());
		        }
	
		        validformat=setCampoFormato(matrizLinea,5, "cantidad", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setCantidad(validformat.getValueDouble());
		        	System.out.println("cantidad::"+detDocumentImp.getCantidad());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,6, "precioUnitario", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setPrecioUnitario(validformat.getValueDouble());
		        	System.out.println("precioUnitario::"+detDocumentImp.getPrecioUnitario());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,7, "descuento", "Double", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setDescuento(validformat.getValueDouble());
		        	System.out.println("descuento::"+detDocumentImp.getDescuento());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,8, "precioTotalSinImpuesto", "Double", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setPrecioTotalSinImpuesto(validformat.getValueDouble());
		        	System.out.println("precioTotalSinImpuesto::"+detDocumentImp.getPrecioTotalSinImpuesto());
		        }	        
	    	}else{
	    		validformat = setCampoFormato(matrizLinea, 1, "IdentificacionDestinatario", "String", "", 13, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setIdentificacionDestinatario(validformat.getValueString());	        	
			        System.out.println("IdentificacionDestinatario::"+detDocumentImp.getIdentificacionDestinatario());
		        }
	    		
	    		validformat = setCampoFormato(matrizLinea, 2, "lineaDetalleDE", "Int", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setLineaFactura(validformat.getValueInt());	        	
			        System.out.println("lineaDetalleDE::"+detDocumentImp.getLineaFactura());
		        }
		        
		    	validformat = setCampoFormato(matrizLinea, 3, "codigoPrincipal", "String", "", 25, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setCodigoPrincipal(validformat.getValueString());	        	
			        System.out.println("codigoPrincipal::"+detDocumentImp.getCodigoPrincipal());
		        }
	
		        validformat = setCampoFormato(matrizLinea,4, "codigoAuxiliar", "String", "", 25, "N");
		        detDocumentImp.setCodigoAuxiliar(validformat.getValueString());
		        System.out.println("codigoAuxiliar::"+detDocumentImp.getCodigoAuxiliar());
	
		        validformat=setCampoFormato(matrizLinea,5, "descripcion", "String", "", 300, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setDescripcion(validformat.getValueString());
			        System.out.println("descripcion::"+detDocumentImp.getDescripcion());
		        }
	
		        validformat=setCampoFormato(matrizLinea,6, "cantidad", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setCantidad(validformat.getValueDouble());
		        	System.out.println("cantidad::"+detDocumentImp.getCantidad());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,7, "precioUnitario", "Double", "", 0, "S");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setPrecioUnitario(validformat.getValueDouble());
		        	System.out.println("precioUnitario::"+detDocumentImp.getPrecioUnitario());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,8, "descuento", "Double", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setDescuento(validformat.getValueDouble());
		        	System.out.println("descuento::"+detDocumentImp.getDescuento());
		        }
		        
		        validformat=setCampoFormato(matrizLinea,9, "precioTotalSinImpuesto", "Double", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
			          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	detDocumentImp.setPrecioTotalSinImpuesto(validformat.getValueDouble());
		        	System.out.println("precioTotalSinImpuesto::"+detDocumentImp.getPrecioTotalSinImpuesto());
		        }
	    	}
	        ListDetalleDocumento.add(detDocumentImp);
	    }
	    
	    if (idLinea.equals("IM")){	    	
			DocImp = new DocumentoImpuestos();
			
			validformat = setCampoFormato(matrizLinea, 1, "lineaDetalleIM", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setLineaFactura(validformat.getValueInt());	        	
		        System.out.println("lineaDetalleIM::"+DocImp.getLineaFactura());
	        }
	        
 			validformat = setCampoFormato(matrizLinea, 3, "impuestoCodigo", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	
	        	DocImp.setImpuestoCodigo(validformat.getValueInt());	        	
		        System.out.println("impuestoCodigo::"+DocImp.getImpuestoCodigo());
	        }
	        
	        validformat = setCampoFormato(matrizLinea, 4, "impuestoCodigoPorcentaje", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setImpuestoCodigoPorcentaje(validformat.getValueInt());	        	
		        System.out.println("impuestoCodigoPorcentaje::"+DocImp.getImpuestoCodigoPorcentaje());
	        }
	        
	        validformat = setCampoFormato(matrizLinea, 6, "impuestoTarifa", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setImpuestoTarifa(validformat.getValueDouble());	        	
		        System.out.println("impuestoTarifa::"+DocImp.getImpuestoTarifa());
	        }
	        
	        validformat = setCampoFormato(matrizLinea, 5, "impuestoBaseImponible", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setImpuestoBaseImponible(validformat.getValueDouble());	        	
		        System.out.println("impuestoBaseImponible::"+DocImp.getImpuestoBaseImponible());
	        }
	        
	        validformat = setCampoFormato(matrizLinea, 7, "impuestoValor", "Double", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setImpuestoValor(validformat.getValueDouble());	        	
		        System.out.println("impuestoValor::"+DocImp.getImpuestoValor());
	        }
	        ListDocumentoImpuestos.add(DocImp);
		}
	    
	    if (idLinea.equals("DA")){
	    	DetAdic = new DetallesAdicionales();
	    	
	    	validformat = setCampoFormato(matrizLinea, 1, "lineaDetalleDA", "Int", "", 0, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DocImp.setLineaFactura(validformat.getValueInt());	        	
		        System.out.println("lineaDetalleDA::"+DocImp.getLineaFactura());
	        }
	        
 			validformat = setCampoFormato(matrizLinea, 2, "Name", "String", "", 300, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DetAdic.setNombre(validformat.getValueString());	        	
		        System.out.println("Name::"+DetAdic.getNombre());
	        }
	        
	        validformat = setCampoFormato(matrizLinea, 3, "Valor", "String", "", 300, "S");
	        if (validformat.getLi_error() == 1) {
	          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
	        }else{
	        	DetAdic.setValor(validformat.getValueString());	        	
		        System.out.println("Valor::"+DetAdic.getValor());
	        }
	        ListDocumentoDetAdicionales.add(DetAdic);
 		}
	    
	 	//IA
	 	if (idLinea.equals("IA")){
	 		//private ArrayList<InformacionAdicional> ListInfAdicional;
	 		InformacionAdicional IAdic = new InformacionAdicional();
	 		
	 		validformat = setCampoFormato(matrizLinea,1, "Name", "String", "", 300, "N");
	 		IAdic.setName(validformat.getValueString());
	        System.out.println("IA-Name::"+IAdic.getName());	 		
	        
	        validformat = setCampoFormato(matrizLinea,2, "Value", "String", "", 300, "N");
	        IAdic.setValue(validformat.getValueString());
	        System.out.println("IA-Value::"+IAdic.getValue());
	        
	        ListInformacionAdicional.add(IAdic);	        
	 	}
	 	
	 	//MO	 	
	 	if ((tipo_documento.equals("05"))){
	 		if (idLinea.equals("MO")){	 		
	 			InformacionAdicional IAdicMotivo = new InformacionAdicional();
		 		
		 		validformat = setCampoFormato(matrizLinea,1, "Name", "String", "", 300, "N");
		 		IAdicMotivo.setName(validformat.getValueString());
		        System.out.println("IA-Name::"+IAdicMotivo.getName());		 		
		        
		        validformat = setCampoFormato(matrizLinea,2, "Value", "String", "", 300, "N");
		        IAdicMotivo.setValue(validformat.getValueString());
		        System.out.println("IA-Value::"+IAdicMotivo.getValue());
		        
		        ListInformacionMotivo.add(IAdicMotivo);
		 	}
	 	}
	 	
	 	//MO	 	
	 	if ((tipo_documento.equals("06"))){
	 		if (idLinea.equals("DEST")){
	 			Destinatarios dest = new Destinatarios();
	 			
	 			validformat = setCampoFormato(matrizLinea, 1, "idDestinatario", "String", "", 13, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setIdentDestinatario(validformat.getValueString());	        	
			        System.out.println("idDestinatario::"+dest.getIdentDestinatario());
		        }
	 			
		        validformat = setCampoFormato(matrizLinea, 2, "razonSocialDestinatario", "String", "", 300, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setRazonSocialDestinatario(validformat.getValueString());	        	
			        System.out.println("razonSocialDestinatario::"+dest.getRazonSocialDestinatario());
		        }
	 			
		        validformat = setCampoFormato(matrizLinea, 3, "dirDestinatario", "String", "", 300, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setDirDestinatario(validformat.getValueString());	        	
			        System.out.println("dirDestinatario::"+dest.getDirDestinatario());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 4, "motivoTraslado", "String", "", 300, "S");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setMotTraslDestinatario(validformat.getValueString());	        	
			        System.out.println("motivoTraslado::"+dest.getMotTraslDestinatario());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 5, "docAduaneroUnico", "String", "", 20, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setDocAduanero(validformat.getValueString());	        	
			        System.out.println("docAduaneroUnico::"+dest.getDocAduanero());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 6, "codEstabDestino", "Int", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setCodEstabDestino(validformat.getValueInt());	        	
			        System.out.println("codEstabDestino::"+dest.getCodEstabDestino());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 7, "ruta", "String", "", 300, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setRutaDest(validformat.getValueString());	        	
			        System.out.println("ruta::"+dest.getRutaDest());
		        }
		        
		        
		        validformat = setCampoFormato(matrizLinea, 8, "codDocSustento", "Int", "", 0, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setCodDocSustentoDest(validformat.getValueInt());	        	
			        System.out.println("codDocSustento::"+dest.getCodDocSustentoDest());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 9, "numDocSustento", "String", "", 17, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setNumDocSustentoDest(validformat.getValueString());	        	
			        System.out.println("numDocSustento::"+dest.getNumDocSustentoDest());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 10, "numAutDocSustento", "String", "", 37, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setNumAutDocSustDest(validformat.getValueString());	        	
			        System.out.println("numAutDocSustento::"+dest.getNumAutDocSustDest());
		        }
		        
		        validformat = setCampoFormato(matrizLinea, 11, "fechaEmisionDocSustento", "Date", "dd/MM/yyyy", 0, "N");
		        if (validformat.getLi_error() == 1) {
		          li_result = 2;  throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+validformat.getLs_msg_error());
		        }else{
		        	dest.setFechEmisionDocSustDest(validformat.getValueDate());	        	
			        System.out.println("fechaEmisionDocSustento::"+dest.getFechEmisionDocSustDest());
		        }	 			
		 	ListDestinatarios.add(dest);
	 		
	 		}
	 	}
	 	
    }   
    }
    
    try
    {
      if (entrada != null)
        entrada.close();
    }
    catch (Exception exf) {
    	li_result = 4;
    	throw new Exception("Error::"+classReference+"::Error en el file::"+f+"::"+exf.getMessage()+"|"+exf.getCause());
    }

    //Validaciones
    /*
    Emisor e = new Emisor();
    InformacionTributaria infTribAdic = new InformacionTributaria();
    
    infTribAdic = e.obtieneInfoAdicional(emite.getInfEmisor().getRuc(), emite.getInfEmisor().getCodEstablecimiento(), emite.getInfEmisor().getCodPuntoEmision(), tipo_documento);
    infTrib.setAmbiente(infTribAdic.getAmbiente());
    if (emite.getInfEmisor().getTipoEmision() == 0) {
    	infTrib.setTipoEmision(infTribAdic.getTipoEmision());
    }
    
    infTribAdic = e.obtieneInfoTributaria(emite.getInfEmisor().getRuc());
    infTrib.set_pathGenerados(infTribAdic.get_pathGenerados());
    infTrib.set_pathFirmados(infTribAdic.get_pathFirmados());    
    infTrib.set_pathInfoRecibida(infTribAdic.get_pathInfoRecibida());
    
    
    System.out.println("pathGenerados::"+infTribAdic.get_pathGenerados());
    System.out.println("pathFirmados::"+infTribAdic.get_pathFirmados());
    System.out.println("pathInfoRecibida::"+infTribAdic.get_pathInfoRecibida());
    */
    emite.getInfEmisor().setListDetDocumentos(ListDetalleDocumento);
    emite.getInfEmisor().setListInfAdicional(ListInformacionAdicional);
    /*emite.getInfEmisor().setListDetDestinatarios(ListDestinatarios);
    emite.getInfEmisor().setListDetDetImpuestos(ListDocumentoImpuestos);
    emite.getInfEmisor().setListDetDetImpuestosRetenciones(ListDetTotalImpRetenciones);
    
    emite.getInfEmisor().setListInfMotivos(ListInformacionMotivo);*/
    
    SimpleDateFormat formatoFecha = new SimpleDateFormat();
    formatoFecha.applyPattern("yyyymmdd");
    String fechafile = formatoFecha.format(new Date());

    String filename = 
      emite.getInfEmisor().getRuc() + emite.getInfEmisor().getCodDocumento() + 
      emite.getInfEmisor().getCodEstablecimiento() + emite.getInfEmisor().getCodPuntoEmision() + 
      emite.getInfEmisor().getSecuencial() + fechafile + ".xml";
    //String fileXml = creaXmlDocumentos(e.getInfEmisor().get_pathGenerados()+ filename, emite, tipo_documento, f.getName());
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    if (!e.existeEstablecimiento(e.getInfEmisor().getRuc(),e.getInfEmisor().getCodEstablecimiento())){
    	String mensaje = " Establecimiento no existe o no se encuentra Activa. Ruc->" +e.getInfEmisor().getRuc()+ " Establecimiento->"+e.getInfEmisor().getCodEstablecimiento();    	
    	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
    	throw new Exception(mensaje);
    }
    
    if (!e.existePuntoEmision(e.getInfEmisor().getRuc(),e.getInfEmisor().getCodEstablecimiento(), e.getInfEmisor().getCodPuntoEmision())){
    	String mensaje = " Establecimiento no existe o no se encuentra Activa. Ruc->" +e.getInfEmisor().getRuc()+ " Establecimiento->"+e.getInfEmisor().getCodEstablecimiento();
    	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
    	throw new Exception(mensaje);
    }
    
    if (!e.existeDocumentoPuntoEmision(e.getInfEmisor().getRuc(),e.getInfEmisor().getCodEstablecimiento(), e.getInfEmisor().getCodPuntoEmision(), e.getInfEmisor().getCodDocumento())){
    	String mensaje = " En el Establecimiento el tipo de Documento no existe o no se encuentra Activa. Ruc->" +e.getInfEmisor().getRuc()+ " Establecimiento->"+e.getInfEmisor().getCodEstablecimiento()+" Tipo de Documento->"+e.getInfEmisor().getCodDocumento();
    	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
    	throw new Exception(mensaje);
    }
    
    e.getInfEmisor().setAmbiente(Integer.parseInt(e.ambienteDocumentoPuntoEmision(e.getInfEmisor().getRuc(),e.getInfEmisor().getCodEstablecimiento(), e.getInfEmisor().getCodPuntoEmision(), e.getInfEmisor().getCodDocumento())));       
    if (e.getInfEmisor().getAmbiente()==-1){
    	String mensaje = " Revise el valor del Ambiente-> "+e.getInfEmisor().getAmbiente()+". Ruc->" +e.getInfEmisor().getRuc()+ " Establecimiento->"+e.getInfEmisor().getCodEstablecimiento()+" Tipo de Documento->"+e.getInfEmisor().getCodDocumento();
    	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
    	throw new Exception(mensaje);
    }
        
    //Obtencion del mail del establecimiento
    e.obtieneMailEstablecimiento(e.getInfEmisor()); 
    e.getInfEmisor().setAmbientePuntoEmision(e.ambienteDocumentoPuntoEmision(e.getInfEmisor().getRuc(),e.getInfEmisor().getCodEstablecimiento(), e.getInfEmisor().getCodPuntoEmision(), e.getInfEmisor().getCodDocumento()));
    e.insertaBitacoraDocumento(e.getInfEmisor().getAmbientePuntoEmision(), 
				    			 e.getInfEmisor().getRuc(), 			    			  						  
				    			 e.getInfEmisor().getCodEstablecimiento(), 
				    			 e.getInfEmisor().getCodPuntoEmision(), 
				    			 e.getInfEmisor().getSecuencial(), 
				    			 e.getInfEmisor().getCodDocumento(), 
				    			 e.getInfEmisor().getFecEmision(),
								 "LF", 
								 "Lectura del Archivo "+filepath+" del Proceso de Despacho", 
								 "", 
								 "", 
								 "", 
								 "", 
								 "",
								 e.getInfEmisor().getTipoEmision());    
    
    String fileXml = creaXmlDocumentos(e.getInfEmisor().get_pathGenerados()+ filename, emite, tipo_documento, "");
    e.setResultado(li_result);
    e.setFilexml(fileXml);
    return e;
  }

  public static String generarClaveAcceso(Emisor emite) throws Exception {
    int li_random = (int)Math.round(Math.random() * 10000000);
    String fecha_clave = "";
    System.out.println("li_random::"+Integer.toString(li_random));
    String codigoNumerico = digitoVerificador(Integer.toString(li_random));
    SimpleDateFormat formatoFecha = new SimpleDateFormat();
    formatoFecha.applyPattern("ddMMyyyy");              
    System.out.println("TipoComprobante::" + emite.getInfEmisor().getTipoComprobante());
    System.out.println("FechaEmision::"+emite.getInfEmisor().getFecEmision());
    fecha_clave = formatoFecha.format(emite.getInfEmisor().getFecEmision());
    if (fecha_clave.length()!=8){
    	throw new Exception("generarClaveAcceso::FechaEmision Formato Invalido:"+fecha_clave);
    }    
    System.out.println("Armada de Clave de Acceso");
    System.out.println("Fecha de Emisión::"+fecha_clave.toString());
    System.out.println("Tipo de Comprobante::"+emite.getInfEmisor().getTipoComprobante());
    System.out.println("Número de RUC::"+emite.getInfEmisor().getRuc());
    System.out.println("Tipo de Ambiente::"+emite.getInfEmisor().getAmbiente());
    System.out.println("Serie::"+emite.getInfEmisor().getCodEstablecimiento() + emite.getInfEmisor().getCodPuntoEmision());
    System.out.println("Número del Comprobante::"+emite.getInfEmisor().getSecuencial());
    System.out.println("Código Numérico::"+codigoNumerico);
    System.out.println("Tipo de Emisión::"+emite.getInfEmisor().getTipoEmision());    
    String clave = fecha_clave + emite.getInfEmisor().getTipoComprobante() + emite.getInfEmisor().getRuc() + 
      emite.getInfEmisor().getAmbiente() + emite.getInfEmisor().getCodEstablecimiento() + 
      emite.getInfEmisor().getCodPuntoEmision() + emite.getInfEmisor().getSecuencial() + codigoNumerico + emite.getInfEmisor().getTipoEmision();
    System.out.println("Clave::"+clave);
    clave = digitoVerificador(clave); 
    System.out.println("Clave DigitoVerificador::"+clave);
    return clave;
  }
  
  public static String generarClaveAccesoContingencia(Emisor emite, String claveContingencia) throws Exception {	   
	  int li_random = (int)Math.round(Math.random() * 10000000);
	    String fecha_clave = "";
	    System.out.println("li_random::"+Integer.toString(li_random));
	    String codigoNumerico = digitoVerificador(Integer.toString(li_random));	    
	    SimpleDateFormat formatoFecha = new SimpleDateFormat();
	    formatoFecha.applyPattern("ddMMyyyy");	    	    	    
	    System.out.println("TipoComprobante::" + emite.getInfEmisor().getTipoComprobante());	
	    System.out.println("Fecha de Emision:"+emite.getInfEmisor().getFecEmision());
	    fecha_clave = emite.getInfEmisor().getFecEmision().replace("/", "");
	    if (fecha_clave.length()!=8){
	    	throw new Exception("generarClaveAcceso::FechaEmision Formato Invalido:"+fecha_clave);
	    }    
	    String clave = fecha_clave + emite.getInfEmisor().getTipoComprobante() + claveContingencia + "2";
	    clave = digitoVerificador(clave);    
	    System.out.println("Clave:"+clave);
	    System.out.println("Length Clave:"+clave.length());
	        
	    return clave;
 }
  
  public static String digitoVerificador(String number) {
    int Sum = 0;
    int i = number.length() - 1; for (int Multiplier = 2; i >= 0; i--)
    {
      Sum += Integer.parseInt(number.substring(i, i + 1)) * Multiplier;
      Multiplier++; if (Multiplier == 8) Multiplier = 2;
    }
    int Validator = 11 - Sum % 11;

    if (Validator == 11) Validator = 0;
    else if (Validator == 10) Validator = 1;

    return number + Validator;
  }

  public static boolean getContextDocu(String contenido, List listado){	  	 
	  for (int i = 0; i < listado.size(); i++) {
		  String context = listado.get(i).toString();		  
				if(context.contains(contenido)){
					return true;
				}
			
	  }
	  
	  return false;
  }
  
  public static String creaXmlDocumentos(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	  String ls_result ="";
	  if (tipoDocumento.equals("01")){
		  ls_result =  factura(ruta, emite, tipoDocumento,nameOriginal);
	  }
	  if (tipoDocumento.equals("04")){
		  ls_result =  notaCredito(ruta, emite, tipoDocumento, nameOriginal);		  
	  }
	  if (tipoDocumento.equals("05")){
		  ls_result = notaDebito(ruta, emite, tipoDocumento,nameOriginal);
	  }
	  
	  if (tipoDocumento.equals("06")){
		  ls_result = guiaRemision(ruta, emite, tipoDocumento, nameOriginal);
	  }
	  if (tipoDocumento.equals("07")){
		  ls_result = comprobanteRetencion(ruta, emite, tipoDocumento,nameOriginal);
	  }
	return ls_result;
  }
  
  public static String comprobanteRetencion(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	
	  String fileFirmado = "";
      String rutaFirmado = "";
    try {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        ArrayList ListDetDocumentosTmp = new ArrayList();

        String ls_tipo_documento = "";
        Attr attr = null;
        
        //Obtencion del Tab Principal del XML para el tipo de Documento.
        ls_tipo_documento = Environment.c.getString("facElectronica.tiposDocumento.doc"+tipoDocumento);        
        //////////////////////Cabecera Xml///////////////////////////////
        Document doc = docBuilder.newDocument();
        Element document = doc.createElement(ls_tipo_documento);
        doc.appendChild(document);
        attr = doc.createAttribute("id");
        attr.setValue("comprobante");
        document.setAttributeNode(attr);
        String version;
        if ((emite.getInfEmisor().getVersion() != null) || (emite.getInfEmisor().getVersion().equals("")))
        {  version = emite.getInfEmisor().getVersion();		}
        else{ 	version = "1.0.0";	}
        attr = doc.createAttribute("version");
        attr.setValue(version);
        document.setAttributeNode(attr);   
        //////////////////////Cabecera Xml///////////////////////////////
        
        //////////////////////infoTributaria////////////////////////////
        Element infoTrib = doc.createElement("infoTributaria");
        document.appendChild(infoTrib);
                
        List formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ambiente.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element ambiente = doc.createElement("ambiente");
	        ambiente.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getAmbiente()).toString()));
	        infoTrib.appendChild(ambiente);
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.tipoEmision.documentos");
        //Obligatorio, conforme tabla 2 Numérico 1
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element tipoEmision = doc.createElement("tipoEmision");
        tipoEmision.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getTipoEmision()).toString()));
        infoTrib.appendChild(tipoEmision);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.razonSocial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	Element razonSocial = doc.createElement("razonSocial");
        	razonSocial.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocial()));
        	infoTrib.appendChild(razonSocial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.nombreComercial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element nombreComercial = doc.createElement("nombreComercial");
        nombreComercial.appendChild(doc.createTextNode(emite.getInfEmisor().getNombreComercial()));
        infoTrib.appendChild(nombreComercial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ruc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ruc = doc.createElement("ruc");
        ruc.appendChild(doc.createTextNode(emite.getInfEmisor().getRuc()));
        infoTrib.appendChild(ruc);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.claveAcceso.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        String ls_clave_acceso = "";
		try {
			ls_clave_acceso = generarClaveAcceso(emite);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        		        	
        if (ls_clave_acceso.length()!=49){
        	try {
				ls_clave_acceso = generarClaveAcceso(emite);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        emite.getInfEmisor().setClaveAcceso(ls_clave_acceso);
        
        System.out.println("ClaveAcceso::" + ls_clave_acceso);
        Element claveAcceso = doc.createElement("claveAcceso");
        claveAcceso.appendChild(doc.createTextNode(ls_clave_acceso));
        infoTrib.appendChild(claveAcceso);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.codDoc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element codDoc = doc.createElement("codDoc");
        codDoc.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocumento()));
        infoTrib.appendChild(codDoc);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.estab.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element estab = doc.createElement("estab");
        estab.appendChild(doc.createTextNode(emite.getInfEmisor().getCodEstablecimiento()));
        infoTrib.appendChild(estab);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ptoEmi.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ptoEmi = doc.createElement("ptoEmi");
        ptoEmi.appendChild(doc.createTextNode(emite.getInfEmisor().getCodPuntoEmision()));
        infoTrib.appendChild(ptoEmi);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.secuencial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element secuencial = doc.createElement("secuencial");
        secuencial.appendChild(doc.createTextNode(emite.getInfEmisor().getSecuencial()));
        infoTrib.appendChild(secuencial);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.dirMatriz.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
        Element dirMatriz = doc.createElement("dirMatriz");
        dirMatriz.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionMatriz()));
        infoTrib.appendChild(dirMatriz);          
        }
        //////////////////////infoTributaria////////////////////////////        
        /////////////////infoComprobante Retencion//////////////////////
        String ls_info_documento = Environment.c.getString("facElectronica.infoDocumento.doc"+tipoDocumento);
        Element infoDocu = doc.createElement("info"+ls_info_documento);
        document.appendChild(infoDocu);
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmision = doc.createElement("fechaEmision");
	        fechaEmision.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmision()));
	        infoDocu.appendChild(fechaEmision);
        }

        if (emite.getInfEmisor().getDireccionEstablecimiento().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirEstablecimiento.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirEstablecimiento = doc.createElement("dirEstablecimiento");
		        dirEstablecimiento.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionEstablecimiento()));
		        infoDocu.appendChild(dirEstablecimiento);
	        }
        }
        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.contribuyenteEspecial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getContribEspecial() > 99) &&(emite.getInfEmisor().getContribEspecial() <= 99999))
	        {
	  	      Element contribuyenteEspecial = doc.createElement("contribuyenteEspecial");
	  	      contribuyenteEspecial.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getContribEspecial()).toString()));
	  	      infoDocu.appendChild(contribuyenteEspecial);
	        }
        }
        
        if (emite.getInfEmisor().getObligContabilidad().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.obligadoContabilidad.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element obligadoContabilidad = doc.createElement("obligadoContabilidad");
    	        obligadoContabilidad.appendChild(doc.createTextNode(emite.getInfEmisor().getObligContabilidad()));
    	        infoDocu.appendChild(obligadoContabilidad);
            }
        }  
                       
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tipoIdentificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTipoIdentificacion() != null){
	        Element tipoIdentificacionComprador = doc.createElement("tipoIdentificacionSujetoRetenido");
	        tipoIdentificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getTipoIdentificacion()));
	        infoDocu.appendChild(tipoIdentificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.razonSocialComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("razonSocialComprador::"+emite.getInfEmisor().getRazonSocialComp());
	        if ((emite.getInfEmisor().getRazonSocialComp() != null) && 
	          (emite.getInfEmisor().getRazonSocialComp().length() > 0)) {
	          Element razonSocialComprador = doc.createElement("razonSocialSujetoRetenido");
	          razonSocialComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocialComp()));
	          infoDocu.appendChild(razonSocialComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.identificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("identificacionComprador::"+emite.getInfEmisor().getIdentificacionComp());
	        if ((emite.getInfEmisor().getIdentificacionComp() != null) && 
	          (emite.getInfEmisor().getIdentificacionComp().length() > 0)) {
	          Element identificacionComprador = doc.createElement("identificacionSujetoRetenido");
	          identificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getIdentificacionComp()));
	          infoDocu.appendChild(identificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.periodoFiscal.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("identificacionComprador::"+emite.getInfEmisor().getIdentificacionComp());
	        if ((emite.getInfEmisor().getIdentificacionComp() != null) && 
	          (emite.getInfEmisor().getIdentificacionComp().length() > 0)) {
	          Element identificacionComprador = doc.createElement("periodoFiscal");
	          //Comentariado por ser OroVerde
	          /*
	          identificacionComprador.appendChild(doc.createTextNode(formateoDate(emite.getInfEmisor().getPeriodoFiscal(),"MM/yyyy")));
	          */
	          infoDocu.appendChild(identificacionComprador);
	        }
        }
        
        
        //////////////////////totalConImpuestos////////////////////////////
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalConImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        	        
        Element totalConImpuestos = doc.createElement("impuestos");
        infoDocu.appendChild(totalConImpuestos);        
        if (emite.getInfEmisor().getListDetDetImpuestos().size()>0){        
        System.out.println("Size::" + emite.getInfEmisor().getListDetDetImpuestos().size());
        for (DetalleTotalImpuestosRetenciones e : emite.getInfEmisor().getListDetDetImpuestosRetenciones())
        {
          Element totalImpuesto = doc.createElement("impuesto");
          totalConImpuestos.appendChild(totalImpuesto);
          
          Element impuestoCodigo = doc.createElement("codigo");
          impuestoCodigo.appendChild(doc.createTextNode(new Integer(e.getCodigo()).toString()));
          totalImpuesto.appendChild(impuestoCodigo);
          
          Element impuestoCodigoPorcentaje = doc.createElement("codigoRetencion");
          impuestoCodigoPorcentaje.appendChild(doc.createTextNode(new Integer(e.getCodigoRetencion()).toString()));
          totalImpuesto.appendChild(impuestoCodigoPorcentaje);

          Element impuestoBaseImponible = doc.createElement("baseImponible");
          impuestoBaseImponible.appendChild(doc.createTextNode(new Double(e.getBaseImponible()).toString()));
          totalImpuesto.appendChild(impuestoBaseImponible);

          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.porcentajeRetener.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){          
	          Element impuestoTarifa = doc.createElement("porcentajeRetener");
	          impuestoTarifa.appendChild(doc.createTextNode(new Double(e.getPorcentajeRetener()).toString()));
	          totalImpuesto.appendChild(impuestoTarifa);
          }

          Element impuestoValor = doc.createElement("valorRetenido");
          impuestoValor.appendChild(doc.createTextNode(new Double(e.getValorRetenido()).toString()));
          totalImpuesto.appendChild(impuestoValor);
          
          Element codDocSustento = doc.createElement("codDocSustento");
          codDocSustento.appendChild(doc.createTextNode(new Double(e.getCodDocSustento()).toString()));
          totalImpuesto.appendChild(codDocSustento);
          
          Element numDocSustento = doc.createElement("numDocSustento");
          numDocSustento.appendChild(doc.createTextNode(new Double(e.getNumDocSustento()).toString()));
          totalImpuesto.appendChild(numDocSustento);
          
          Element fechaEmisionDocSustento = doc.createElement("fechaEmisionDocSustento");
          fechaEmisionDocSustento.appendChild(doc.createTextNode(formateoDate(e.getFechaEmisionDocSustento(),"dd/MM/yyyy")));
          totalImpuesto.appendChild(fechaEmisionDocSustento);
          
        }
        }
        }
        
		//////////////////////////////////////////////////////////
		/////////////////Informacion Adicional////////////////////
		//////////////////////////////////////////////////////////
        Element infoAdicional = doc.createElement("infoAdicional");
        document.appendChild(infoAdicional);
        if (ListDocumentoDetAdicionales!=null){
        	if (ListDocumentoDetAdicionales.size()>0){
	        	for (int y=0;y<=ListDocumentoDetAdicionales.size()-1;y++){
	        		DetallesAdicionales DetAdic = new DetallesAdicionales();
	        		DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(y);
		    	        Element campoAdicional = doc.createElement("campoAdicional"); 
		    	        attr = doc.createAttribute("nombre");
		    	        attr.setValue(DetAdic.getNombre());
		    	        campoAdicional.setAttributeNode(attr);
		    	        
		    	        attr = doc.createAttribute("valor");
		    	        attr.setValue(DetAdic.getValor());
		    	        campoAdicional.setAttributeNode(attr);		    	        
		    	        infoAdicional.appendChild(campoAdicional);
	        		
	        	}
        	}
        } 
        
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);      
      if (nameOriginal != null){    	
    	  ruta = nameOriginal.replace("txt", "xml");
      }
      File file = new File(ruta);
      StreamResult result = new StreamResult(file);
      //result =  new StreamResult(System.out);
      transformer.transform(source, result);      
      System.out.println("File saved!\n"+ruta);
      return file.getName();
      /*
      String respuestaFirma = null;
      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
      ArchivoUtils arcFirmar = new ArchivoUtils();      
      emi.setRuc("0992531940001");
      
      System.out.println("ruta::" + ruta);
      System.out.println("file"+file.getName());
      fileFirmado = file.getName();
      rutaFirmado = emite.getInfEmisor().get_pathFirmados();
      respuestaFirma = ArchivoUtils.firmarArchivo(emi, ruta, rutaFirmado, "BCE_IKEY2032", null);
      
      if (respuestaFirma == null)
      {
        System.out.println("Firmado OK::"+respuestaFirma);
      }
      else System.out.println("Firmado Error::" + respuestaFirma);
      */      
      //return true;
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
      return "";
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    /*} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();*/
	}return "";
    

  }
  
  public static String notaCredito(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	
	  String fileFirmado = "";
      String rutaFirmado = "";
    try {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        ArrayList ListDetDocumentosTmp = new ArrayList();

        String ls_tipo_documento = "";
        Attr attr = null;
        
        //Obtencion del Tab Principal del XML para el tipo de Documento.
        ls_tipo_documento = Environment.c.getString("facElectronica.tiposDocumento.doc"+tipoDocumento);        
        //////////////////////Cabecera Xml///////////////////////////////
        Document doc = docBuilder.newDocument();
        Element document = doc.createElement(ls_tipo_documento);
        doc.appendChild(document);
        attr = doc.createAttribute("id");
        attr.setValue("comprobante");
        document.setAttributeNode(attr);
        String version;
        if ((emite.getInfEmisor().getVersion() != null) || (emite.getInfEmisor().getVersion().equals("")))
        {  version = emite.getInfEmisor().getVersion();		}
        else{ 	version = "1.0.0";	}
        attr = doc.createAttribute("version");
        attr.setValue(version);
        document.setAttributeNode(attr);   
        //////////////////////Cabecera Xml///////////////////////////////
        
        //////////////////////infoTributaria////////////////////////////
        Element infoTrib = doc.createElement("infoTributaria");
        document.appendChild(infoTrib);
                
        List formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ambiente.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element ambiente = doc.createElement("ambiente");
	        ambiente.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getAmbiente()).toString()));
	        infoTrib.appendChild(ambiente);
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.tipoEmision.documentos");
        //Obligatorio, conforme tabla 2 Numérico 1
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element tipoEmision = doc.createElement("tipoEmision");
        tipoEmision.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getTipoEmision()).toString()));
        infoTrib.appendChild(tipoEmision);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.razonSocial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	Element razonSocial = doc.createElement("razonSocial");
        	razonSocial.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocial()));
        	infoTrib.appendChild(razonSocial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.nombreComercial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element nombreComercial = doc.createElement("nombreComercial");
        nombreComercial.appendChild(doc.createTextNode(emite.getInfEmisor().getNombreComercial()));
        infoTrib.appendChild(nombreComercial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ruc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ruc = doc.createElement("ruc");
        ruc.appendChild(doc.createTextNode(emite.getInfEmisor().getRuc()));
        infoTrib.appendChild(ruc);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.claveAcceso.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	String ls_clave_acceso = "";
    		try {
    			ls_clave_acceso = generarClaveAcceso(emite);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        		        	
            if (ls_clave_acceso.length()!=49){
            	try {
    				ls_clave_acceso = generarClaveAcceso(emite);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
        
        emite.getInfEmisor().setClaveAcceso(ls_clave_acceso);    
        System.out.println("ClaveAcceso::" + ls_clave_acceso);
        Element claveAcceso = doc.createElement("claveAcceso");
        claveAcceso.appendChild(doc.createTextNode(ls_clave_acceso));
        infoTrib.appendChild(claveAcceso);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.codDoc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element codDoc = doc.createElement("codDoc");
        codDoc.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocumento()));
        infoTrib.appendChild(codDoc);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.estab.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element estab = doc.createElement("estab");
        estab.appendChild(doc.createTextNode(emite.getInfEmisor().getCodEstablecimiento()));
        infoTrib.appendChild(estab);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ptoEmi.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ptoEmi = doc.createElement("ptoEmi");
        ptoEmi.appendChild(doc.createTextNode(emite.getInfEmisor().getCodPuntoEmision()));
        infoTrib.appendChild(ptoEmi);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.secuencial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element secuencial = doc.createElement("secuencial");
        secuencial.appendChild(doc.createTextNode(emite.getInfEmisor().getSecuencial()));
        infoTrib.appendChild(secuencial);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.dirMatriz.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
        Element dirMatriz = doc.createElement("dirMatriz");
        dirMatriz.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionMatriz()));
        infoTrib.appendChild(dirMatriz);          
        }
        //////////////////////infoTributaria////////////////////////////        
        //////////////////////infoFactura////////////////////////////
        String ls_info_documento = Environment.c.getString("facElectronica.infoDocumento.doc"+tipoDocumento);
        Element infoDocu = doc.createElement("info"+ls_info_documento);
        document.appendChild(infoDocu);
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmision = doc.createElement("fechaEmision");
	        fechaEmision.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmision()));
	        infoDocu.appendChild(fechaEmision);
        }

        if (emite.getInfEmisor().getDireccionEstablecimiento().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirEstablecimiento.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirEstablecimiento = doc.createElement("dirEstablecimiento");
		        dirEstablecimiento.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionEstablecimiento()));
		        infoDocu.appendChild(dirEstablecimiento);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tipoIdentificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTipoIdentificacion() != null){
	        Element tipoIdentificacionComprador = doc.createElement("tipoIdentificacionComprador");
	        tipoIdentificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getTipoIdentificacion()));
	        infoDocu.appendChild(tipoIdentificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.razonSocialComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("razonSocialComprador::"+emite.getInfEmisor().getRazonSocialComp());
	        if ((emite.getInfEmisor().getRazonSocialComp() != null) && 
	          (emite.getInfEmisor().getRazonSocialComp().length() > 0)) {
	          Element razonSocialComprador = doc.createElement("razonSocialComprador");
	          razonSocialComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocialComp()));
	          infoDocu.appendChild(razonSocialComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.identificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("identificacionComprador::"+emite.getInfEmisor().getIdentificacionComp());
	        if ((emite.getInfEmisor().getIdentificacionComp() != null) && 
	          (emite.getInfEmisor().getIdentificacionComp().length() > 0)) {
	          Element identificacionComprador = doc.createElement("identificacionComprador");
	          identificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getIdentificacionComp()));
	          infoDocu.appendChild(identificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.contribuyenteEspecial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getContribEspecial() > 99) &&(emite.getInfEmisor().getContribEspecial() <= 99999))
	        {
	  	      Element contribuyenteEspecial = doc.createElement("contribuyenteEspecial");
	  	      contribuyenteEspecial.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getContribEspecial()).toString()));
	  	      infoDocu.appendChild(contribuyenteEspecial);
	        }
        }
        
        if (emite.getInfEmisor().getObligContabilidad().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.obligadoContabilidad.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element obligadoContabilidad = doc.createElement("obligadoContabilidad");
    	        obligadoContabilidad.appendChild(doc.createTextNode(emite.getInfEmisor().getObligContabilidad()));
    	        infoDocu.appendChild(obligadoContabilidad);
            }
        }                        

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.guiaRemision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getGuiaRemision() != null) && 
	          (emite.getInfEmisor().getGuiaRemision().length() > 0)) {
	          Element guiaRemision = doc.createElement("guiaRemision");
	          guiaRemision.appendChild(doc.createTextNode(emite.getInfEmisor().getGuiaRemision()));
	          infoDocu.appendChild(guiaRemision);
	        }
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.rise.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	  if (emite.getInfEmisor().getRise().length()>0){
		          Element rise = doc.createElement("rise");
		          rise.appendChild(doc.createTextNode(emite.getInfEmisor().getRise()));
		          infoDocu.appendChild(rise);
        	  }
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.codDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element codDocModificado = doc.createElement("codDocModificado");
	          codDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocModificado()));
	          infoDocu.appendChild(codDocModificado);
        }        

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.numDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element numDocModificado = doc.createElement("numDocModificado");
	          numDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getNumDocModificado()));
	          infoDocu.appendChild(numDocModificado);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmisionDocSustento.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmisionDocSustento = doc.createElement("fechaEmisionDocSustento");
	        fechaEmisionDocSustento.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmisionDoc()));
	        infoDocu.appendChild(fechaEmisionDocSustento);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalSinImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTotalSinImpuestos() >= 0.0D) {
	          Element totalSinImpuestos = doc.createElement("totalSinImpuestos");
	          totalSinImpuestos.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getTotalSinImpuestos()).toString()));
	          infoDocu.appendChild(totalSinImpuestos);
	        }
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalDescuento.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTotalDescuento() >= 0.0D) {
	          Element TotalDescuento = doc.createElement("totalDescuento");
	          TotalDescuento.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getTotalDescuento()).toString()));
	          infoDocu.appendChild(TotalDescuento);
	        }
        }
        
       
        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.valorModificacion.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getValorModificado() >= 0.0D) {
	          Element valorModificacion = doc.createElement("valorModificacion");
	          valorModificacion.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getValorModificado()).toString()));
	          infoDocu.appendChild(valorModificacion);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        if (emite.getInfEmisor().getMoneda().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){
		          Element moneda = doc.createElement("moneda");
		          moneda.appendChild(doc.createTextNode(emite.getInfEmisor().getMoneda()));
		          infoDocu.appendChild(moneda);
	        }
        }
        }
        
        
        //////////////////////totalConImpuestos////////////////////////////
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalConImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        	        
        Element totalConImpuestos = doc.createElement("totalConImpuestos");
        infoDocu.appendChild(totalConImpuestos);        
        if (emite.getInfEmisor().getListDetDetImpuestos().size()>0){        
        System.out.println("Size::" + emite.getInfEmisor().getListDetDetImpuestos().size());
        for (DetalleTotalImpuestos e : emite.getInfEmisor().getListDetDetImpuestos())
        {
          Element totalImpuesto = doc.createElement("totalImpuesto");
          totalConImpuestos.appendChild(totalImpuesto);
          
          Element impuestoCodigo = doc.createElement("codigo");
          impuestoCodigo.appendChild(doc.createTextNode(new Integer(e.getCodTotalImpuestos()).toString()));
          totalImpuesto.appendChild(impuestoCodigo);
          
          Element impuestoCodigoPorcentaje = doc.createElement("codigoPorcentaje");
          impuestoCodigoPorcentaje.appendChild(doc.createTextNode(new Integer(e.getCodPorcentImp()).toString()));
          totalImpuesto.appendChild(impuestoCodigoPorcentaje);

          Element impuestoBaseImponible = doc.createElement("baseImponible");
          impuestoBaseImponible.appendChild(doc.createTextNode(new Double(e.getBaseImponibleImp()).toString()));
          totalImpuesto.appendChild(impuestoBaseImponible);

          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tarifa.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){          
	          Element impuestoTarifa = doc.createElement("tarifa");
	          impuestoTarifa.appendChild(doc.createTextNode(new Double(e.getTarifaImp()).toString()));
	          totalImpuesto.appendChild(impuestoTarifa);
          }

          Element impuestoValor = doc.createElement("valor");
          impuestoValor.appendChild(doc.createTextNode(new Double(e.getValorImp()).toString()));
          totalImpuesto.appendChild(impuestoValor);
          
          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.totalConImpuestos.moneda.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){
          if (emite.getInfEmisor().getMoneda().length()>0){
  	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
  	        if (getContextDocu(tipoDocumento,formatTipDoc)){
  		          Element moneda = doc.createElement("moneda");
  		          moneda.appendChild(doc.createTextNode(emite.getInfEmisor().getMoneda()));
  		          infoDocu.appendChild(moneda);
  	        }
          }
          }
        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.motivo.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element motivo = doc.createElement("motivo");
	          motivo.appendChild(doc.createTextNode(emite.getInfEmisor().getMotivo()));
	          infoDocu.appendChild(motivo);
        }
        
       
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.propina.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element propina = doc.createElement("propina");
	        propina.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getPropina()).toString()));
	        infoDocu.appendChild(propina);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.importeTotal.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){
		        Element importeTotal = doc.createElement("importeTotal");
		        importeTotal.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getImporteTotal()).toString()));
		        infoDocu.appendChild(importeTotal);
	        }
	        
        }
        //////////////////////////////////////////////////////////
        /////////////////////////Detalles/////////////////////////
        //////////////////////////////////////////////////////////
        /*
        public static ArrayList ListDetalleDocumento = new ArrayList();    
        public static ArrayList ListDocumentoImpuestos = new ArrayList();
        public static ArrayList ListDocumentoDetAdicionales = new ArrayList();
        */
        //ArrayList<DetalleDocumento> ListDetDocumentos = new ArrayList();
        //ListDetDocumentos = emite.getInfEmisor().getListDetDocumentos();
        int indexLineaCab = 0;
        int indexLineaDet = 0;
        if (ListDetalleDocumento != null){        
        if (ListDetalleDocumento.size() > 0){
	        Element detalles = doc.createElement("detalles");
	        document.appendChild(detalles);
	        for (int x=0;x<=ListDetalleDocumento.size()-1;x++){
		        DetalleDocumento DetDocTmp = new DetalleDocumento(); 
		        DetDocTmp = (DetalleDocumento)ListDetalleDocumento.get(x);
		        Element detalle = doc.createElement("detalle");
		        detalles.appendChild(detalle);
		        
		        indexLineaCab = DetDocTmp.getLineaFactura();
		        String ls_alias = "";
		        if (tipoDocumento.equals("04")){
		        	ls_alias = "codigoInterno";
		        }else{	
		        	ls_alias = "codigoPrincipal";
		        }
		        Element codigoPrincipal = doc.createElement(ls_alias);
		        codigoPrincipal.appendChild(doc.createTextNode(DetDocTmp.getCodigoPrincipal()));
		        detalle.appendChild(codigoPrincipal);
		        
		        if (tipoDocumento.equals("04")){
		        	ls_alias = "codigoAdicional";
		        }else{	
		        	ls_alias = "codigoAuxiliar";
		        }
		        if (DetDocTmp.getCodigoAuxiliar().length()>0){
		        Element codigoAuxiliar = doc.createElement(ls_alias);
		        codigoAuxiliar.appendChild(doc.createTextNode(DetDocTmp.getCodigoAuxiliar()));
		        detalle.appendChild(codigoAuxiliar);
		        }
		        
		        Element descripcion = doc.createElement("descripcion");
		        descripcion.appendChild(doc.createTextNode(DetDocTmp.getDescripcion()));
		        detalle.appendChild(descripcion);
		        
		        Element cantidad = doc.createElement("cantidad");
		        cantidad.appendChild(doc.createTextNode(new Double(DetDocTmp.getCantidad()).toString()));
		        detalle.appendChild(cantidad);
		        
		        Element precioUnitario = doc.createElement("precioUnitario");
		        precioUnitario.appendChild(doc.createTextNode(new Double(DetDocTmp.getPrecioUnitario()).toString()));
		        detalle.appendChild(precioUnitario);
		        
		        Element descuento = doc.createElement("descuento");
		        descuento.appendChild(doc.createTextNode(new Double(DetDocTmp.getDescuento()).toString()));
		        detalle.appendChild(descuento);
		        
		        Element precioTotalSinImpuesto = doc.createElement("precioTotalSinImpuesto");
		        precioTotalSinImpuesto.appendChild(doc.createTextNode(new Double(DetDocTmp.getPrecioTotalSinImpuesto()).toString()));
		        detalle.appendChild(precioTotalSinImpuesto);
	        
	        	//ArrayList<DetallesAdicionales> ListDetAdic = new ArrayList();
	        	//ListDetAdic = DetDocTmp.getListDetAdicionalesDocumentos();
	        	if (ListDocumentoDetAdicionales!=null){
	        	if (ListDocumentoDetAdicionales.size()>0){
		        	for (int y=0;y<=ListDocumentoDetAdicionales.size()-1;y++){
		        		DetallesAdicionales DetAdic = new DetallesAdicionales();
		        		DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(y);		        		
		        		indexLineaDet = DetAdic.getLineaFactura();		        		
		        		if (indexLineaDet == indexLineaCab){
			        		Element detalleAdic = doc.createElement("detallesAdicionales");
			    	        detalle.appendChild(detalleAdic);
			    	        
			    	        Element detAdicional = doc.createElement("detAdicional"); 
			    	        attr = doc.createAttribute("nombre");
			    	        attr.setValue(DetAdic.getNombre());
			    	        detAdicional.setAttributeNode(attr);
			    	        
			    	        attr = doc.createAttribute("valor");
			    	        attr.setValue(DetAdic.getValor());
			    	        detAdicional.setAttributeNode(attr);
			    	        
			    	        detalleAdic.appendChild(detAdicional);
		        		}
		        	}
	        	}
	        	}
	        	//ArrayList<DocumentoImpuestos> ListDocImp = new ArrayList();	        	
	        	//ListDocImp = DetDocTmp.getListDetImpuestosDocumentos();
	        	if (ListDocumentoImpuestos!=null){
	        	if (ListDocumentoImpuestos.size()>0){
	        	Element impuestos = doc.createElement("impuestos");
	            detalle.appendChild(impuestos);
	            
	        	for (int z=0;z<=ListDocumentoImpuestos.size()-1;z++){
	        		
	        	   DocumentoImpuestos DocImp = new DocumentoImpuestos(); 
	        	   DocImp = (DocumentoImpuestos)ListDocumentoImpuestos.get(z);
	        	   indexLineaDet = DocImp.getLineaFactura();		        		
	        	   if (indexLineaDet == indexLineaCab){
		     	       Element impuesto = doc.createElement("impuesto");
		     	       impuestos.appendChild(impuesto);	  
		     	        
		     	       	     	       
		     	       Element codigo = doc.createElement("codigo");
		     	       codigo.appendChild(doc.createTextNode(new Integer(DocImp.getImpuestoCodigo()).toString()));
		     	       impuesto.appendChild(codigo);
		     	       
		     	       
		     	       Element codigoPorcentaje = doc.createElement("codigoPorcentaje");
		     	       codigoPorcentaje.appendChild(doc.createTextNode(new Integer(DocImp.getImpuestoCodigoPorcentaje()).toString()));
		    	       impuesto.appendChild(codigoPorcentaje);
		    	       
		    	       Element tarifa = doc.createElement("tarifa");
		    	       tarifa.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoTarifa()).toString()));
		    	       impuesto.appendChild(tarifa);
		    	       
		    	       Element baseImponible = doc.createElement("baseImponible");
		    	       baseImponible.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoBaseImponible()).toString()));
		    	       impuesto.appendChild(baseImponible);
		    	       
		    	       Element valor = doc.createElement("valor");
		    	       valor.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoValor()).toString()));
		    	       impuesto.appendChild(valor);
	        	   }
	        	}
	            }
	        	}
	        //JZURITA DETALLE
	        }
        }
        }
        /*
         for (int j = 0; j <= ListDetalleDocumento.size() - 1; j++) {
	 	             // Añade cada nombre al resultado
	 		    	  DetalleDocumento detDocumentImp2 = new DetalleDocumento();
	 		    	  detDocumentImp2 = (DetalleDocumento)ListDetalleDocumento.get(j);
	 			 	  
	 		    	  System.out.println("getCodigoPrincipal::"+detDocumentImp2.getCodigoPrincipal());
	 		    	  System.out.println("getCodigoAuxiliar::"+detDocumentImp2.getCodigoAuxiliar());
	 		    	  System.out.println("getCantidad::"+detDocumentImp2.getCantidad());
	 		    	  System.out.println("getDescripcion::"+detDocumentImp2.getDescripcion());
	 		    	  
	 		    	 ArrayList<DocumentoImpuestos> ListDocImp2 = new ArrayList();
	 		    	 ListDocImp2 = detDocumentImp2.getListDetImpuestosDocumentos();
	 		    	 for (int k = 0; k <= ListDocImp2.size() - 1; k++) {
	 		    		DocumentoImpuestos DocImp2 = new DocumentoImpuestos();
	 		    		DocImp2 = (DocumentoImpuestos)ListDocImp2.get(k);
	 		    		System.out.println("getImpuestoCodigo::"+DocImp2.getImpuestoCodigo());
	 		    		System.out.println("getImpuestoCodigoPorcentaje::"+DocImp2.getImpuestoCodigoPorcentaje());
	 		    		System.out.println("getImpuestoTarifa::"+DocImp2.getImpuestoTarifa());
	 		    		System.out.println("getImpuestoBaseImponible::"+DocImp2.getImpuestoBaseImponible());
	 		    		System.out.println("getImpuestoValor::"+DocImp2.getImpuestoValor());
	 		    	 }
	 		    	 
	 		    	ArrayList<DetallesAdicionales> ListDetAdic2 = new ArrayList();
	 		    	ListDetAdic2 = detDocumentImp2.getListDetAdicionalesDocumentos();
	 		    	for (int k = 0; k <= ListDetAdic2.size() - 1; k++) {
	 		    		DetallesAdicionales DetAdic2 = new DetallesAdicionales();
	 		    		DetAdic2 = ListDetAdic2.get(k);
	 		    		System.out.println("getNombre::"+DetAdic2.getNombre());
	 		    		System.out.println("getValor::"+DetAdic2.getValor());
	 		    	}
	 	        }
         * */
        
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      if (nameOriginal != null){    	
    	  ruta = nameOriginal.replace("txt", "xml");
      }
      File file = new File(ruta);
      StreamResult result = new StreamResult(file);
      //result =  new StreamResult(System.out);
      transformer.transform(source, result);      
      System.out.println("File saved!\n"+ruta);
      return file.getName();
      /*
      String respuestaFirma = null;
      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
      ArchivoUtils arcFirmar = new ArchivoUtils();      
      emi.setRuc("0992531940001");
      
      System.out.println("ruta::" + ruta);
      System.out.println("file"+file.getName());
      fileFirmado = file.getName();
      rutaFirmado = emite.getInfEmisor().get_pathFirmados();
      respuestaFirma = ArchivoUtils.firmarArchivo(emi, ruta, rutaFirmado, "BCE_IKEY2032", null);
      
      if (respuestaFirma == null)
      {
        System.out.println("Firmado OK::"+respuestaFirma);
      }
      else System.out.println("Firmado Error::" + respuestaFirma);
      */      
      //return true;
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
      return "";
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    /*} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();*/
	}return "";
    

  }
 
  public static String notaDebito(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	
	  String fileFirmado = "";
      String rutaFirmado = "";
    try {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        ArrayList ListDetDocumentosTmp = new ArrayList();

        String ls_tipo_documento = "";
        Attr attr = null;
        
        //Obtencion del Tab Principal del XML para el tipo de Documento.
        ls_tipo_documento = Environment.c.getString("facElectronica.tiposDocumento.doc"+tipoDocumento);        
        //////////////////////Cabecera Xml///////////////////////////////
        Document doc = docBuilder.newDocument();
        Element document = doc.createElement(ls_tipo_documento);
        doc.appendChild(document);
        attr = doc.createAttribute("id");
        attr.setValue("comprobante");
        document.setAttributeNode(attr);
        String version;
        if ((emite.getInfEmisor().getVersion() != null) || (emite.getInfEmisor().getVersion().equals("")))
        {  version = emite.getInfEmisor().getVersion();		}
        else{ 	version = "1.0.0";	}
        attr = doc.createAttribute("version");
        attr.setValue(version);
        document.setAttributeNode(attr);   
        //////////////////////Cabecera Xml///////////////////////////////
        
        //////////////////////infoTributaria////////////////////////////
        Element infoTrib = doc.createElement("infoTributaria");
        document.appendChild(infoTrib);
                
        List formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ambiente.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element ambiente = doc.createElement("ambiente");
	        ambiente.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getAmbiente()).toString()));
	        infoTrib.appendChild(ambiente);
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.tipoEmision.documentos");
        //Obligatorio, conforme tabla 2 Numérico 1
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element tipoEmision = doc.createElement("tipoEmision");
        tipoEmision.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getTipoEmision()).toString()));
        infoTrib.appendChild(tipoEmision);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.razonSocial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	Element razonSocial = doc.createElement("razonSocial");
        	razonSocial.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocial()));
        	infoTrib.appendChild(razonSocial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.nombreComercial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element nombreComercial = doc.createElement("nombreComercial");
        nombreComercial.appendChild(doc.createTextNode(emite.getInfEmisor().getNombreComercial()));
        infoTrib.appendChild(nombreComercial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ruc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ruc = doc.createElement("ruc");
        ruc.appendChild(doc.createTextNode(emite.getInfEmisor().getRuc()));
        infoTrib.appendChild(ruc);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.claveAcceso.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	String ls_clave_acceso = "";
    		try {
    			ls_clave_acceso = generarClaveAcceso(emite);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        		        	
            if (ls_clave_acceso.length()!=49){
            	try {
    				ls_clave_acceso = generarClaveAcceso(emite);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            emite.getInfEmisor().setClaveAcceso(ls_clave_acceso);
            System.out.println("ClaveAcceso::" + ls_clave_acceso);
        Element claveAcceso = doc.createElement("claveAcceso");
        claveAcceso.appendChild(doc.createTextNode(ls_clave_acceso));
        infoTrib.appendChild(claveAcceso);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.codDoc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element codDoc = doc.createElement("codDoc");
        codDoc.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocumento()));
        infoTrib.appendChild(codDoc);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.estab.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element estab = doc.createElement("estab");
        estab.appendChild(doc.createTextNode(emite.getInfEmisor().getCodEstablecimiento()));
        infoTrib.appendChild(estab);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ptoEmi.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ptoEmi = doc.createElement("ptoEmi");
        ptoEmi.appendChild(doc.createTextNode(emite.getInfEmisor().getCodPuntoEmision()));
        infoTrib.appendChild(ptoEmi);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.secuencial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element secuencial = doc.createElement("secuencial");
        secuencial.appendChild(doc.createTextNode(emite.getInfEmisor().getSecuencial()));
        infoTrib.appendChild(secuencial);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.dirMatriz.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
        Element dirMatriz = doc.createElement("dirMatriz");
        dirMatriz.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionMatriz()));
        infoTrib.appendChild(dirMatriz);          
        }
        //////////////////////infoTributaria////////////////////////////        
        //////////////////////infoFactura////////////////////////////
        String ls_info_documento = Environment.c.getString("facElectronica.infoDocumento.doc"+tipoDocumento);
        Element infoDocu = doc.createElement("info"+ls_info_documento);
        document.appendChild(infoDocu);
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmision = doc.createElement("fechaEmision");
	        fechaEmision.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmision()));
	        infoDocu.appendChild(fechaEmision);
        }

        if (emite.getInfEmisor().getDireccionEstablecimiento().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirEstablecimiento.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirEstablecimiento = doc.createElement("dirEstablecimiento");
		        dirEstablecimiento.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionEstablecimiento()));
		        infoDocu.appendChild(dirEstablecimiento);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tipoIdentificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTipoIdentificacion() != null){
	        Element tipoIdentificacionComprador = doc.createElement("tipoIdentificacionComprador");
	        tipoIdentificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getTipoIdentificacion()));
	        infoDocu.appendChild(tipoIdentificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.razonSocialComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("razonSocialComprador::"+emite.getInfEmisor().getRazonSocialComp());
	        if ((emite.getInfEmisor().getRazonSocialComp() != null) && 
	          (emite.getInfEmisor().getRazonSocialComp().length() > 0)) {
	          Element razonSocialComprador = doc.createElement("razonSocialComprador");
	          razonSocialComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocialComp()));
	          infoDocu.appendChild(razonSocialComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.identificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("identificacionComprador::"+emite.getInfEmisor().getIdentificacionComp());
	        if ((emite.getInfEmisor().getIdentificacionComp() != null) && 
	          (emite.getInfEmisor().getIdentificacionComp().length() > 0)) {
	          Element identificacionComprador = doc.createElement("identificacionComprador");
	          identificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getIdentificacionComp()));
	          infoDocu.appendChild(identificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.contribuyenteEspecial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getContribEspecial() > 99) &&(emite.getInfEmisor().getContribEspecial() <= 99999))
	        {
	  	      Element contribuyenteEspecial = doc.createElement("contribuyenteEspecial");
	  	      contribuyenteEspecial.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getContribEspecial()).toString()));
	  	      infoDocu.appendChild(contribuyenteEspecial);
	        }
        }
        
        if (emite.getInfEmisor().getObligContabilidad().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.obligadoContabilidad.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element obligadoContabilidad = doc.createElement("obligadoContabilidad");
    	        obligadoContabilidad.appendChild(doc.createTextNode(emite.getInfEmisor().getObligContabilidad()));
    	        infoDocu.appendChild(obligadoContabilidad);
            }
        }                        

        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.rise.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	  if (emite.getInfEmisor().getRise().length()>0){
		          Element rise = doc.createElement("rise");
		          rise.appendChild(doc.createTextNode(emite.getInfEmisor().getRise()));
		          infoDocu.appendChild(rise);
        	  }
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.codDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element codDocModificado = doc.createElement("codDocModificado");
	          codDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocModificado()));
	          infoDocu.appendChild(codDocModificado);
        }        

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.numDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element numDocModificado = doc.createElement("numDocModificado");
	          numDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getNumDocModificado()));
	          infoDocu.appendChild(numDocModificado);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmisionDocSustento.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmisionDocSustento = doc.createElement("fechaEmisionDocSustento");
	        fechaEmisionDocSustento.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmisionDoc()));
	        infoDocu.appendChild(fechaEmisionDocSustento);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalSinImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTotalSinImpuestos() >= 0.0D) {
	          Element totalSinImpuestos = doc.createElement("totalSinImpuestos");
	          totalSinImpuestos.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getTotalSinImpuestos()).toString()));
	          infoDocu.appendChild(totalSinImpuestos);
	        }
        }

        
        //////////////////////totalConImpuestos////////////////////////////
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalConImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        	        
        Element totalConImpuestos = doc.createElement("totalConImpuestos");
        infoDocu.appendChild(totalConImpuestos);        
        if (emite.getInfEmisor().getListDetDetImpuestos().size()>0){        
        System.out.println("Size::" + emite.getInfEmisor().getListDetDetImpuestos().size());
        for (DetalleTotalImpuestos e : emite.getInfEmisor().getListDetDetImpuestos())
        {
          Element totalImpuesto = doc.createElement("totalImpuesto");
          totalConImpuestos.appendChild(totalImpuesto);
          
          Element impuestoCodigo = doc.createElement("codigo");
          impuestoCodigo.appendChild(doc.createTextNode(new Integer(e.getCodTotalImpuestos()).toString()));
          totalImpuesto.appendChild(impuestoCodigo);
          
          Element impuestoCodigoPorcentaje = doc.createElement("codigoPorcentaje");
          impuestoCodigoPorcentaje.appendChild(doc.createTextNode(new Integer(e.getCodPorcentImp()).toString()));
          totalImpuesto.appendChild(impuestoCodigoPorcentaje);

          Element impuestoBaseImponible = doc.createElement("baseImponible");
          impuestoBaseImponible.appendChild(doc.createTextNode(new Double(e.getBaseImponibleImp()).toString()));
          totalImpuesto.appendChild(impuestoBaseImponible);

          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tarifa.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){          
	          Element impuestoTarifa = doc.createElement("tarifa");
	          impuestoTarifa.appendChild(doc.createTextNode(new Double(e.getTarifaImp()).toString()));
	          totalImpuesto.appendChild(impuestoTarifa);
          }

          Element impuestoValor = doc.createElement("valor");
          impuestoValor.appendChild(doc.createTextNode(new Double(e.getValorImp()).toString()));
          totalImpuesto.appendChild(impuestoValor);          
        }
        }

        //Revisar JZU
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.valorTotal.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){
		        Element importeTotal = doc.createElement("valorTotal");
		        importeTotal.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getImporteTotal()).toString()));
		        infoDocu.appendChild(importeTotal);
	        }
	        
        }
		//////////////////////////////////////////////////////////
		/////////////////////////Motivos//////////////////////////
		//////////////////////////////////////////////////////////
        //ListInformacionMotivo
        Element motivos = doc.createElement("motivos");
        document.appendChild(motivos);
        if (ListInformacionMotivo!=null){
        	if (ListInformacionMotivo.size()>0){
        		for (int y=0;y<=ListInformacionMotivo.size()-1;y++){
        			InformacionAdicional MotInf = new InformacionAdicional();
        			MotInf = (InformacionAdicional) ListInformacionMotivo.get(y);
        			
        			Element motivo = doc.createElement("motivo");
        	        document.appendChild(motivo);
        			
        			Element razon = doc.createElement("razon");
        			razon.appendChild(doc.createTextNode(MotInf.getName()));
        			motivo.appendChild(razon);
        			
        			Element valor = doc.createElement("valor");
        			valor.appendChild(doc.createTextNode(MotInf.getValue()));
        			motivo.appendChild(valor);
        		}
        	}
        }        
		//////////////////////////////////////////////////////////
		/////////////////Informacion Adicional////////////////////
		//////////////////////////////////////////////////////////
        Element infoAdicional = doc.createElement("infoAdicional");
        document.appendChild(infoAdicional);
        if (ListDocumentoDetAdicionales!=null){
        	if (ListDocumentoDetAdicionales.size()>0){
	        	for (int y=0;y<=ListDocumentoDetAdicionales.size()-1;y++){
	        		DetallesAdicionales DetAdic = new DetallesAdicionales();
	        		DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(y);
		    	        Element campoAdicional = doc.createElement("campoAdicional"); 
		    	        attr = doc.createAttribute("nombre");
		    	        attr.setValue(DetAdic.getNombre());
		    	        campoAdicional.setAttributeNode(attr);
		    	        
		    	        attr = doc.createAttribute("valor");
		    	        attr.setValue(DetAdic.getValor());
		    	        campoAdicional.setAttributeNode(attr);		    	        
		    	        infoAdicional.appendChild(campoAdicional);
	        		
	        	}
        	}
        }                  
        
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      if (nameOriginal != null){    	
    	  ruta = nameOriginal.replace("txt", "xml");
      }
      File file = new File(ruta);
      StreamResult result = new StreamResult(file);
      //result =  new StreamResult(System.out);
      transformer.transform(source, result);      
      System.out.println("File saved!\n"+ruta);
      return file.getName();
      /*
      String respuestaFirma = null;
      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
      ArchivoUtils arcFirmar = new ArchivoUtils();      
      emi.setRuc("0992531940001");
      
      System.out.println("ruta::" + ruta);
      System.out.println("file"+file.getName());
      fileFirmado = file.getName();
      rutaFirmado = emite.getInfEmisor().get_pathFirmados();
      respuestaFirma = ArchivoUtils.firmarArchivo(emi, ruta, rutaFirmado, "BCE_IKEY2032", null);
      
      if (respuestaFirma == null)
      {
        System.out.println("Firmado OK::"+respuestaFirma);
      }
      else System.out.println("Firmado Error::" + respuestaFirma);
      */      
      //return true;
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
      return "";
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    /*} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();*/
	}return "";    
  }
  
  public static String factura(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	
	  String fileFirmado = "";
      String rutaFirmado = "";
    try {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        ArrayList ListDetDocumentosTmp = new ArrayList();

        String ls_tipo_documento = "";
        Attr attr = null;
        
        //Obtencion del Tab Principal del XML para el tipo de Documento.
        ls_tipo_documento = Environment.c.getString("facElectronica.tiposDocumento.doc"+tipoDocumento);
        
        //////////////////////Cabecera Xml///////////////////////////////
        Document doc = docBuilder.newDocument();
        Element document = doc.createElement(ls_tipo_documento);
        doc.appendChild(document);
        attr = doc.createAttribute("id");
        attr.setValue("comprobante");
        document.setAttributeNode(attr);
        String version;
        if ((emite.getInfEmisor().getVersion() != null) || (emite.getInfEmisor().getVersion().equals("")))
        {  version = emite.getInfEmisor().getVersion();		}
        else{ 	version = "1.0.0";	}
        attr = doc.createAttribute("version");
        attr.setValue(version);
        document.setAttributeNode(attr);   
        //////////////////////Cabecera Xml///////////////////////////////
        
        //////////////////////infoTributaria////////////////////////////
        Element infoTrib = doc.createElement("infoTributaria");
        document.appendChild(infoTrib);
                
        List formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ambiente.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element ambiente = doc.createElement("ambiente");
	        ambiente.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getAmbiente()).toString()));
	        infoTrib.appendChild(ambiente);
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.tipoEmision.documentos");
        //Obligatorio, conforme tabla 2 Numérico 1
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element tipoEmision = doc.createElement("tipoEmision");
        tipoEmision.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getTipoEmision()).toString()));
        infoTrib.appendChild(tipoEmision);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.razonSocial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	Element razonSocial = doc.createElement("razonSocial");
        	razonSocial.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocial()));
        	infoTrib.appendChild(razonSocial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.nombreComercial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element nombreComercial = doc.createElement("nombreComercial");
        nombreComercial.appendChild(doc.createTextNode(emite.getInfEmisor().getNombreComercial()));
        infoTrib.appendChild(nombreComercial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ruc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ruc = doc.createElement("ruc");
        ruc.appendChild(doc.createTextNode(emite.getInfEmisor().getRuc()));
        infoTrib.appendChild(ruc);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.claveAcceso.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	String ls_clave_acceso = "";
    		try {
    			ls_clave_acceso = generarClaveAcceso(emite);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        		        	
            if (ls_clave_acceso.length()!=49){
            	try {
    				ls_clave_acceso = generarClaveAcceso(emite);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            emite.getInfEmisor().setClaveAcceso(ls_clave_acceso);
            System.out.println("ClaveAcceso::" + ls_clave_acceso);
        Element claveAcceso = doc.createElement("claveAcceso");
        claveAcceso.appendChild(doc.createTextNode(ls_clave_acceso));
        infoTrib.appendChild(claveAcceso);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.codDoc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element codDoc = doc.createElement("codDoc");
        codDoc.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocumento()));
        infoTrib.appendChild(codDoc);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.estab.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element estab = doc.createElement("estab");
        estab.appendChild(doc.createTextNode(emite.getInfEmisor().getCodEstablecimiento()));
        infoTrib.appendChild(estab);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ptoEmi.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ptoEmi = doc.createElement("ptoEmi");
        ptoEmi.appendChild(doc.createTextNode(emite.getInfEmisor().getCodPuntoEmision()));
        infoTrib.appendChild(ptoEmi);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.secuencial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element secuencial = doc.createElement("secuencial");
        secuencial.appendChild(doc.createTextNode(emite.getInfEmisor().getSecuencial()));
        infoTrib.appendChild(secuencial);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.dirMatriz.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
        Element dirMatriz = doc.createElement("dirMatriz");
        dirMatriz.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionMatriz()));
        infoTrib.appendChild(dirMatriz);          
        }
        //////////////////////infoTributaria////////////////////////////        
        //////////////////////infoFactura////////////////////////////
        String ls_info_documento = Environment.c.getString("facElectronica.infoDocumento.doc"+tipoDocumento);
        Element infoDocu = doc.createElement("info"+ls_info_documento);
        document.appendChild(infoDocu);
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmision = doc.createElement("fechaEmision");
	        fechaEmision.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmision()));
	        infoDocu.appendChild(fechaEmision);
        }

        if (emite.getInfEmisor().getDireccionEstablecimiento().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirEstablecimiento.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirEstablecimiento = doc.createElement("dirEstablecimiento");
		        dirEstablecimiento.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionEstablecimiento()));
		        infoDocu.appendChild(dirEstablecimiento);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.contribuyenteEspecial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getContribEspecial() > 99) &&(emite.getInfEmisor().getContribEspecial() <= 99999))
	        {
	  	      Element contribuyenteEspecial = doc.createElement("contribuyenteEspecial");
	  	      contribuyenteEspecial.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getContribEspecial()).toString()));
	  	      infoDocu.appendChild(contribuyenteEspecial);
	        }
        }
        
        if (emite.getInfEmisor().getObligContabilidad().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.obligadoContabilidad.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element obligadoContabilidad = doc.createElement("obligadoContabilidad");
    	        obligadoContabilidad.appendChild(doc.createTextNode(emite.getInfEmisor().getObligContabilidad()));
    	        infoDocu.appendChild(obligadoContabilidad);
            }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tipoIdentificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTipoIdentificacion() != null){
	        Element tipoIdentificacionComprador = doc.createElement("tipoIdentificacionComprador");
	        tipoIdentificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getTipoIdentificacion()));
	        infoDocu.appendChild(tipoIdentificacionComprador);
	        }
        }
        

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.guiaRemision.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getGuiaRemision() != null) && 
	          (emite.getInfEmisor().getGuiaRemision().length() > 1)) {
	          Element guiaRemision = doc.createElement("guiaRemision");
	          guiaRemision.appendChild(doc.createTextNode(emite.getInfEmisor().getGuiaRemision()));
	          infoDocu.appendChild(guiaRemision);
	        }
        }
        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.razonSocialComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("razonSocialComprador::"+emite.getInfEmisor().getRazonSocialComp());
	        if ((emite.getInfEmisor().getRazonSocialComp() != null) && 
	          (emite.getInfEmisor().getRazonSocialComp().length() > 0)) {
	          Element razonSocialComprador = doc.createElement("razonSocialComprador");
	          razonSocialComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocialComp()));
	          infoDocu.appendChild(razonSocialComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.identificacionComprador.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
	        System.out.println("identificacionComprador::"+emite.getInfEmisor().getIdentificacionComp());
	        if ((emite.getInfEmisor().getIdentificacionComp() != null) && 
	          (emite.getInfEmisor().getIdentificacionComp().length() > 0)) {
	          Element identificacionComprador = doc.createElement("identificacionComprador");
	          identificacionComprador.appendChild(doc.createTextNode(emite.getInfEmisor().getIdentificacionComp()));
	          infoDocu.appendChild(identificacionComprador);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.rise.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	  if (emite.getInfEmisor().getRise().length()>0){
		          Element rise = doc.createElement("rise");
		          rise.appendChild(doc.createTextNode(emite.getInfEmisor().getRise()));
		          infoDocu.appendChild(rise);
        	  }
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.codDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element codDocModificado = doc.createElement("codDocModificado");
	          codDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocModificado()));
	          infoDocu.appendChild(codDocModificado);
        }        

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.numDocModificado.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element numDocModificado = doc.createElement("numDocModificado");
	          numDocModificado.appendChild(doc.createTextNode(emite.getInfEmisor().getNumDocModificado()));
	          infoDocu.appendChild(numDocModificado);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaEmisionDocSustento.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaEmisionDocSustento = doc.createElement("fechaEmisionDocSustento");
	        fechaEmisionDocSustento.appendChild(doc.createTextNode(emite.getInfEmisor().getFecEmisionDoc()));
	        infoDocu.appendChild(fechaEmisionDocSustento);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalSinImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTotalSinImpuestos() >= 0.0D) {
	          Element totalSinImpuestos = doc.createElement("totalSinImpuestos");
	          totalSinImpuestos.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getTotalSinImpuestos()).toString()));
	          infoDocu.appendChild(totalSinImpuestos);
	        }
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalDescuento.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getTotalDescuento() >= 0.0D) {
	          Element TotalDescuento = doc.createElement("totalDescuento");
	          TotalDescuento.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getTotalDescuento()).toString()));
	          infoDocu.appendChild(TotalDescuento);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.valorModificacion.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if (emite.getInfEmisor().getValorModificado() >= 0.0D) {
	          Element valorModificacion = doc.createElement("valorModificacion");
	          valorModificacion.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getValorModificado()).toString()));
	          infoDocu.appendChild(valorModificacion);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        if (emite.getInfEmisor().getMoneda().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){
		          Element moneda = doc.createElement("moneda");
		          moneda.appendChild(doc.createTextNode(emite.getInfEmisor().getMoneda()));
		          infoDocu.appendChild(moneda);
	        }
        }
        }
        
        
        //////////////////////totalConImpuestos////////////////////////////
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.totalConImpuestos.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        	        
        Element totalConImpuestos = doc.createElement("totalConImpuestos");
        infoDocu.appendChild(totalConImpuestos);        
        if (emite.getInfEmisor().getListDetDetImpuestos().size()>0){        
        System.out.println("Size::" + emite.getInfEmisor().getListDetDetImpuestos().size());
        for (DetalleTotalImpuestos e : emite.getInfEmisor().getListDetDetImpuestos())
        {
          Element totalImpuesto = doc.createElement("totalImpuesto");
          totalConImpuestos.appendChild(totalImpuesto);
          
          Element impuestoCodigo = doc.createElement("codigo");
          impuestoCodigo.appendChild(doc.createTextNode(new Integer(e.getCodTotalImpuestos()).toString()));
          totalImpuesto.appendChild(impuestoCodigo);
          
          Element impuestoCodigoPorcentaje = doc.createElement("codigoPorcentaje");
          impuestoCodigoPorcentaje.appendChild(doc.createTextNode(new Integer(e.getCodPorcentImp()).toString()));
          totalImpuesto.appendChild(impuestoCodigoPorcentaje);

          Element impuestoBaseImponible = doc.createElement("baseImponible");
          impuestoBaseImponible.appendChild(doc.createTextNode(new Double(e.getBaseImponibleImp()).toString()));
          totalImpuesto.appendChild(impuestoBaseImponible);

          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tarifa.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){          
	          Element impuestoTarifa = doc.createElement("tarifa");
	          impuestoTarifa.appendChild(doc.createTextNode(new Double(e.getTarifaImp()).toString()));
	          totalImpuesto.appendChild(impuestoTarifa);
          }

          Element impuestoValor = doc.createElement("valor");
          impuestoValor.appendChild(doc.createTextNode(new Double(e.getValorImp()).toString()));
          totalImpuesto.appendChild(impuestoValor);
          
          formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.totalConImpuestos.moneda.documentos");
          if (getContextDocu(tipoDocumento,formatTipDoc)){
          if (emite.getInfEmisor().getMoneda().length()>0){
  	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.moneda.documentos");
  	        if (getContextDocu(tipoDocumento,formatTipDoc)){
  		          Element moneda = doc.createElement("moneda");
  		          moneda.appendChild(doc.createTextNode(emite.getInfEmisor().getMoneda()));
  		          infoDocu.appendChild(moneda);
  	        }
          }
          }
        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.motivo.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	          Element motivo = doc.createElement("motivo");
	          motivo.appendChild(doc.createTextNode(emite.getInfEmisor().getMotivo()));
	          infoDocu.appendChild(motivo);
        }
        
       
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.propina.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element propina = doc.createElement("propina");
	        propina.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getPropina()).toString()));
	        infoDocu.appendChild(propina);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.importeTotal.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){
		        Element importeTotal = doc.createElement("importeTotal");
		        importeTotal.appendChild(doc.createTextNode(new Double(emite.getInfEmisor().getImporteTotal()).toString()));
		        infoDocu.appendChild(importeTotal);
	        }
	        
        }
        //////////////////////////////////////////////////////////
        /////////////////////////Detalles/////////////////////////
        //////////////////////////////////////////////////////////
        /*
        public static ArrayList ListDetalleDocumento = new ArrayList();    
        public static ArrayList ListDocumentoImpuestos = new ArrayList();
        public static ArrayList ListDocumentoDetAdicionales = new ArrayList();
        */
        //ArrayList<DetalleDocumento> ListDetDocumentos = new ArrayList();
        //ListDetDocumentos = emite.getInfEmisor().getListDetDocumentos();
        int indexLineaCab = 0;
        int indexLineaDet = 0;
        if (ListDetalleDocumento != null){        
        if (ListDetalleDocumento.size() > 0){
	        Element detalles = doc.createElement("detalles");
	        document.appendChild(detalles);
	        for (int x=0;x<=ListDetalleDocumento.size()-1;x++){
		        DetalleDocumento DetDocTmp = new DetalleDocumento(); 
		        DetDocTmp = (DetalleDocumento)ListDetalleDocumento.get(x);
		        Element detalle = doc.createElement("detalle");
		        detalles.appendChild(detalle);
		        
		        indexLineaCab = DetDocTmp.getLineaFactura();
		        String ls_alias = "";
		        if (tipoDocumento.equals("04")){
		        	ls_alias = "codigoInterno";
		        }else{	
		        	ls_alias = "codigoPrincipal";
		        }
		        Element codigoPrincipal = doc.createElement(ls_alias);
		        codigoPrincipal.appendChild(doc.createTextNode(DetDocTmp.getCodigoPrincipal()));
		        detalle.appendChild(codigoPrincipal);
		        
		        if (tipoDocumento.equals("04")){
		        	ls_alias = "codigoAdicional";
		        }else{	
		        	ls_alias = "codigoAuxiliar";
		        }
		        if (DetDocTmp.getCodigoAuxiliar().length()>0){
		        Element codigoAuxiliar = doc.createElement(ls_alias);
		        codigoAuxiliar.appendChild(doc.createTextNode(DetDocTmp.getCodigoAuxiliar()));
		        detalle.appendChild(codigoAuxiliar);
		        }
		        
		        Element descripcion = doc.createElement("descripcion");
		        descripcion.appendChild(doc.createTextNode(DetDocTmp.getDescripcion()));
		        detalle.appendChild(descripcion);
		        
		        Element cantidad = doc.createElement("cantidad");
		        cantidad.appendChild(doc.createTextNode(new Double(DetDocTmp.getCantidad()).toString()));
		        detalle.appendChild(cantidad);
		        
		        Element precioUnitario = doc.createElement("precioUnitario");
		        precioUnitario.appendChild(doc.createTextNode(new Double(DetDocTmp.getPrecioUnitario()).toString()));
		        detalle.appendChild(precioUnitario);
		        
		        Element descuento = doc.createElement("descuento");
		        descuento.appendChild(doc.createTextNode(new Double(DetDocTmp.getDescuento()).toString()));
		        detalle.appendChild(descuento);
		        
		        Element precioTotalSinImpuesto = doc.createElement("precioTotalSinImpuesto");
		        precioTotalSinImpuesto.appendChild(doc.createTextNode(new Double(DetDocTmp.getPrecioTotalSinImpuesto()).toString()));
		        detalle.appendChild(precioTotalSinImpuesto);
	        
	        	//ArrayList<DetallesAdicionales> ListDetAdic = new ArrayList();
	        	//ListDetAdic = DetDocTmp.getListDetAdicionalesDocumentos();
	        	if (ListDocumentoDetAdicionales!=null){
	        	if (ListDocumentoDetAdicionales.size()>0){
		        	for (int y=0;y<=ListDocumentoDetAdicionales.size()-1;y++){
		        		DetallesAdicionales DetAdic = new DetallesAdicionales();
		        		DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(y);		        		
		        		indexLineaDet = DetAdic.getLineaFactura();		        		
		        		if (indexLineaDet == indexLineaCab){
			        		Element detalleAdic = doc.createElement("detallesAdicionales");
			    	        detalle.appendChild(detalleAdic);
			    	        
			    	        Element detAdicional = doc.createElement("detAdicional"); 
			    	        attr = doc.createAttribute("nombre");
			    	        attr.setValue(DetAdic.getNombre());
			    	        detAdicional.setAttributeNode(attr);
			    	        
			    	        attr = doc.createAttribute("valor");
			    	        attr.setValue(DetAdic.getValor());
			    	        detAdicional.setAttributeNode(attr);
			    	        
			    	        detalleAdic.appendChild(detAdicional);
		        		}
		        	}
	        	}
	        	}
	        	//ArrayList<DocumentoImpuestos> ListDocImp = new ArrayList();	        	
	        	//ListDocImp = DetDocTmp.getListDetImpuestosDocumentos();
	        	if (ListDocumentoImpuestos!=null){
	        	if (ListDocumentoImpuestos.size()>0){
	        	Element impuestos = doc.createElement("impuestos");
	            detalle.appendChild(impuestos);
	            
	        	for (int z=0;z<=ListDocumentoImpuestos.size()-1;z++){
	        		
	        	   DocumentoImpuestos DocImp = new DocumentoImpuestos(); 
	        	   DocImp = (DocumentoImpuestos)ListDocumentoImpuestos.get(z);
	        	   indexLineaDet = DocImp.getLineaFactura();		        		
	        	   if (indexLineaDet == indexLineaCab){
		     	       Element impuesto = doc.createElement("impuesto");
		     	       impuestos.appendChild(impuesto);	  
		     	        
		     	       	     	       
		     	       Element codigo = doc.createElement("codigo");
		     	       codigo.appendChild(doc.createTextNode(new Integer(DocImp.getImpuestoCodigo()).toString()));
		     	       impuesto.appendChild(codigo);
		     	       
		     	       
		     	       Element codigoPorcentaje = doc.createElement("codigoPorcentaje");
		     	       codigoPorcentaje.appendChild(doc.createTextNode(new Integer(DocImp.getImpuestoCodigoPorcentaje()).toString()));
		    	       impuesto.appendChild(codigoPorcentaje);
		    	       
		    	       Element tarifa = doc.createElement("tarifa");
		    	       tarifa.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoTarifa()).toString()));
		    	       impuesto.appendChild(tarifa);
		    	       
		    	       Element baseImponible = doc.createElement("baseImponible");
		    	       baseImponible.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoBaseImponible()).toString()));
		    	       impuesto.appendChild(baseImponible);
		    	       
		    	       Element valor = doc.createElement("valor");
		    	       valor.appendChild(doc.createTextNode(new Double(DocImp.getImpuestoValor()).toString()));
		    	       impuesto.appendChild(valor);
	        	   }
	        	}
	            }
	        	}
	        //JZURITA DETALLE
	        }
        }
        }
        /*
         for (int j = 0; j <= ListDetalleDocumento.size() - 1; j++) {
	 	             // Añade cada nombre al resultado
	 		    	  DetalleDocumento detDocumentImp2 = new DetalleDocumento();
	 		    	  detDocumentImp2 = (DetalleDocumento)ListDetalleDocumento.get(j);
	 			 	  
	 		    	  System.out.println("getCodigoPrincipal::"+detDocumentImp2.getCodigoPrincipal());
	 		    	  System.out.println("getCodigoAuxiliar::"+detDocumentImp2.getCodigoAuxiliar());
	 		    	  System.out.println("getCantidad::"+detDocumentImp2.getCantidad());
	 		    	  System.out.println("getDescripcion::"+detDocumentImp2.getDescripcion());
	 		    	  
	 		    	 ArrayList<DocumentoImpuestos> ListDocImp2 = new ArrayList();
	 		    	 ListDocImp2 = detDocumentImp2.getListDetImpuestosDocumentos();
	 		    	 for (int k = 0; k <= ListDocImp2.size() - 1; k++) {
	 		    		DocumentoImpuestos DocImp2 = new DocumentoImpuestos();
	 		    		DocImp2 = (DocumentoImpuestos)ListDocImp2.get(k);
	 		    		System.out.println("getImpuestoCodigo::"+DocImp2.getImpuestoCodigo());
	 		    		System.out.println("getImpuestoCodigoPorcentaje::"+DocImp2.getImpuestoCodigoPorcentaje());
	 		    		System.out.println("getImpuestoTarifa::"+DocImp2.getImpuestoTarifa());
	 		    		System.out.println("getImpuestoBaseImponible::"+DocImp2.getImpuestoBaseImponible());
	 		    		System.out.println("getImpuestoValor::"+DocImp2.getImpuestoValor());
	 		    	 }
	 		    	 
	 		    	ArrayList<DetallesAdicionales> ListDetAdic2 = new ArrayList();
	 		    	ListDetAdic2 = detDocumentImp2.getListDetAdicionalesDocumentos();
	 		    	for (int k = 0; k <= ListDetAdic2.size() - 1; k++) {
	 		    		DetallesAdicionales DetAdic2 = new DetallesAdicionales();
	 		    		DetAdic2 = ListDetAdic2.get(k);
	 		    		System.out.println("getNombre::"+DetAdic2.getNombre());
	 		    		System.out.println("getValor::"+DetAdic2.getValor());
	 		    	}
	 	        }
         * */
        
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      if (nameOriginal == null){    	
    	  ruta = nameOriginal.replace("txt", "xml");
      }
      File file = new File(ruta);
      StreamResult result = new StreamResult(file);
      //result =  new StreamResult(System.out);
      transformer.transform(source, result);      
      System.out.println("File saved!\n"+ruta);
      try {
		emite.insertaBitacoraDocumento(emite.getInfEmisor().getAmbientePuntoEmision(), 
							    	   emite.getInfEmisor().getRuc(),
							    	   emite.getInfEmisor().getCodEstablecimiento(),
							    	   emite.getInfEmisor().getCodPuntoEmision(),
							    	   emite.getInfEmisor().getSecuencial(),
							    	   emite.getInfEmisor().getCodDocumento(),
							    	   emite.getInfEmisor().getFecEmision(),
							    	   "CX",
							    	   "Generacion del Archivo XML "+file.getName()+" del Proceso de Despacho",
							    	   "",
							    	   result.toString(),
							    	   "",
							    	   "",
									   "",
									   emite.getInfEmisor().getTipoEmision());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      return file.getName();
      /*
      String respuestaFirma = null;
      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
      ArchivoUtils arcFirmar = new ArchivoUtils();      
      emi.setRuc("0992531940001");
      
      System.out.println("ruta::" + ruta);
      System.out.println("file"+file.getName());
      fileFirmado = file.getName();
      rutaFirmado = emite.getInfEmisor().get_pathFirmados();
      respuestaFirma = ArchivoUtils.firmarArchivo(emi, ruta, rutaFirmado, "BCE_IKEY2032", null);
      
      if (respuestaFirma == null)
      {
        System.out.println("Firmado OK::"+respuestaFirma);
      }
      else System.out.println("Firmado Error::" + respuestaFirma);
      */      
      //return true;        
      
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
		     try {
				emite.insertaBitacoraDocumento(emite.getInfEmisor().getAmbientePuntoEmision(), 
				    	   emite.getInfEmisor().getRuc(),
				    	   emite.getInfEmisor().getCodEstablecimiento(),
				    	   emite.getInfEmisor().getCodPuntoEmision(),
				    	   emite.getInfEmisor().getSecuencial(),
				    	   emite.getInfEmisor().getCodDocumento(),
				    	   emite.getInfEmisor().getFecEmision(),
				    	   "CX",
				    	   "Error Generacion del Archivo XML del Proceso de Despacho"+ pce.toString(),
				    	   "",
				    	   "",
				    	   "",
				    	   "",
						   "",
						   emite.getInfEmisor().getTipoEmision());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      return "";
    } catch (TransformerException tfe) {
    	try {
			emite.insertaBitacoraDocumento(emite.getInfEmisor().getAmbientePuntoEmision(), 
			    	   emite.getInfEmisor().getRuc(),
			    	   emite.getInfEmisor().getCodEstablecimiento(),
			    	   emite.getInfEmisor().getCodPuntoEmision(),
			    	   emite.getInfEmisor().getSecuencial(),
			    	   emite.getInfEmisor().getCodDocumento(),
			    	   emite.getInfEmisor().getFecEmision(),
			    	   "CX",
			    	   "Error Generacion del Archivo XML del Proceso de Despacho"+ tfe.toString(),
			    	   "",
			    	   "",
			    	   "",
			    	   "",
					   "",
					   emite.getInfEmisor().getTipoEmision());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      tfe.printStackTrace();
	}return "";
    

  }
  
  
  public static String guiaRemision(String ruta, Emisor emite, String tipoDocumento, String nameOriginal)
  {
	
	  String fileFirmado = "";
      String rutaFirmado = "";
    try {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        ArrayList ListDetDocumentosTmp = new ArrayList();

        String ls_tipo_documento = "";
        Attr attr = null;
        
        //Obtencion del Tab Principal del XML para el tipo de Documento.
        ls_tipo_documento = Environment.c.getString("facElectronica.tiposDocumento.doc"+tipoDocumento);        
        
        
        //////////////////////Cabecera Xml///////////////////////////////
        Document doc = docBuilder.newDocument();
        Element document = doc.createElement(ls_tipo_documento);
        doc.appendChild(document);
        attr = doc.createAttribute("id");
        attr.setValue("comprobante");
        document.setAttributeNode(attr);
        String version;
        if ((emite.getInfEmisor().getVersion() != null) || (emite.getInfEmisor().getVersion().equals("")))
        {  version = emite.getInfEmisor().getVersion();		}
        else{ 	version = "1.0.0";	}
        attr = doc.createAttribute("version");
        attr.setValue(version);
        document.setAttributeNode(attr);   
        //////////////////////Cabecera Xml///////////////////////////////
        
        //////////////////////infoTributaria////////////////////////////
        Element infoTrib = doc.createElement("infoTributaria");
        document.appendChild(infoTrib);
                
        List formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ambiente.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element ambiente = doc.createElement("ambiente");
	        ambiente.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getAmbiente()).toString()));
	        infoTrib.appendChild(ambiente);
        }        
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.tipoEmision.documentos");
        //Obligatorio, conforme tabla 2 Numérico 1
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element tipoEmision = doc.createElement("tipoEmision");
        tipoEmision.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getTipoEmision()).toString()));
        infoTrib.appendChild(tipoEmision);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.razonSocial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	Element razonSocial = doc.createElement("razonSocial");
        	razonSocial.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocial()));
        	infoTrib.appendChild(razonSocial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.nombreComercial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element nombreComercial = doc.createElement("nombreComercial");
        nombreComercial.appendChild(doc.createTextNode(emite.getInfEmisor().getNombreComercial()));
        infoTrib.appendChild(nombreComercial);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ruc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ruc = doc.createElement("ruc");
        ruc.appendChild(doc.createTextNode(emite.getInfEmisor().getRuc()));
        infoTrib.appendChild(ruc);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.claveAcceso.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	String ls_clave_acceso = "";
    		try {
    			ls_clave_acceso = generarClaveAcceso(emite);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        		        	
            if (ls_clave_acceso.length()!=49){
            	try {
    				ls_clave_acceso = generarClaveAcceso(emite);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            emite.getInfEmisor().setClaveAcceso(ls_clave_acceso);
            System.out.println("ClaveAcceso::" + ls_clave_acceso);
        Element claveAcceso = doc.createElement("claveAcceso");
        claveAcceso.appendChild(doc.createTextNode(ls_clave_acceso));
        infoTrib.appendChild(claveAcceso);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.codDoc.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element codDoc = doc.createElement("codDoc");
        codDoc.appendChild(doc.createTextNode(emite.getInfEmisor().getCodDocumento()));
        infoTrib.appendChild(codDoc);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.estab.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element estab = doc.createElement("estab");
        estab.appendChild(doc.createTextNode(emite.getInfEmisor().getCodEstablecimiento()));
        infoTrib.appendChild(estab);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.ptoEmi.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element ptoEmi = doc.createElement("ptoEmi");
        ptoEmi.appendChild(doc.createTextNode(emite.getInfEmisor().getCodPuntoEmision()));
        infoTrib.appendChild(ptoEmi);
        }

        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.secuencial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        Element secuencial = doc.createElement("secuencial");
        secuencial.appendChild(doc.createTextNode(emite.getInfEmisor().getSecuencial()));
        infoTrib.appendChild(secuencial);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoTributaria.dirMatriz.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){        
        Element dirMatriz = doc.createElement("dirMatriz");
        dirMatriz.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionMatriz()));
        infoTrib.appendChild(dirMatriz);          
        }
        //////////////////////infoTributaria////////////////////////////        
        //////////////////////infoFactura////////////////////////////
        String ls_info_documento = Environment.c.getString("facElectronica.infoDocumento.doc"+tipoDocumento);
        Element infoDocu = doc.createElement("info"+ls_info_documento);
        document.appendChild(infoDocu);
        
        

        if (emite.getInfEmisor().getDireccionEstablecimiento().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirEstablecimiento.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirEstablecimiento = doc.createElement("dirEstablecimiento");
		        dirEstablecimiento.appendChild(doc.createTextNode(emite.getInfEmisor().getDireccionEstablecimiento()));
		        infoDocu.appendChild(dirEstablecimiento);
	        }
        }

        if (emite.getInfEmisor().getDirPartida().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.dirPartida.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element dirPartida = doc.createElement("dirPartida");
		        dirPartida.appendChild(doc.createTextNode(emite.getInfEmisor().getDirPartida()));
		        infoDocu.appendChild(dirPartida);
	        }
        }
        
        if (emite.getInfEmisor().getRazonSocTransp().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.razonSocialTransportista.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element razonSocialTransportista = doc.createElement("razonSocialTransportista");
		        razonSocialTransportista.appendChild(doc.createTextNode(emite.getInfEmisor().getRazonSocTransp()));
		        infoDocu.appendChild(razonSocialTransportista);
	        }
        }        
        
        if (emite.getInfEmisor().getTipoIdentTransp()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.tipoIdentificacionTransportista.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element tipoIdentificacionTransportista = doc.createElement("tipoIdentificacionTransportista");
		        tipoIdentificacionTransportista.appendChild(doc.createTextNode("0"+new Integer(emite.getInfEmisor().getTipoIdentTransp()).toString()));
		        infoDocu.appendChild(tipoIdentificacionTransportista);
	        }
        }
        
        if (emite.getInfEmisor().getRucTransp().length()>0){
	        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.rucTransportista.documentos");
	        if (getContextDocu(tipoDocumento,formatTipDoc)){        
		        Element rucTransportista = doc.createElement("rucTransportista");
		        rucTransportista.appendChild(doc.createTextNode(emite.getInfEmisor().getRucTransp()));
		        infoDocu.appendChild(rucTransportista);
	        }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.rise.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
        	  if (emite.getInfEmisor().getRise().length()>0){
		          Element rise = doc.createElement("rise");
		          rise.appendChild(doc.createTextNode(emite.getInfEmisor().getRise()));
		          infoDocu.appendChild(rise);
        	  }
        }
        
        if (emite.getInfEmisor().getObligContabilidad().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.obligadoContabilidad.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element obligadoContabilidad = doc.createElement("obligadoContabilidad");
    	        obligadoContabilidad.appendChild(doc.createTextNode((emite.getInfEmisor().getObligContabilidad().equals("S")?"SI":"NO")));
    	        infoDocu.appendChild(obligadoContabilidad);
            }
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.contribuyenteEspecial.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        if ((emite.getInfEmisor().getContribEspecial() > 99) &&(emite.getInfEmisor().getContribEspecial() <= 99999))
	        {
	  	      Element contribuyenteEspecial = doc.createElement("contribuyenteEspecial");
	  	      contribuyenteEspecial.appendChild(doc.createTextNode(new Integer(emite.getInfEmisor().getContribEspecial()).toString()));
	  	      infoDocu.appendChild(contribuyenteEspecial);
	        }
        }
                
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaIniTransporte.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaIniTransporte = doc.createElement("fechaIniTransporte");
	        fechaIniTransporte.appendChild(doc.createTextNode(emite.getInfEmisor().getFechaIniTransp()));
	        infoDocu.appendChild(fechaIniTransporte);
        }
        
        formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.fechaFinTransporte.documentos");
        if (getContextDocu(tipoDocumento,formatTipDoc)){
	        Element fechaFinTransporte = doc.createElement("fechaFinTransporte");
	        fechaFinTransporte.appendChild(doc.createTextNode(emite.getInfEmisor().getFechaFinTransp()));
	        infoDocu.appendChild(fechaFinTransporte);
        }
        
        if (emite.getInfEmisor().getPlaca().length()>0){
            formatTipDoc = Environment.c.getList("facElectronica.format_xml_field.infoDocumento.placa.documentos");
            if (getContextDocu(tipoDocumento,formatTipDoc)){
    	        Element placa = doc.createElement("placa");
    	        placa.appendChild(doc.createTextNode(emite.getInfEmisor().getPlaca()));
    	        infoDocu.appendChild(placa);
            }
        }
        //NOWJZU
        String indexLineaCab = "";
        String indexLineaDet = "";
        int indexLineaDetalle = 0;
        int indexLineaDetalleAdic = 0;
        if ( ListDestinatarios != null){        
            if (ListDestinatarios.size() > 0){
    	        Element destinatarios = doc.createElement("destinatarios");
    	        document.appendChild(destinatarios);
    	        for (int x=0;x<=ListDestinatarios.size()-1;x++){
    	        	Destinatarios DetDest = new Destinatarios(); 
    	        	DetDest = (Destinatarios)ListDestinatarios.get(x);
    		        Element destinatario = doc.createElement("destinatario");
    		        destinatarios.appendChild(destinatario);
    		        
    		        indexLineaCab = DetDest.getIdentDestinatario();
    		        
    		        Element identificacionDestinatario = doc.createElement("identificacionDestinatario");
    		        identificacionDestinatario.appendChild(doc.createTextNode(DetDest.getIdentDestinatario()));
    		        destinatario.appendChild(identificacionDestinatario);
    		        
    		        Element razonSocialDestinatario = doc.createElement("razonSocialDestinatario");
    		        razonSocialDestinatario.appendChild(doc.createTextNode(DetDest.getRazonSocialDestinatario()));
    		        destinatario.appendChild(razonSocialDestinatario);
    		        
    		        Element dirDestinatario = doc.createElement("dirDestinatario");
    		        dirDestinatario.appendChild(doc.createTextNode(DetDest.getDirDestinatario()));
    		        destinatario.appendChild(dirDestinatario);
    		        
    		        Element motivoTraslado = doc.createElement("motivoTraslado");
    		        motivoTraslado.appendChild(doc.createTextNode(DetDest.getMotTraslDestinatario()));
    		        destinatario.appendChild(motivoTraslado);
    		        
    		        Element docAduaneroUnico = doc.createElement("docAduaneroUnico");
    		        docAduaneroUnico.appendChild(doc.createTextNode(DetDest.getDocAduanero()));
    		        destinatario.appendChild(docAduaneroUnico);
    		        
    		        Element codEstabDestino = doc.createElement("codEstabDestino");
    		        codEstabDestino.appendChild(doc.createTextNode("0"+new Integer(DetDest.getCodEstabDestino()).toString()));
    		        destinatario.appendChild(codEstabDestino);
    		            		        
    		        Element rutaDest = doc.createElement("ruta");
    		        rutaDest.appendChild(doc.createTextNode(DetDest.getRutaDest()));
    		        destinatario.appendChild(rutaDest);
    		        
    		        Element codDocSustento = doc.createElement("codDocSustento");
    		        codDocSustento.appendChild(doc.createTextNode("0"+new Integer(DetDest.getCodDocSustentoDest()).toString()));
    		        destinatario.appendChild(codDocSustento);
    		        
    		        Element numDocSustento = doc.createElement("numDocSustento");
    		        numDocSustento.appendChild(doc.createTextNode(DetDest.getNumDocSustentoDest()));
    		        destinatario.appendChild(numDocSustento);    		        
    		        
    		        Element numAutDocSustento = doc.createElement("numAutDocSustento");
    		        numAutDocSustento.appendChild(doc.createTextNode(DetDest.getNumAutDocSustDest()));
    		        destinatario.appendChild(numAutDocSustento);
    		        
    		        
    		        Element fechaEmisionDocSustento = doc.createElement("fechaEmisionDocSustento");
    		        fechaEmisionDocSustento.appendChild(doc.createTextNode(formateoDate(DetDest.getFechEmisionDocSustDest(), "dd/MM/yyyy")));
    		        destinatario.appendChild(fechaEmisionDocSustento);
    		            		        
    		        indexLineaDet = "";
    		        if (ListDetalleDocumento != null){        
    		        if (ListDetalleDocumento.size() > 0){
    			        Element detalles = doc.createElement("detalles");
    			        destinatario.appendChild(detalles);
    			        for (int y=0;y<=ListDetalleDocumento.size()-1;y++){
    				        DetalleDocumento DetDocTmp = new DetalleDocumento(); 
    				        DetDocTmp = (DetalleDocumento)ListDetalleDocumento.get(y);
    				        indexLineaDet = DetDocTmp.getIdentificacionDestinatario();
    				        Element detalle = doc.createElement("detalle");
    				        detalles.appendChild(detalle);
    				        String ls_alias = "";
    				        if (indexLineaCab.equals(indexLineaDet)){
    				        	indexLineaDetalle = DetDocTmp.getLineaFactura(); 
    				        	ls_alias = "codigoInterno";    					        
    					        Element codigoPrincipal = doc.createElement(ls_alias);
    					        codigoPrincipal.appendChild(doc.createTextNode(DetDocTmp.getCodigoPrincipal()));
    					        detalle.appendChild(codigoPrincipal);    					        
    					        
    					        ls_alias = "codigoAdicional";    					        
    					        if (DetDocTmp.getCodigoAuxiliar().length()>0){
    					        Element codigoAuxiliar = doc.createElement(ls_alias);
    					        codigoAuxiliar.appendChild(doc.createTextNode(DetDocTmp.getCodigoAuxiliar()));
    					        detalle.appendChild(codigoAuxiliar);
    					        }
    					        
    					        Element descripcion = doc.createElement("descripcion");
    					        descripcion.appendChild(doc.createTextNode(DetDocTmp.getDescripcion()));
    					        detalle.appendChild(descripcion);
    					        
    					        Element cantidad = doc.createElement("cantidad");
    					        cantidad.appendChild(doc.createTextNode(new Double(DetDocTmp.getCantidad()).toString()));
    					        detalle.appendChild(cantidad);
    					        
    					        if (ListDocumentoDetAdicionales!=null){
    		    		        	if (ListDocumentoDetAdicionales.size()>0){
    		    			        	for (int z=0;z<=ListDocumentoDetAdicionales.size()-1;z++){
    		    			        		DetallesAdicionales DetAdic = new DetallesAdicionales();
    		    			        		DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(z);		        		
    		    			        		indexLineaDetalleAdic = DetAdic.getLineaFactura();		        		
    		    			        		if (indexLineaDetalle == indexLineaDetalleAdic){
    		    				        		Element detalleAdic = doc.createElement("detallesAdicionales");
    		    				    	        detalle.appendChild(detalleAdic);
    		    				    	        
    		    				    	        Element detAdicional = doc.createElement("detAdicional"); 
    		    				    	        attr = doc.createAttribute("nombre");
    		    				    	        attr.setValue(DetAdic.getNombre());
    		    				    	        detAdicional.setAttributeNode(attr);
    		    				    	        
    		    				    	        attr = doc.createAttribute("valor");
    		    				    	        attr.setValue(DetAdic.getValor());
    		    				    	        detAdicional.setAttributeNode(attr);
    		    				    	        
    		    				    	        detalleAdic.appendChild(detAdicional);
    		    			        		}
    		    			        	}
    		    		        	}
    		    		        }
    				        }    				        
    			        }
    		        }
    		        }
    		            		        
            		        
            }
        }
        
	        //JZURITA DETALLE	   
        }
        
		//////////////////////////////////////////////////////////
		/////////////////Informacion Adicional////////////////////
		//////////////////////////////////////////////////////////
		Element infoAdicional = doc.createElement("infoAdicional");
		document.appendChild(infoAdicional);
		if (ListDocumentoDetAdicionales!=null){
			if (ListDocumentoDetAdicionales.size()>0){
				for (int y=0;y<=ListDocumentoDetAdicionales.size()-1;y++){
				DetallesAdicionales DetAdic = new DetallesAdicionales();
				DetAdic = (DetallesAdicionales)ListDocumentoDetAdicionales.get(y);
				Element campoAdicional = doc.createElement("campoAdicional"); 
				attr = doc.createAttribute("nombre");
				attr.setValue(DetAdic.getNombre());
				campoAdicional.setAttributeNode(attr);
				
				/*attr = doc.createAttribute("valor");
				attr.setValue(DetAdic.getValor());
				campoAdicional.setAttributeNode(attr);
				*/	
				campoAdicional.appendChild(doc.createTextNode(DetAdic.getValor()));
				
				infoAdicional.appendChild(campoAdicional);
				
				
				}
			}
		
		}                   
        
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      if (nameOriginal != null){    	
    	  ruta = nameOriginal.replace("txt", "xml");
      }
      File file = new File(ruta);
      StreamResult result = new StreamResult(file);
      //result =  new StreamResult(System.out);
      transformer.transform(source, result);      
      System.out.println("File saved!\n"+ruta);
      return file.getName();
      /*
      String respuestaFirma = null;
      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
      ArchivoUtils arcFirmar = new ArchivoUtils();      
      emi.setRuc("0992531940001");
      
      System.out.println("ruta::" + ruta);
      System.out.println("file"+file.getName());
      fileFirmado = file.getName();
      rutaFirmado = emite.getInfEmisor().get_pathFirmados();
      respuestaFirma = ArchivoUtils.firmarArchivo(emi, ruta, rutaFirmado, "BCE_IKEY2032", null);
      
      if (respuestaFirma == null)
      {
        System.out.println("Firmado OK::"+respuestaFirma);
      }
      else System.out.println("Firmado Error::" + respuestaFirma);
      */      
      //return true;
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
      return "";
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    /*} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();*/
	}return "";
    

  }
  public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl)
  {
    RespuestaSolicitud respuesta = new RespuestaSolicitud();
    EnvioComprobantesWs cliente = null;
    try {
      cliente = new EnvioComprobantesWs(urlWsdl, 30000);
   
    respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0",10000);
    } catch (Exception ex) {
        respuesta.setEstado(ex.getMessage());
        return respuesta;
      }
    return respuesta;
  }
  
  private X509Certificate LoadCertificate(String path, String password)
  {
    X509Certificate certificate = null;
    this.provider = null;
    this.privateKey = null;

    return certificate;
  }

  public static String formateoDate(Date date, String formato)
  {
    String fecha;
    try {
      SimpleDateFormat formateo = new SimpleDateFormat(formato);
      fecha = formateo.format(date);
    }
    catch (Exception e)
    {      
      fecha = "";
    }
    return fecha;
  }

  public static ValidateField setCampoFormato(String[] matriz, int index, String Alias, String tipoDato, String formato, int size, String need) {
    ValidateField validFormat = new ValidateField();
    String value = "";
    validFormat.setLi_error(0);
    validFormat.setLs_msg_error("");
    try {
      value = matriz[index];
      System.out.println(Alias + "=" + value+"::"+value.length());
    }
    catch (Exception e) {
      validFormat.setLi_error(2);
      validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
      value = "";
      System.out.println("   " + Alias + "=" + value);
    }
    if (value == null) {
      validFormat.setLi_error(1);
      validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::null");
    } else {
      if (tipoDato.equals("String")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio.");
          } else {
            validFormat.setValueString("");
          }

        }
        else if (value.length() > size) {
          validFormat.setLi_error(1);
          validFormat.setLs_msg_error("El campo " + Alias + " sobrepasa el size " + size + " tiene " + value.length());
          value = value.substring(0, size);
          //validFormat.setValueString(value);
        } else {
          validFormat.setValueString(value);
        }
      }

      if (tipoDato.equals("Int")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueInt(0);
          } else {
            try {
              validFormat.setValueInt(Integer.parseInt(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueInt(Integer.parseInt(value));
          } catch (Exception e) {
        	 e.printStackTrace();
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }
      
      if (tipoDato.equals("Long")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueLong(0L);
          } else {
            try {
              validFormat.setValueLong(Long.parseLong(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueLong(Long.parseLong(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }
      
      if (tipoDato.equals("Double")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueDouble(0.0D);
          } else {
            try {
              validFormat.setValueDouble(Double.parseDouble(value));
            } catch (Exception e) {
              e.printStackTrace();
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueDouble(Double.parseDouble(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }
      
      if (tipoDato.equals("Date")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueDate(null);
          } else {
            try {
              System.out.println("value::"+value);              
              SimpleDateFormat formatFecha = new SimpleDateFormat();
              formatFecha.applyPattern(formato);
              validFormat.setValueDate(formatFecha.parse(value));
              System.out.println("ValueDate::"+validFormat.getValueDate());
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
              validFormat.setValueDate(null);
            }
          }
        }
        else try {
        	System.out.println("value::"+value);
            SimpleDateFormat formatFecha = new SimpleDateFormat();
            formatFecha.applyPattern(formato);
            validFormat.setValueDate(formatFecha.parse(value));
            System.out.println("ValueDate::"+validFormat.getValueDate());
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }
    }
    return validFormat;
  }

  public static ValidateField setCampoFormato(String[] matriz, int index, String Alias, String tipoDato, String formato, int size, String need, String valueDefault) {
    ValidateField validFormat = new ValidateField();
    String value = "";
    validFormat.setLi_error(0);
    validFormat.setLs_msg_error("");
    try {
      value = matriz[index];
      System.out.println(Alias + "=" + value);
    }
    catch (Exception e) {
      validFormat.setLi_error(2);
      validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
      if (valueDefault != null)
        value = valueDefault;
      else {
        value = "";
      }
      System.out.println("   " + Alias + "=" + value);
    }
    if (value == null) {
      validFormat.setLi_error(1);
      validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::null");
    } else {
      if (tipoDato.equals("String")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio.");
          } else {
            validFormat.setValueString("");
          }

        }
        else if (value.length() > size) {
          validFormat.setLi_error(-1);
          validFormat.setLs_msg_error("El campo " + Alias + " sobrepasa el size " + size + " tiene " + value.length());
          value = value.substring(0, size);
        } else {
          validFormat.setValueString(value);
        }
      }

      if (tipoDato.equals("Int")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueInt(0);
          } else {
            try {
              validFormat.setValueInt(Integer.parseInt(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueInt(Integer.parseInt(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }

      if (tipoDato.equals("Long")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueLong(0L);
          } else {
            try {
              validFormat.setValueLong(Long.parseLong(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueLong(Long.parseLong(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }

      if (tipoDato.equals("Double")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueDouble(0.0D);
          } else {
            try {
              validFormat.setValueDouble(Double.parseDouble(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
            }
          }
        }
        else try {
            validFormat.setValueDouble(Double.parseDouble(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }

      if (tipoDato.equals("Date")) {
        if (value.length() == 0) {
          if (need.equals("S")) {
            validFormat.setLi_error(1);
            validFormat.setLs_msg_error("El campo " + Alias + " es Obligatorio. Valor::" + value);
            validFormat.setValueDate(null);
          } else {
            try {
              SimpleDateFormat formatFecha = new SimpleDateFormat();
              formatFecha.applyPattern(formato);
              validFormat.setValueDate(formatFecha.parse(value));
            } catch (Exception e) {
              validFormat.setLi_error(2);
              validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
              validFormat.setValueDate(null);
            }
          }
        }
        else try {
            SimpleDateFormat formatFecha = new SimpleDateFormat();
            formatFecha.applyPattern(formato);
            validFormat.setValueDate(formatFecha.parse(value));
          } catch (Exception e) {
            validFormat.setLi_error(2);
            validFormat.setLs_msg_error("El campo " + Alias + " es Erroneo.Valor::" + value + "::Error::" + e.getMessage());
          }
      }

    }

    return validFormat;
  }

  public static boolean isInt(String value) {
    try {
      Integer.parseInt(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean isLong(String value) {
    try {
      Long.parseLong(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static String[] splitTotokens(String line, String delim) {
    String s = line;
    int i = 0;

    while (s.contains(delim)) {
      s = s.substring(s.indexOf(delim) + delim.length());
      i++;
    }
    String token = null;
    String remainder = null;
    String[] tokens = new String[i];

    for (int j = 0; j < i; j++) {
      token = line.substring(0, line.indexOf(delim));

      tokens[j] = token;
      remainder = line.substring(line.indexOf(delim) + delim.length());

      line = remainder;
    }
    return tokens;
  }

  public static void mostrarDatos()
  {
    List l = ListFormat;
    System.out.println("---------------------------");
    /*for (Formatos s : l) {
      System.out.println("Alias:" + s.getAlias());
      System.out.println("Orden:" + s.getOrden());
      System.out.println("Valor:" + s.getValor());
      System.out.println("---------------------------");
    }*/
  }

  public static void main(String[] arg) throws SQLException, Exception, IOException, NamingException, ClassNotFoundException {
    String valor = "IT|1|1|Servicios Profesionales Cima-E S.A.|Cima IT|0992531940001||01|001|001||Cdla. Kennedy Norte, Av. Miguel H. Alcivar y Eleodoro Arboleda, Ed. Plaza Center Piso 8 Of. 802|";
    int ln_result;
    String name_xml="facturacion.xml";
    //LeerDocumentos leer = new LeerDocumentos();
    
    LeerDocumentos.classReference = "LeerDocumentos";
    LeerDocumentos.id = "1.0";
    
    try{
		Environment.setConfiguration(name_xml);
		Environment.setCtrlFile();
		Environment.setLogger(Util.log_control);			
	}catch(Exception e){
		System.out.println(classReference+"::main>>FacturacionElectronica.WebService::main::Proceso de Carga de Archivo Xml Configuraciones::::");
	    throw new Exception(classReference+"::main>>FacturacionElectronica.WebService::main::Proceso de Carga de Archivo Xml Configuraciones::::");
	}
    
    try
    {
      Emisor e = new Emisor();
      InformacionTributaria infTribAdic = new InformacionTributaria();
      infTribAdic = e.obtieneInfoTributaria("0992531940001");
      System.out.println("Ruta::"+infTribAdic.get_pathInfoRecibida()+"010992531940001001010000006011.txt");
      //ln_result = procesarDatosWebServicesFiles(infTribAdic.get_pathInfoRecibida()+"010992531940001001010000006011.txt", "|", "01",e);
      
    }
    catch (Exception e)
    {
      
      e.printStackTrace();
      System.out.println("Error:" + e.getMessage());
    }
  }

  public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error){
		
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
		//Estimado(a):\nHubo inconvenientes con documento electrónico generado el |FECHA| con No. |NODOCUMENTO|.\n 
		//|Mensaje|\n Cualquier novedad comunicarse con |HELPDESK|
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
		emSend.send(emailTo, 
		  	        "Facturacion Electronica", 
		  	        emailMensaje);
		return 0;
	}
  
  public static int enviaEmailCliente(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf, String emailCliente){
	  String resultEnvioMail = "";
	  	EmailSender emSend = new EmailSender(emailHost,emailFrom);
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);		
		//Estimado(a):\nHubo inconvenientes con documento electrónico generado el |FECHA| con No. |NODOCUMENTO|.\n 
		//|Mensaje|\n Cualquier novedad comunicarse con |HELPDESK|
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
		resultEnvioMail = emSend.send(emailTo+";"+emailCliente, 
						  	        "Facturacion Electronica", 
						  	        emailMensaje,
						  	        fileAttachXml,
						  	        fileAttachPdf);
		}
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else
			System.out.println("Error de Envio de Mail::"+resultEnvioMail);
			return -1;
	}
  
  public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf){
		String resultEnvioMail = "";
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);
		emailHost = Environment.c.getString("facElectronica.alarm.email.host");
		//Enviado desde
		emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
		//Enviado para
		emailTo = Environment.c.getString("facElectronica.alarm.email.receivers-list");
		//Asunto
		emailSubject = Environment.c.getString("facElectronica.alarm.email.subject");
		//Email HelpDesk
		emailHelpDesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		//Estimado(a):\nHubo inconvenientes con documento electrónico generado el |FECHA| con No. |NODOCUMENTO|.\n 
		//|Mensaje|\n Cualquier novedad comunicarse con |HELPDESK|
		EmailSender emSend = new EmailSender(emailHost,emailFrom);
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
		
		resultEnvioMail = emSend.send(emailTo, 
		  	        "Facturacion Electronica", 
		  	        emailMensaje,
		  	        fileAttachXml,
		  	        fileAttachPdf);	
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else
			System.out.println("Error de Envio de Mail::"+resultEnvioMail);
			return -1;
	}
}