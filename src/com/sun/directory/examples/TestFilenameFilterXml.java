package com.sun.directory.examples;

import java.io.*; 
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestFilenameFilterXml{ 

public static void main( String args[] ) throws InterruptedException {

// Buscar en el raiz del disco C 
// Se coloca doble slash puesto que es un caracter de escape. 
// en literales cadenas en java se realiza: 
// \n -> retorno de carro 
// \t -> tabulador 
// \\ -> slash 
// \" -> comillas 
String directorio = "D://FacturacionElectronica//";
directorio = "//192.168.32.90//DataExpress//jzurita//generados//";
// el directorio es un archivo del sistema operativo 
// fileDirectorio es este archivo en el programa 
File fileDirectorio = new File(directorio);

// se crea un filtro de archivos que contengan java. 
FilenameFilter filtro = new FilterJava(".*.xml");
//ruc + establecimento + punto emision + secuencial

// se aplica el filtro al directorio. 
    String [] contenido = fileDirectorio.list(filtro); 
	for (int i=0; i < contenido.length; i++) {
		System.out.println(directorio+contenido[i]);
		
		
	      String respuestaFirma = null;
	      ec.gob.sri.comprobantes.administracion.modelo.Emisor emi = new ec.gob.sri.comprobantes.administracion.modelo.Emisor();
	      ArchivoUtils arcFirmar = new ArchivoUtils();      
	      emi.setRuc("0992531940001");
	      String rutaFirmado;
	      String fileFirmado;
	      
	      System.out.println("ruta::" + directorio+contenido[i]);
	      System.out.println("file"+contenido[i]);
	      fileFirmado = contenido[i];
	      rutaFirmado = "C:\\firmados\\";//emite.getInfEmisor().get_pathFirmados();
	      rutaFirmado = "//192.168.32.90//DataExpress//jzurita//firmados//";
	      
	      String dirAutorizados = "//192.168.32.90//DataExpress//jzurita//autorizados//";
	      String dirNoAutorizados = "//192.168.32.90//DataExpress//jzurita//noautorizados//";
	      try {
	    	File fxml = new File(directorio+contenido[i]);
			byte xmlbyte[] = ArchivoUtils.archivoToByte(fxml);
			
	      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      /*
	      String patron = "dd-MM-yyyy HH:mm:ss.SSS";
	      SimpleDateFormat formato = new SimpleDateFormat(patron);
	      // formateo
	      System.out.println(formato.format(new Date("2015-06-07 00:00:00.0") ));
	      */	      
	      
	      //respuestaFirma = ArchivoUtils.firmarArchivo(emi, directorio+contenido[i], rutaFirmado, "BCE_IKEY2032", null);
	      
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      factory.setNamespaceAware(true);
	      DocumentBuilder builder;
	      Document doc = null;
	      String claveAcceso = "";
	      String ruc = "";
	      String ambiente  ="";
	      try {
	            builder = factory.newDocumentBuilder();
	            doc = builder.parse(directorio+contenido[i]);
	 
	            // Create XPathFactory object
	            XPathFactory xpathFactory = XPathFactory.newInstance();
	 
	            // Create XPath object
	            XPath xpath = xpathFactory.newXPath();
	           
	            try {
	                XPathExpression expr =
	                    xpath.compile("/factura/infoTributaria/claveAcceso/text()");
	                claveAcceso = (String) expr.evaluate(doc, XPathConstants.STRING);
	            } catch (XPathExpressionException e) {
	                e.printStackTrace();
	            }
	            
	            try {
	                XPathExpression expr =
	                    xpath.compile("/factura/infoTributaria/ruc/text()");
	                ruc = (String) expr.evaluate(doc, XPathConstants.STRING);
	            } catch (XPathExpressionException e) {
	                e.printStackTrace();
	            }
	            
	            try {
	                XPathExpression expr =
	                    xpath.compile("/factura/infoTributaria/ambiente/text()");
	                ambiente = (String) expr.evaluate(doc, XPathConstants.STRING);
	            } catch (XPathExpressionException e) {
	                e.printStackTrace();
	            }
	            
	            emi.setTipoAmbiente(ambiente);
	        } catch (Exception e) {
	            e.printStackTrace();
	      }
	      com.sun.businessLogic.validate.Emisor emite = null;
	      //ArchivoUtils.firmarEnviarAutorizar(emi, rutaFirmado, directorio+contenido[i], contenido[i],  ruc, "01", claveAcceso, null, "BCE_IKEY2032",dirAutorizados,dirNoAutorizados,emite);
	      
	      if (respuestaFirma == null)
	      {
	        System.out.println("Firmado OK::"+respuestaFirma);
	      }
	      else System.out.println("Firmado Error::" + respuestaFirma);
	      
	}
	}
	
} 