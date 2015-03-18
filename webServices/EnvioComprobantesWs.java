package com.cimait.webServices;

 
 import ec.gob.sri.comprobantes.modelo.Respuesta;
 import ec.gob.sri.comprobantes.sql.RespuestaSQL;
 import ec.gob.sri.comprobantes.ws.Comprobante;
 import ec.gob.sri.comprobantes.ws.Comprobante.Mensajes;
 import ec.gob.sri.comprobantes.ws.Mensaje;
 import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
 import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
 import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
 import ec.gob.sri.comprobantes.ws.RespuestaSolicitud.Comprobantes;
 import java.io.File;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import com.cimait.util.key.ArchivoUtils;
 
 public class EnvioComprobantesWs
 {
   private static RecepcionComprobantesService service;
   private static final String VERSION = "1.0.0";
   public static final String ESTADO_RECIBIDA = "RECIBIDA";
   public static final String ESTADO_DEVUELTA = "DEVUELTA";
 
   public EnvioComprobantesWs(String wsdlLocation)
     throws MalformedURLException, WebServiceException
   {
	     URL url = new URL(wsdlLocation);
	     QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
	     service = new RecepcionComprobantesService(url, qname);
   }
 
   public static final Object webService(String wsdlLocation) {
     try {
	       QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
	       URL url = new URL(wsdlLocation);
	       service = new RecepcionComprobantesService(url, qname);
/*  48 */       return null;
     } catch (MalformedURLException ex) {
/*  50 */       Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
/*  51 */       return ex;
     } catch (WebServiceException ws) {
/*  53 */       return ws;
     }
   }
 
   public RespuestaSolicitud enviarComprobante(String ruc, File xmlFile, String tipoComprobante, String versionXsd)
   {
/*  69 */     RespuestaSolicitud response = null;
     try {
/*  71 */       RecepcionComprobantes port = service.getRecepcionComprobantesPort();
/*  72 */       response = port.validarComprobante(ArchivoUtils.archivoToByte(xmlFile));
     }
     catch (Exception e) {
/*  75 */       Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
/*  76 */       response = new RespuestaSolicitud();
/*  77 */       response.setEstado(e.getMessage());
/*  78 */       return response;
     }
 
/*  81 */     return response;
   }
 
   public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte[] xml, String tipoComprobante, String versionXsd)
   {
/*  96 */     RespuestaSolicitud response = null;
     try {
/*  98 */       RecepcionComprobantes port = service.getRecepcionComprobantesPort();
 
/* 100 */       response = port.validarComprobante(xml);
     }
     catch (Exception e) {
/* 103 */       Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
/* 104 */       response = new RespuestaSolicitud();
/* 105 */       response.setEstado(e.getMessage());
/* 106 */       return response;
     }
/* 108 */     return response;
   }
 
   public RespuestaSolicitud enviarComprobanteLotes(String ruc, File xml, String tipoComprobante, String versionXsd)
   {
/* 123 */     RespuestaSolicitud response = null;
     try {
/* 125 */       RecepcionComprobantes port = service.getRecepcionComprobantesPort();
/* 126 */       response = port.validarComprobante(ArchivoUtils.archivoToByte(xml));
     } catch (Exception e) {
/* 128 */       Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
/* 129 */       response = new RespuestaSolicitud();
/* 130 */       response.setEstado(e.getMessage());
/* 131 */       return response;
     }
/* 133 */     return response;
   }
 
   public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl)
   {
/* 145 */     RespuestaSolicitud respuesta = new RespuestaSolicitud();
/* 146 */     EnvioComprobantesWs cliente = null;
     try {
/* 148 */       cliente = new EnvioComprobantesWs(urlWsdl);
     } catch (Exception ex) {
/* 150 */       //Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
/* 151 */       respuesta.setEstado(ex.getMessage());
/* 152 */       return respuesta;
     }
/* 154 */     respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0");
 
/* 156 */     return respuesta;
   }
 
   public static void guardarRespuesta(String claveDeAcceso, String archivo, String estado, java.util.Date fecha)
   {
     try
     {
/* 170 */       java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());
 
/* 172 */       Respuesta item = new Respuesta(null, claveDeAcceso, archivo, estado, sqlDate);
/* 173 */       RespuestaSQL resp = new RespuestaSQL();
/* 174 */       resp.insertarRespuesta(item);
     }
     catch (Exception ex) {
/* 177 */       Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
     }
   }
 
   public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta)
   {
/* 189 */     StringBuilder mensajeDesplegable = new StringBuilder();
/* 190 */     if (respuesta.getEstado().equals("DEVUELTA") == true)
     {
/* 192 */       RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();
/* 193 */       for (Comprobante comp : comprobantes.getComprobante()) {
/* 194 */         mensajeDesplegable.append(comp.getClaveAcceso());
/* 195 */         mensajeDesplegable.append("\n");
/* 196 */         for (Mensaje m : comp.getMensajes().getMensaje()) {
/* 197 */           mensajeDesplegable.append(m.getMensaje()).append(" :\n");
/* 198 */           mensajeDesplegable.append(m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "");
/* 199 */           mensajeDesplegable.append("\n");
         }
/* 201 */         mensajeDesplegable.append("\n");
       }
     }
 
/* 205 */     return mensajeDesplegable.toString();
   }
 }

/* Location:           J:\eclipseIndigo32\workspaceJSF\EJB_3WebServices\WebContent\WEB-INF\lib\ComprobantesDesktop.jar
 * Qualified Name:     ec.gob.sri.comprobantes.util.EnvioComprobantesWs
 * JD-Core Version:    0.6.2
 */