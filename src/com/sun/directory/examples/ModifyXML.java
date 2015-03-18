package com.sun.directory.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class ModifyXML {

	public static void modificarClaveAcceso(String filePath, String ClaveAcceso){
		try {
			 
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
	 
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
	 
			// select infoTributaria
			Element infoTributaria = rootNode.getChild("infoTributaria");			
	 
			// update clave Acceso
			infoTributaria.getChild("claveAcceso").setText(ClaveAcceso);
	 
	 
			XMLOutputter xmlOutput = new XMLOutputter();
	 
			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
	 
			// xmlOutput.output(doc, System.out);
	 
			System.out.println("File Modificado::"+filePath+".\nModificado la clave de acceso de contingencia::"+ClaveAcceso);
		  } catch (IOException io) {
			  io.printStackTrace();
			io.printStackTrace();
		  } catch (JDOMException e) {
			
			  e.printStackTrace();
		  }
	}
	
	
	public static void main(String arg[]){
		ModifyXML Eje = new ModifyXML();
		Eje.modificarClaveAcceso("file.xml", "2gdfgdfgdfg");
	}
	
}
