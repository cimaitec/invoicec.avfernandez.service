package com.util.webServices;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.WebParam;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.DAO.InformacionTributaria;
import com.sun.businessLogic.validate.Emisor;
import com.sun.businessLogic.validate.LeerDocumentos;
import com.sun.database.ConexionBase;
@WebService
public class FacturacionElectronicaWS {

	@WebMethod(action = "facElect_Emision")
	public String facElect_Emision(@WebParam(name = "ps_Ruc") final String ps_Ruc,
								   @WebParam(name = "ps_tipo_documento") final String ps_tipo_documento,
								   @WebParam(name = "ps_pathfiles") final String ps_pathfiles) throws SQLException, IOException, NamingException, ClassNotFoundException{
		String xml = null;
		System.out.println("Ruc->"+ps_Ruc);
		System.out.println("Tipo Documento->"+ps_tipo_documento);
		Emisor e = new Emisor();
		InformacionTributaria infTrib = new InformacionTributaria();
		infTrib = e.obtieneInfoTributaria(ps_Ruc);		
		if (infTrib.getRuc().equals(ps_Ruc)){			
			//int ln_result = LeerDocumentos.procesarDatosWebServicesLines(ps_pathfiles,"\n");
			//LeerDocumentos.mostrarDatos();			
			try {
				;;
				//int ln_result = LeerDocumentos.procesarDatosWebServicesFiles(ps_pathfiles,"|",ps_tipo_documento);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("Error:"+e1.getMessage());
			}
			
			//boolean lb_result = factura("D://FacturacionElectronica//pruebaxml.xml");
			xml = "Existe";			
		}else{
			xml = "Error Ruc no Existe";
		}
		return xml;
	}
	
	
	public static void main (String arg[]) throws SQLException, IOException, NamingException, ClassNotFoundException{
    	/*Connection Con = ConexionBase.getConexionPostgres();
    	ResultSet Rs;
    	Statement st;
    	st = Con.createStatement();
    	String sql = "SELECT version() ";    	        
    	Rs= st.executeQuery(sql);
    	while (Rs.next()){ 
    	System.out.println("Version ->"+Rs.getString(1));
    	}
    	Rs.close();
    	st.close();
    	Con.close();*/
		FacturacionElectronicaWS fac = new FacturacionElectronicaWS();
		fac.facElect_Emision("0992531940001", "01", "D://FacturacionElectronica//prueba.txt");
    }
	
}
