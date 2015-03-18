package com.sun.directory.examples;
import ec.gob.sri.comprobantes.modelo.Respuesta;
import ec.gob.sri.comprobantes.sql.RespuestaSQL;

//import ec.gob.sri.comprobantes.ws.Comprobante;
import com.util.webServices.ws.Comprobante;
//import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import com.util.webServices.ws.RecepcionComprobantes;
//import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import com.util.webServices.ws.RecepcionComprobantesService;
//import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import com.util.webServices.ws.RespuestaSolicitud;
//import ec.gob.sri.comprobantes.ws.RespuestaSolicitud.Comprobantes;
import com.util.webServices.ws.RespuestaSolicitud.Comprobantes;
//import ec.gob.sri.comprobantes.ws.Mensaje;
import com.util.webServices.ws.Mensaje;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/*import com.cimait.webServices.ws.Mensaje;
import com.cimait.webServices.ws.RecepcionComprobantesService;
*/


public class EnvioComprobantesWs
{
  private static RecepcionComprobantesService service;
  private static final String VERSION = "1.0.0";
  public static final String ESTADO_RECIBIDA = "RECIBIDA";
  public static final String ESTADO_DEVUELTA = "DEVUELTA";

  public EnvioComprobantesWs(String wsdlLocation)
    throws MalformedURLException, WebServiceException
  {
	try{
    URL url = new URL(wsdlLocation);
    URLConnection con = url.openConnection();
    con.setConnectTimeout(40000);
    con.setReadTimeout(30000);
    QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
    service = new RecepcionComprobantesService(con.getURL(), qname);
	}catch(Exception e){
		e.printStackTrace();
	}
	
  }

  public static final Object webService(String wsdlLocation) throws IOException {
    try {
      QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
      URL url = new URL(wsdlLocation);
      URLConnection con = url.openConnection();
      con.setConnectTimeout(4000);
      con.setReadTimeout(3000);
      service = new RecepcionComprobantesService(con.getURL(), qname);
      return null;
    } catch (MalformedURLException ex) {
      Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
      return ex;
    } catch (WebServiceException ws) {
      return ws;
    }
  }

  public RespuestaSolicitud enviarComprobante(String ruc, File xmlFile, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try {
      RecepcionComprobantes port = (RecepcionComprobantes) service.getRecepcionComprobantesPort();
      response = port.validarComprobante(ArchivoUtils.archivoToByte(xmlFile));
    }
    catch (Exception e) {
      Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }

    return response;
  }

  public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte[] xml, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try {
      RecepcionComprobantes port = (RecepcionComprobantes) service.getRecepcionComprobantesPort();

      response = port.validarComprobante(xml);
    }
    catch (Exception e) {
      Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }
    return response;
  }

  public RespuestaSolicitud enviarComprobanteLotes(String ruc, File xml, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try {
      RecepcionComprobantes port = (RecepcionComprobantes) service.getRecepcionComprobantesPort();
      response = port.validarComprobante(ArchivoUtils.archivoToByte(xml));
    } catch (Exception e) {
      Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }
    return response;
  }

  public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl)
  {
    RespuestaSolicitud respuesta = new RespuestaSolicitud();
    EnvioComprobantesWs cliente = null;
    try {
    	urlWsdl = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";    	    
    	
    	cliente = new EnvioComprobantesWs(urlWsdl);
    	
    } catch (Exception ex) {
      ex.printStackTrace();
      respuesta.setEstado(ex.getMessage());
      return respuesta;
    }
    respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0");

    return respuesta;
  }

  public static void guardarRespuesta(String claveDeAcceso, String archivo, String estado, java.util.Date fecha)
  {
    try
    {
      java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());

      Respuesta item = new Respuesta(null, claveDeAcceso, archivo, estado, sqlDate);
      RespuestaSQL resp = new RespuestaSQL();
      resp.insertarRespuesta(item);
    }
    catch (Exception ex) {
      Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta)
  {
    StringBuilder mensajeDesplegable = new StringBuilder();
    if (respuesta.getEstado().equals("DEVUELTA") == true)
    {
      RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();
      for (Comprobante comp : comprobantes.getComprobante()) {
        mensajeDesplegable.append(comp.getClaveAcceso());
        mensajeDesplegable.append("\n");
        for (Mensaje m : comp.getMensajes().getMensaje()) {
          mensajeDesplegable.append(m.getMensaje()).append(" :\n");
          mensajeDesplegable.append(m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "");
          mensajeDesplegable.append("\n");
        }
        mensajeDesplegable.append("\n");
      }
    }

    return mensajeDesplegable.toString();
  }
}