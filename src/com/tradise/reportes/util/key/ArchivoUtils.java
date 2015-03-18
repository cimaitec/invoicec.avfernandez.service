/*     */ package com.tradise.reportes.util.key;
/*     */ 
/*     */ import com.thoughtworks.xstream.XStream;
/*     */ import ec.gob.sri.comprobantes.administracion.modelo.ClaveContingencia;
/*     */ import ec.gob.sri.comprobantes.administracion.modelo.ConfiguracionDirectorio;
/*     */ import ec.gob.sri.comprobantes.administracion.modelo.Emisor;
/*     */ import ec.gob.sri.comprobantes.modelo.factura.Factura;
/*     */ import ec.gob.sri.comprobantes.modelo.guia.GuiaRemision;
/*     */ import ec.gob.sri.comprobantes.modelo.notacredito.NotaCredito;
/*     */ import ec.gob.sri.comprobantes.modelo.notadebito.NotaDebito;
/*     */ import ec.gob.sri.comprobantes.modelo.rentencion.ComprobanteRetencion;
/*     */ import ec.gob.sri.comprobantes.sql.ClavesSQL;
/*     */ import ec.gob.sri.comprobantes.sql.ComprobantesSQL;
/*     */ import ec.gob.sri.comprobantes.sql.ConfiguracionDirectorioSQL;
/*     */ import ec.gob.sri.comprobantes.util.AutorizacionComprobantesWs;
/*     */ import ec.gob.sri.comprobantes.util.DirectorioEnum;
/*     */ import ec.gob.sri.comprobantes.util.EnvioComprobantesWs;
/*     */ import ec.gob.sri.comprobantes.util.FormGenerales;
/*     */ import ec.gob.sri.comprobantes.util.TipoComprobanteEnum;
/*     */ import ec.gob.sri.comprobantes.util.X509Utils;
/*     */ import ec.gob.sri.comprobantes.util.xml.Java2XML;
/*     */ import ec.gob.sri.comprobantes.util.xml.LectorXPath;
/*     */ import ec.gob.sri.comprobantes.util.xml.ValidadorEstructuraDocumento;
/*     */ import ec.gob.sri.comprobantes.util.xml.XStreamUtil;
/*     */ import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
/*     */ import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.xpath.XPath;
/*     */ import javax.xml.xpath.XPathConstants;
/*     */ import javax.xml.xpath.XPathExpression;
/*     */ import javax.xml.xpath.XPathFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import xadesbes.ServicioFirmaXades;
/*     */ 
/*     */ public class ArchivoUtils
/*     */ {
/*     */   public static String archivoToString(String rutaArchivo)
/*     */   {
/*  63 */     StringBuffer buffer = new StringBuffer();
/*     */     try
/*     */     {
/*  66 */       FileInputStream fis = new FileInputStream(rutaArchivo);
/*  67 */       InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
/*  68 */       Reader in = new BufferedReader(isr);
/*     */       int ch;
/*  70 */       while ((ch = in.read()) > -1)
/*     */       {         
/*  71 */         buffer.append((char)ch);
/*     */       }
/*  73 */       in.close();
/*  74 */       return buffer.toString();
/*     */     } catch (IOException e) {
/*  76 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, e);
/*  77 */     }return null;
/*     */   }
/*     */ 
/*     */   public static File stringToArchivo(String rutaArchivo, String contenidoArchivo)
/*     */   {
/*  82 */     FileOutputStream fos = null;
/*  83 */     File archivoCreado = null;
/*     */     try
/*     */     {
/*  86 */       fos = new FileOutputStream(rutaArchivo);
/*  87 */       OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
/*  88 */       for (int i = 0; i < contenidoArchivo.length(); i++) {
/*  89 */         out.write(contenidoArchivo.charAt(i));
/*     */       }
/*  91 */       out.close();
/*     */ 
/*  93 */       archivoCreado = new File(rutaArchivo);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*  98 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*  99 */       return null;
/*     */     } finally {
/*     */       try {
/* 102 */         if (fos != null)
/* 103 */           fos.close();
/*     */       }
/*     */       catch (Exception ex) {
/* 106 */         Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/* 109 */     return archivoCreado;
/*     */   }
/*     */ 
/*     */   public static byte[] archivoToByte(File file)
/*     */     throws IOException
/*     */   {
/* 115 */     byte[] buffer = new byte[(int)file.length()];
/* 116 */     InputStream ios = null;
/*     */     try {
/* 118 */       ios = new FileInputStream(file);
/* 119 */       if (ios.read(buffer) == -1)
/* 120 */         throw new IOException("EOF reached while trying to read the whole file");
/*     */     }
/*     */     finally {
/*     */       try {
/* 124 */         if (ios != null)
/* 125 */           ios.close();
/*     */       }
/*     */       catch (IOException e) {
/* 128 */         Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, e);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 124 */       if (ios != null)
/* 125 */         ios.close();
/*     */     }
/*     */     catch (IOException e) {
/* 128 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, e);
/*     */     }
/*     */ 
/* 132 */     return buffer;
/*     */   }
/*     */ 
/*     */   public static boolean byteToFile(byte[] arrayBytes, String rutaArchivo)
/*     */   {
/* 137 */     boolean respuesta = false;
/*     */     try {
/* 139 */       File file = new File(rutaArchivo);
/* 140 */       file.createNewFile();
/* 141 */       FileInputStream fileInputStream = new FileInputStream(rutaArchivo);
/* 142 */       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayBytes);
/* 143 */       OutputStream outputStream = new FileOutputStream(rutaArchivo);
/*     */       int data;
/* 145 */       while ((data = byteArrayInputStream.read()) != -1)
/*     */       {
/* 146 */         outputStream.write(data);
/*     */       }
/*     */ 
/* 149 */       fileInputStream.close();
/* 150 */       outputStream.close();
/* 151 */       respuesta = true;
/*     */     } catch (IOException ex) {
/* 153 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/* 155 */     return respuesta;
/*     */   }
/*     */ 
/*     */   public static String obtenerValorXML(File xmlDocument, String expression)
/*     */   {
/* 160 */     String valor = null;
/*     */     try
/*     */     {
/* 163 */       LectorXPath reader = new LectorXPath(xmlDocument.getPath());
/* 164 */       valor = (String)reader.leerArchivo(expression, XPathConstants.STRING);
/*     */     }
/*     */     catch (Exception e) {
/* 167 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, e);
/*     */     }
/*     */ 
/* 170 */     return valor;
/*     */   }
/*     */ 
/*     */   public static String seleccionaXsd(String tipo)
/*     */   {
/* 175 */     String nombreXsd = null;
/*     */ 
/* 177 */     if (tipo.equals(TipoComprobanteEnum.FACTURA.getCode()))
/* 178 */       nombreXsd = TipoComprobanteEnum.FACTURA.getXsd();
/* 179 */     else if (tipo.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCode()))
/* 180 */       nombreXsd = TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getXsd();
/* 181 */     else if (tipo.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getCode()))
/* 182 */       nombreXsd = TipoComprobanteEnum.GUIA_DE_REMISION.getXsd();
/* 183 */     else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getCode()))
/* 184 */       nombreXsd = TipoComprobanteEnum.NOTA_DE_CREDITO.getXsd();
/* 185 */     else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getCode()))
/* 186 */       nombreXsd = TipoComprobanteEnum.NOTA_DE_DEBITO.getXsd();
/* 187 */     else if (tipo.equals(TipoComprobanteEnum.LOTE.getCode())) {
/* 188 */       nombreXsd = TipoComprobanteEnum.LOTE.getXsd();
/*     */     }
/*     */ 
/* 191 */     return nombreXsd;
/*     */   }
/*     */ 
/*     */   public static void firmarEnviarAutorizar(Emisor emisor, String pathCompletoArchivoAFirmar, String nombreArchivo, String ruc, String codDoc, String claveDeAcceso, String password)
/*     */     throws InterruptedException
/*     */   {
/* 197 */     RespuestaSolicitud respuestaRecepcion = new RespuestaSolicitud();
/* 198 */     String respuestaFirma = null;
/* 199 */     String respAutorizacion = null;
/*     */     try
/*     */     {
/* 202 */       String dirFirmados = new ConfiguracionDirectorioSQL().obtenerDirectorio(DirectorioEnum.FIRMADOS.getCode()).getPath();
/*     */ 
/* 204 */       respuestaFirma = firmarArchivo(emisor, pathCompletoArchivoAFirmar, dirFirmados, null, password);
/*     */ 
/* 206 */       if (respuestaFirma == null)
/*     */       {
/* 208 */         new File(pathCompletoArchivoAFirmar).delete();
/*     */ 
/* 210 */         File archivoFirmado = new File(dirFirmados + File.separator + nombreArchivo);
/*     */ 
/* 212 */         respuestaRecepcion = EnvioComprobantesWs.obtenerRespuestaEnvio(archivoFirmado, ruc, codDoc, claveDeAcceso, FormGenerales.devuelveUrlWs(emisor.getTipoAmbiente(), "RecepcionComprobantes"));
/*     */ 
/* 214 */         if (respuestaRecepcion.getEstado().equals("RECIBIDA"))
/*     */         {
/* 216 */           Thread.currentThread(); Thread.sleep(emisor.getTiempoEspera().intValue() * 1000);
/* 217 */           respAutorizacion = AutorizacionComprobantesWs.autorizarComprobanteIndividual(claveDeAcceso, nombreArchivo, emisor.getTipoAmbiente());
/*     */ 
/* 219 */           if (respAutorizacion.equals("AUTORIZADO")) {
/* 220 */             JOptionPane.showMessageDialog(new JFrame(), "El comprobante fue autorizado por el SRI", "Respuesta", 1);
/* 221 */             archivoFirmado.delete();
/*     */           }
/* 223 */           else if (respAutorizacion != null) {
/* 224 */             String estado = respAutorizacion.substring(0, respAutorizacion.lastIndexOf("|"));
/* 225 */             String resultado = respAutorizacion.substring(respAutorizacion.lastIndexOf("|") + 1, respAutorizacion.length());
/* 226 */             JOptionPane.showMessageDialog(new JFrame(), "El comprobante fue guardado, firmado y enviado exitósamente, pero no fue Autorizado\n" + estado + "\n" + FormGenerales.insertarCaracteres(resultado, "\n", 160), "Respuesta", 1);
/*     */           }
/*     */         }
/* 229 */         else if (respuestaRecepcion.getEstado().equals("DEVUELTA"))
/*     */         {
/* 231 */           String dirRechazados = dirFirmados + File.separator + "rechazados";
/* 232 */           String resultado = FormGenerales.insertarCaracteres(EnvioComprobantesWs.obtenerMensajeRespuesta(respuestaRecepcion), "\n", 160);
/*     */ 
/* 234 */           anadirMotivosRechazo(archivoFirmado, respuestaRecepcion);
/*     */ 
/* 236 */           File rechazados = new File(dirRechazados);
/* 237 */           if (!rechazados.exists()) {
/* 238 */             new File(dirRechazados).mkdir();
/*     */           }
/*     */ 
/* 241 */           if (!copiarArchivo(archivoFirmado, rechazados.getPath() + File.separator + nombreArchivo))
/* 242 */             JOptionPane.showMessageDialog(new JFrame(), "Error al mover archivo a carpeta rechazados", "Respuesta", 1);
/*     */           else {
/* 244 */             archivoFirmado.delete();
/*     */           }
/*     */ 
/* 247 */           JOptionPane.showMessageDialog(new JFrame(), "Error al tratar de enviar el comprobante hacia el SRI:\n" + resultado, "Se ha producido un error ", 0);
/*     */         }
/*     */       } else {
/* 250 */         JOptionPane.showMessageDialog(new JFrame(), "Error al tratar de firmar digitalmente el archivo:\n" + respuestaFirma, "Se ha producido un error ", 0);
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 254 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String firmarArchivo(Emisor emisor, String archivoACrear, String dirFirmados, String tokenId, String password)
/*     */   {
/* 260 */     String respuestaFirma = null;
/*     */     try
/*     */     {
/* 263 */       if (tokenId == null) {
/* 264 */         tokenId = emisor.getToken();
/*     */       }
/* 266 */       System.out.println("os.name::" + System.getProperty("os.name"));
/* 267 */       if ((password == null) || (System.getProperty("os.name").toUpperCase().indexOf("LINUX") == 0) || (System.getProperty("os.name").toUpperCase().indexOf("MAC") == 0))
/*     */       {
/* 269 */         respuestaFirma = X509Utils.firmaValidaArchivo(new File(archivoACrear), dirFirmados, emisor.getRuc(), tokenId, password);
/*     */       }
/* 271 */       else respuestaFirma = ServicioFirmaXades.firmaValidaArchivo(new File(archivoACrear), dirFirmados, emisor.getRuc(), password.toCharArray());
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 275 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 276 */       return ex.getMessage();
/*     */     }
/* 278 */     return respuestaFirma;
/*     */   }
/*     */ 
/*     */   public static String firmarArchivo2(Emisor emisor, File archivoACrear, String dirFirmados, String tokenId, String password)
/*     */   {
/* 283 */     String respuestaFirma = null;
/*     */     try
/*     */     {
/* 286 */       if (tokenId == null) {
/* 287 */         tokenId = emisor.getToken();
/*     */       }
/* 289 */       System.out.println("os.name::" + System.getProperty("os.name"));
/* 290 */       if ((password == null) || (System.getProperty("os.name").toUpperCase().indexOf("LINUX") == 0) || (System.getProperty("os.name").toUpperCase().indexOf("MAC") == 0))
/*     */       {
/* 292 */         respuestaFirma = X509Utils.firmaValidaArchivo(archivoACrear, dirFirmados, emisor.getRuc(), tokenId, password);
/*     */       }
/* 294 */       else respuestaFirma = ServicioFirmaXades.firmaValidaArchivo(archivoACrear, dirFirmados, emisor.getRuc(), password.toCharArray());
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 298 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 299 */       return ex.getMessage();
/*     */     }
/* 301 */     return respuestaFirma;
/*     */   }
/*     */ 
/*     */   public static String validaArchivoXSD(String tipoComprobante, String pathArchivoXML)
/*     */   {
/* 306 */     String respuestaValidacion = null;
/*     */     try
/*     */     {
/* 309 */       ValidadorEstructuraDocumento validador = new ValidadorEstructuraDocumento();
/* 310 */       String nombreXsd = seleccionaXsd(tipoComprobante);
/*     */ 
/* 312 */       String pathArchivoXSD = "resources/xsd/" + nombreXsd;
/*     */ 
/* 314 */       if (pathArchivoXML != null) {
/* 315 */         validador.setArchivoXML(new File(pathArchivoXML));
/* 316 */         validador.setArchivoXSD(new File(pathArchivoXSD));
/*     */ 
/* 318 */         respuestaValidacion = validador.validacion();
/*     */       }
/*     */     } catch (Exception ex) {
/* 321 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/* 323 */     return respuestaValidacion;
/*     */   }
/*     */ 
/*     */   private static String realizaMarshal(Object comprobante, String pathArchivo)
/*     */   {
/* 328 */     String respuesta = null;
/*     */ 
/* 330 */     if ((comprobante instanceof Factura))
/* 331 */       respuesta = Java2XML.marshalFactura((Factura)comprobante, pathArchivo);
/* 332 */     else if ((comprobante instanceof NotaDebito))
/* 333 */       respuesta = Java2XML.marshalNotaDeDebito((NotaDebito)comprobante, pathArchivo);
/* 334 */     else if ((comprobante instanceof NotaCredito))
/* 335 */       respuesta = Java2XML.marshalNotaDeCredito((NotaCredito)comprobante, pathArchivo);
/* 336 */     else if ((comprobante instanceof ComprobanteRetencion))
/* 337 */       respuesta = Java2XML.marshalComprobanteRetencion((ComprobanteRetencion)comprobante, pathArchivo);
/* 338 */     else if ((comprobante instanceof GuiaRemision)) {
/* 339 */       respuesta = Java2XML.marshalGuiaRemision((GuiaRemision)comprobante, pathArchivo);
/*     */     }
/* 341 */     return respuesta;
/*     */   }
/*     */ 
/*     */   public static String crearArchivoXml2(String pathArchivo, Object objetoModelo, ClaveContingencia claveContingencia, Long secuencial, String tipoComprobante)
/*     */   {
/* 346 */     String respuestaCreacion = null;
/* 347 */     if (objetoModelo != null)
/*     */       try {
/* 349 */         respuestaCreacion = realizaMarshal(objetoModelo, pathArchivo);
/*     */ 
/* 351 */         if (respuestaCreacion != null)
/* 353 */         if ((claveContingencia != null) && (claveContingencia.getCodigoComprobante() != null)) {
/* 354 */           claveContingencia.setUsada("S");
/* 355 */           new ClavesSQL().actualizaClave(claveContingencia);
/*     */         }
/*     */ 
/* 358 */         new ComprobantesSQL().actualizaSecuencial(tipoComprobante, Long.valueOf(secuencial.longValue() + 1L));
/*     */       }
/*     */       catch (Exception ex) {
/* 361 */         Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     else {
/* 364 */       respuestaCreacion = "Ingrese los campos obligatorios del comprobante";
/*     */     }
/* 366 */     label99: return respuestaCreacion;
/*     */   }
/*     */ 
/*     */   public static String obtieneClaveAccesoAutorizacion(Autorizacion item)
/*     */   {
/* 371 */     String claveAcceso = null;
/*     */ 
/* 373 */     String xmlAutorizacion = XStreamUtil.getRespuestaLoteXStream().toXML(item);
/* 374 */     File archivoTemporal = new File("temp.xml");
/*     */ 
/* 376 */     stringToArchivo(archivoTemporal.getPath(), xmlAutorizacion);
/* 377 */     String contenidoXML = decodeArchivoBase64(archivoTemporal.getPath());
/*     */ 
/* 379 */     if (contenidoXML != null) {
/* 380 */       stringToArchivo(archivoTemporal.getPath(), contenidoXML);
/* 381 */       claveAcceso = obtenerValorXML(archivoTemporal, "/*/infoTributaria/claveAcceso");
/*     */     }
/*     */ 
/* 384 */     return claveAcceso;
/*     */   }
/*     */ 
/*     */   public static String decodeArchivoBase64(String pathArchivo)
/*     */   {
/* 389 */     String xmlDecodificado = null;
/*     */     try {
/* 391 */       File file = new File(pathArchivo);
/* 392 */       if (file.exists())
/*     */       {
/* 394 */         String encd = obtenerValorXML(file, "/*/comprobante");
/*     */ 
/* 396 */         xmlDecodificado = encd;
/*     */       }
/*     */       else {
/* 399 */         System.out.print("File not found!");
/*     */       }
/*     */     } catch (Exception e) {
/* 402 */       Logger.getLogger(AutorizacionComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
/*     */     }
/* 404 */     return xmlDecodificado;
/*     */   }
/*     */ 
/*     */   public static boolean anadirMotivosRechazo(File archivo, RespuestaSolicitud respuestaRecepcion)
/*     */   {
/* 409 */     boolean exito = false;
/* 410 */     File respuesta = new File("respuesta.xml");
/* 411 */     Java2XML.marshalRespuestaSolicitud(respuestaRecepcion, respuesta.getPath());
/* 412 */     if (adjuntarArchivo(respuesta, archivo)) {
/* 413 */       exito = true;
/* 414 */       respuesta.delete();
/*     */     }
/* 416 */     return exito;
/*     */   }
/*     */ 
/*     */   public static boolean adjuntarArchivo(File respuesta, File comprobante)
/*     */   {
/* 421 */     boolean exito = false;
/*     */     try
/*     */     {
/* 424 */       Document document = merge("*", new File[] { comprobante, respuesta });
/*     */ 
/* 426 */       DOMSource source = new DOMSource(document);
/*     */ 
/* 428 */       StreamResult result = new StreamResult(new OutputStreamWriter(new FileOutputStream(comprobante), "UTF-8"));
/*     */ 
/* 430 */       TransformerFactory transFactory = TransformerFactory.newInstance();
/* 431 */       Transformer transformer = transFactory.newTransformer();
/*     */ 
/* 433 */       transformer.transform(source, result);
/*     */     }
/*     */     catch (Exception ex) {
/* 436 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/* 438 */     return exito;
/*     */   }
/*     */ 
/*     */   private static Document merge(String exp, File[] files)
/*     */     throws Exception
/*     */   {
/* 444 */     XPathFactory xPathFactory = XPathFactory.newInstance();
/* 445 */     XPath xpath = xPathFactory.newXPath();
/* 446 */     XPathExpression expression = xpath.compile(exp);
/*     */ 
/* 448 */     DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/* 449 */     docBuilderFactory.setIgnoringElementContentWhitespace(true);
/* 450 */     DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 451 */     Document base = docBuilder.parse(files[0]);
/*     */ 
/* 453 */     Node results = (Node)expression.evaluate(base, XPathConstants.NODE);
/* 454 */     if (results == null) {
/* 455 */       throw new IOException(files[0] + ": expression does not evaluate to node");
/*     */     }
/*     */ 
/* 458 */     for (int i = 1; i < files.length; i++) {
/* 459 */       Document merge = docBuilder.parse(files[i]);
/* 460 */       Node nextResults = (Node)expression.evaluate(merge, XPathConstants.NODE);
/* 461 */       results.appendChild(base.importNode(nextResults, true));
/*     */     }
/*     */ 
/* 464 */     return base;
/*     */   }
/*     */ 
/*     */   public static boolean copiarArchivo(File archivoOrigen, String pathDestino)
/*     */   {
/* 469 */     FileReader in = null;
/* 470 */     boolean resultado = false;
/*     */     try
/*     */     {
/* 473 */       File outputFile = new File(pathDestino);
/* 474 */       in = new FileReader(archivoOrigen);
/* 475 */       FileWriter out = new FileWriter(outputFile);
/*     */       int c;
/* 477 */       while ((c = in.read()) != -1)
/*     */       {
/* 478 */         out.write(c);
/*     */       }
/* 480 */       in.close();
/* 481 */       out.close();
/* 482 */       resultado = true;
/*     */     }
/*     */     catch (Exception ex) {
/* 485 */       Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */       try
/*     */       {
/* 488 */         in.close();
/*     */       } catch (IOException ex1) {
/* 490 */         Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex1);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 488 */         in.close();
/*     */       } catch (IOException ex) {
/* 490 */         Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/* 493 */     return resultado;
/*     */   }
/*     */ }
