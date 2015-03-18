package com.sun.directory.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;





import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.DAO.DetalleDocumento;
import com.sun.DAO.DetalleImpuestosRetenciones;
import com.sun.DAO.DetalleTotalImpuestos;
import com.sun.DAO.DocumentoImpuestos;
import com.sun.DAO.InformacionAdicional;
import com.sun.businessLogic.validate.Emisor;
import com.sun.businessLogic.validate.LeerDocumentos;
import com.sun.comprobantes.util.EmailSender;
import com.sun.comprobantes.util.FormGenerales;
import com.sun.database.ConexionBase;
import com.tradise.reportes.entidades.FacCabDocumento;
import com.tradise.reportes.entidades.FacDetDocumento;
import com.tradise.reportes.servicios.ReporteSentencias;
import com.tradise.reportes.util.key.GenericTransaction;
import com.util.util.key.Environment;
import com.util.webServices.EnvioComprobantesWs;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

public class ServiceDataHilo extends GenericTransaction{
	
	private int idHilo;
	private static InfoEmpresa infoEmp;
	private Emisor emite;
	
	public static String classReference = "ServiceData";
	public static StringBuilder SBmsj = null;
	public File[] contenido;
	private static String nombreEmpresa = Environment.c.getString("facElectronica.alarm.email.nombreEmpresa");
	public static String flagUpdateERP = Environment.c.getString("facElectronica.database.Empresa.sql.flagUpdateERP");
	private int timeOutRecepComp = Environment.c.getInt("facElectronica.general.ws.RecepcionComprobantes.timeOut");
	
	
	public ServiceDataHilo(int hilo, InfoEmpresa infoEmpresa, Emisor emitir) {
		this.idHilo = hilo;
		this.infoEmp = infoEmpresa;
		this.emite = emitir;
	}
	
	public void atiendeHilo() throws Exception {
		//boolean flagFile = false;
		//Se cambia a atómico por concurrencia -> false por defecto
		AtomicBoolean flagFile = new AtomicBoolean();
		boolean li_result = false,flagReproceso = false;
		//VPI - GBA
		ConexionBase.init();
		String mensaje = "", respuestaFirma = "";
		obtieneInfoXml(emite.getFilexml());
		System.out.println("Atiende Hilo::" + idHilo);
		FacCabDocumento CabDoc = new FacCabDocumento();
		ReporteSentencias rpSen = new ReporteSentencias();
		
		try {
			flagFile.set(validaEstadoDocumento(emite));
		} catch (Exception e) {
			mensaje = e.getMessage();
			//Necesario si ya nace como false?
			flagFile.set(false);
		}

		
		//if (flagFile)
		if (flagFile.compareAndSet(true, false)) {

			emite.insertaBitacora(emite, "IN",  "Carga Inicial del Proceso de Despacho", "", "", "", "");
			try {
				leerXml(infoEmp.getDirRecibidos() + emite.getFilexml(), emite);
				emite.insertaBitacora(emite, "LX",  "Lectura del XML Terminada Proceso de Despacho", "", "", "", "");
				li_result = copiarXml(infoEmp.getDirRecibidos()
						+ emite.getFilexml());
				System.out.println("Clave de Acesso::::"
						+ emite.getInfEmisor().getClaveAcceso());
				//Siempre se debe setear el xmlbackup
				emite.setFileXmlBackup(infoEmp.getDirRecibidos()
						+ emite.getFilexml().replace(".xml",
								"_backup.xml"));
				//FIXME: NULLPOINTER EXCEPTION SI NO LEYÓ EL XML Y POR ENDE NO SETEÓ EL TIPOEMISION...

				if (li_result) {
					//Únicamente si pudo ser leído el documento XML y seteado el valor de tipo emisión
					//Evita nullpointer exception
					if(emite.getInfEmisor().getTipoEmision()!= null){
						if (emite.getInfEmisor().getTipoEmision().equals("1")) {

						emite = ModifyDocumentAcceso.addPutClaveAcceso(
								infoEmp.getDirRecibidos() + emite.getFilexml(),
								emite);
						emite.insertaBitacora(emite, "MC",  "Modificacion de Clave de Acceso Terminada >> " + emite.getInfEmisor().getClaveAcceso(), "", "", "", "");

					} else {
						emite.insertaBitacora(emite, "MN","No se modifico la clave de acceso, la transaccion esta "
								+ "en estado de Contingencia, Reprocesando con clave de acceso de contingencia.::"
								+ emite.getFilexml(), "", "", "", "");
					}
					}
				}
				/*
				log.info("<<setFileXmlBackup y ModifyDocumentAcceso de xml::Inicio::"
						+ emite.getFilexml() + "::" + toString());*/
			}
			catch (Exception ex) {
				System.out.println("Error al leer el XML");
				System.out.println("Registrando versión mínima del documento con estado EX");
				registrarDocErroneo(emite);
				mensaje = " Error en Leer Xml. Mensaje->" + ex.getMessage()
						+ "::Ruc::" + emite.getInfEmisor().getRuc()
						+ " ::Establecimiento::"
						+ emite.getInfEmisor().getCodEstablecimiento()
						+ " ::Punto Emision::"
						+ emite.getInfEmisor().getCodPuntoEmision()
						+ " ::CodDocumento::"
						+ emite.getInfEmisor().getCodDocumento()
						+ " ::Secuencial::"
						+ emite.getInfEmisor().getSecuencial()
						+ "::ClaveAcceso::" + emite.toStringInfo();
				ex.printStackTrace();
				int li_envio = enviaEmail("message_error", emite, "",
						mensaje, infoEmp.getDirRecibidos() + emite.getFilexml(), null);
				
				String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
				String fileGenerado = ArchivoUtils.archivoToString(infoEmp.getDirRecibidos() + emite.getFilexml());
				emite.insertaBitacora(emite, "EX", "" ,mensaje +" >> "+resultEmail, fileGenerado, "", "");
				throw new Exception(mensaje);
			}
			
			try {
				validacionAdicional(emite);
			} catch (Exception e) {
				mensaje = e.getMessage();
				emite.setResultado(-1);
			}
			if (emite.getResultado() == 0) {
				// Validacion con el archivo XSD
				String ls_validaXSD = "";
				ls_validaXSD = com.sun.directory.examples.ArchivoUtils
						.validaArchivoXSD(emite.getInfEmisor()
								.getTipoComprobante(),
								infoEmp.getDirRecibidos() + emite.getFilexml(),
								infoEmp.getPathXsd());
				emite.insertaBitacora(emite, "VX",  "Validacion XSD Terminada "+ ls_validaXSD, "", "", "", "");
				// VPI - Validacion XSD que estaba al ultimo -- Logica inversa
				if (!(ls_validaXSD == null)/* || !(ls_validaXSD.equals("")) */) {
					// VPI - Validacion XSD que estaba al ultimo - se por
					// excepcion si es diferente de null
					// Error en validacion del XSD
					int li_envio = enviaEmail("message_error", emite, "",
							"Validacion en XSD::" + ls_validaXSD, infoEmp.getDirRecibidos() + emite.getFilexml(), null);
					String fileGenerado = ArchivoUtils.archivoToString(infoEmp.getDirRecibidos() + emite.getFilexml());
					String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
					
					//============================================================
					//Metodo para registrar comprobante en BD 
					Acciones ac = new Acciones();
					CabDoc = ac.registroComprobante(emite, infoEmp, "S","Error en Estructura XML: " + ls_validaXSD,"EX");
					
					//Si es null es porque el documento cayó en excepción previa... NullPoiner, etc.. por falta de algún dato necesario y no fue almacenado
					if (CabDoc == null){
						System.out.println("Documento incompleto, actualizar su estado de error");
						ac.actualizarEstadoErrorDocumento(emite, "No se pudo registrar documento: " + ls_validaXSD);
					}
					emite.insertaBitacora(emite, "VE", "" , "Validacion en XSD: "+ ls_validaXSD+" >> "+resultEmail, fileGenerado, "", "");
				} else {

					// VPI - se comenta codigo quemado
					// Preparacion del Ruc para la firmar el documento
					emite.getInfEmisor().setRucFirmante(
							infoEmp.getRucFirmante());
					// Firmado del Documento
					if ((System.getProperty("os.name").toUpperCase()
							.indexOf("LINUX") == 0)
							|| (System.getProperty("os.name").toUpperCase()
									.indexOf("MAC") == 0)) {
						respuestaFirma = com.sun.directory.examples.ArchivoUtils
								.firmarArchivo(
										emite,
										infoEmp.getDirRecibidos()
												+ emite.getFilexml(),
										infoEmp.getDirFirmados(),
										infoEmp.getTipoFirma()/* "BCE_IKEY2032" */,
										infoEmp.getClaveFirma(),
										infoEmp.getRutaFirma());
					} else {
						respuestaFirma = com.sun.directory.examples.ArchivoUtils
								.firmarArchivo(
										emite,
										infoEmp.getDirRecibidos()
												+ emite.getFilexml(),
										infoEmp.getDirFirmados(),
										infoEmp.getTipoFirma()/* "BCE_IKEY2032" */,
										null, infoEmp.getRutaFirma());
					}
					/*
					log.info(emite.toString() + "::" + emite.toStringInfo()
							+ "::Respuesta del Firmado::" + respuestaFirma);*/
					// Respuesta del firmado del Documento
					// VPI - se invierte logica
					// if (respuestaFirma == null)
					if (respuestaFirma != null) {
						// VPI - Si hay algun fallo en el firmado != null
						// Error en Firmado del Documento

						System.out.println(" Error al firmar documeno :: "+emite.toString());
						int li_envio = enviaEmail("message_error", emite, "",
								" Error al firmar documento", infoEmp.getDirRecibidos()
									+ emite.getFilexml(), null);
						// Se regitra en bitacora error en firmado
						String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
						emite.insertaBitacora(emite, "EF", "", "::Error al firmar documento ::"+ respuestaFirma +" >> "+resultEmail,"", "", "");
						
						// VPI - Fin error en firmado
					} else {
						
						emite.insertaBitacora(emite, "FX",  "::Validacion Firmado Terminada "+ respuestaFirma, "", "", "", "");
						
						File fileFirmado = new File(infoEmp.getDirFirmados()
								+ emite.getFilexml());
						RespuestaSolicitud respuestaRecepcion = new RespuestaSolicitud();
					
					//Try - catch general del flujo principal	
					try{	
						try {
							respuestaRecepcion = solicitudRecepcion(fileFirmado, emite, timeOutRecepComp);
							if (respuestaRecepcion == null ||respuestaRecepcion.getEstado() == null) {
								respuestaRecepcion = new RespuestaSolicitud();
								respuestaRecepcion.setEstado("ERROR-GENERAL");
							}
						} catch (Exception e) {
							respuestaRecepcion = new RespuestaSolicitud();
							respuestaRecepcion.setEstado("ERROR-GENERAL");
							System.out.println("::ERROR-GENERAL - Error en recepcion de comprobante ::"+ e.getMessage() + ">> "+emite.toString());
							System.err.println("::ERROR-GENERAL - Error en recepcion de comprobante ::"+emite.toString());
							emite.insertaBitacora(emite, "EG", "", "::ERROR-GENERAL - Error en recepcion de comprobante ::"+ e.getMessage(),"", "", "");
						}
					}catch(Exception ex){
						respuestaRecepcion = new RespuestaSolicitud();
						respuestaRecepcion.setEstado("EXCEPCION-GENERAL");
						emite.insertaBitacora(emite, "EG", "", "::EXCEPCION-GENERAL - Error en recepcion de comprobante ::"+ ex.getMessage(),"", "", "");
					}

						System.out.println(emite.toString()
								+ "EstadoRecepcion::"
								+ respuestaRecepcion.getEstado());
						
						emite.insertaBitacora(emite, "ER",  "::Estado recepcion de comprobante :: "+ respuestaRecepcion.getEstado(), "", "", "", "");
						Acciones ac = new Acciones();
						String traceError ="",resultEmail="";
						
						/*************************************************************************/
						if (respuestaRecepcion.getEstado().equals("RECIBIDA")) {
							
							ac.documentoRecibido(emite, infoEmp);

						/*************************************************************************/	
						} else if (respuestaRecepcion.getEstado().equals("DEVUELTA")) {

							int respSize = respuestaRecepcion.getComprobantes()
									.getComprobante().size();
							String ls_mensaje_respuesta = "";
							String ls_tipo = "";
							String ls_mensaje = "";
							String ls_infoAdicional = "";

							if (respSize > 0) {
								for (int r = 0; r < respSize; r++) {
									ec.gob.sri.comprobantes.ws.Comprobante respuesta = respuestaRecepcion
											.getComprobantes().getComprobante()
											.get(r);
									int respMsjSize = respuesta.getMensajes()
											.getMensaje().size();
									for (int m = 0; m < respMsjSize; m++) {
										ls_tipo = respuesta.getMensajes()
												.getMensaje().get(m).getTipo();
										if (ls_tipo.equals("ERROR")) {
											ls_mensaje = respuesta
													.getMensajes().getMensaje()
													.get(m).getMensaje();
											ls_infoAdicional = respuesta
													.getMensajes().getMensaje()
													.get(m)
													.getInformacionAdicional();

											ls_mensaje_respuesta = ls_mensaje_respuesta
													+ ls_infoAdicional
													+ " ("
													+ ls_mensaje + ") " + "\n";
										}
									}
								}
							}
							
							String archivoDevuelto = infoEmp.getDirRecibidos() +"devuelta\\" + emite.getFilexml();
							ArchivoUtils.stringToArchivo(archivoDevuelto,ArchivoUtils.archivoToString(infoEmp.getDirRecibidos()+ emite.getFilexml()));
							
							delFile(emite, infoEmp.getDirFirmados(),
									infoEmp.getDirGenerado(),
									infoEmp.getDirNoAutorizados());

							//Servicio adicionales
							//Actualizacion ERP con informacion SRI 
							//============================================================
							int respUdateErp = 1;
							//Flag de autualizacion en ERP
							if(flagUpdateERP.endsWith("S")){
								respUdateErp =	rpSen.updateERPInfoSRI(emite, ls_mensaje_respuesta,"N","D");		
							}
							if(respUdateErp<=0)	{ 
								flagReproceso = true;
								traceError = "Error al actualizar en ERP - verificar enlace de datos ::Transaccion Devuelta::";
								int li_envio = ServiceDataHilo.enviaEmail("message_error", emite,
										"", ls_mensaje_respuesta+" >> "+traceError,
										infoEmp.getDirNoAutorizados()+ emite.getFilexml(), null);
							resultEmail = li_envio >= 0 ? "Mail enviado correctamente": "Error en envio de Mail";
							//============================================================
							}
							else{
								String fileGenerado = ArchivoUtils.archivoToString(archivoDevuelto);	
								int li_envio = enviaEmail("message_error", emite,
										"", "Transaccion devuelta : "+ls_mensaje_respuesta + " ruta de archivo : " +archivoDevuelto, null, null);
								
								resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
								emite.insertaBitacora(emite, "TD","" , "::Transaccion devuelta:: "+ ls_mensaje_respuesta+""
										+ " >> "+resultEmail, fileGenerado, "", "");
								//============================================================
								//Metodo para registrar comprobante en BD 
											ac = new Acciones();
											CabDoc = ac.registroComprobante(emite, infoEmp, "S","Transacción devuelta","TD");
											rpSen.updateEstadoDocumento("TD", "Transaccion devuelta : "+ls_mensaje_respuesta + " ruta de archivo : " +archivoDevuelto, emite
													.getInfEmisor().getTipoEmision(), CabDoc,
													fileGenerado);
								//============================================================
								}
							
							

						} else {
							/*************************************************************************/
							/* Validacion de Estado ELSE */
							/*************************************************************************/
							
							int clavesDisponibles = 0;
							List umbralClavesContingencia = Environment.c
									.getList("facElectronica.database.facturacion.sql.umbralClavesContingencias");

							clavesDisponibles = emite
									.verificaClavesContingencia(String
											.valueOf(emite.getInfEmisor()
													.getAmbiente()), emite
											.getInfEmisor().getRuc());
							// Verifico si hay claves disponibles para proceder
							// por contingencia
							if (clavesDisponibles > 0) {
								for (int i = 0; i < umbralClavesContingencia
										.size(); i++) {
									
									if (umbralClavesContingencia.get(i)
											.toString()
											.equals(clavesDisponibles)) {
										// Se debe enviar notificacion por mail
										// de que ya quedan
										// pocas
										// claves de contingencia
										int li_envio = enviaEmail("message_claves", emite,
												"", "Quedan : "+clavesDisponibles+" claves de contingencia disponibles.", null, null);
										
										resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
										emite.insertaBitacora(emite, "NC","" , "::Notificacion claves de contingencia :: "+ clavesDisponibles +" >> "+resultEmail, "", "", "");
										System.out
												.println(" Envio Notificacion :"
														+ umbralClavesContingencia
																.get(i)
																.toString());
									}
								}

								if (emite.getInfEmisor().getTipoEmision()
										.equals("1")) {
									emite.insertaBitacora(emite, "SR","" ,  "::Sin respuesta en proceso RecepcionComprobantes ", "", "", "");

									String ls_clave_contingencia = emite
											.obtieneClaveContingencia(emite
													.getInfEmisor().getRuc(),
													emite.getInfEmisor()
															.getAmbiente(), "0");
									String ls_clave_accesoCont="";
									/*
									String ls_clave_accesoCont = LeerDocumentos
											.generarClaveAccesoContingencia(
													emite,
													ls_clave_contingencia);
									
									if (ls_clave_accesoCont.length() != 49) {*/
										try {
											ls_clave_accesoCont = LeerDocumentos
													.generarClaveAccesoContingencia(
															emite,
															ls_clave_contingencia);
										} catch (Exception excep) {
											excep.printStackTrace();
										}
									//}
									emite.getInfEmisor().setTipoEmision("2");
									emite.getInfEmisor().setClaveAcceso(
											ls_clave_accesoCont);
									//============================================================
									//Registro de comprobantes con error en estructura del XML. Se almacenan para ser reprocesados via portal (update del estado)
									//ac = new Acciones();
									
									CabDoc = ac.registroComprobante(emite, infoEmp, "S","No Receptado SRI Contingencia","CT");
									//============================================================
	


									String ls_xml_inicial = ArchivoUtils
											.archivoToString(infoEmp
													.getDirRecibidos()
													+ emite.getFilexml()
															.replace(".xml",
																	"_backup.xml"));
									copiarXmlDir(infoEmp.getDirRecibidos()
											+ emite.getFilexml(),
											infoEmp.getDirContingencias());
									System.out
											.println("Error esquema de Contingencia ERROR-RESPUESTA");

									ModifyDocumentAcceso
											.addPutClaveAccesoContingencia(
													infoEmp.getDirContingencias()
															+ emite.getFilexml(),
													emite);
									
									
									String ls_xml = ArchivoUtils
											.archivoToString(infoEmp
													.getDirContingencias()
													+ emite.getFilexml());
									
									SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
									String strDate = sm.format(new Date());
									emite.insertaColaDocumentos(String
											.valueOf(emite.getInfEmisor()
													.getAmbiente()), emite
											.getInfEmisor().getRuc(), emite
											.getInfEmisor()
											.getCodEstablecimiento(), emite
											.getInfEmisor()
											.getCodPuntoEmision(), emite
											.getInfEmisor().getSecuencial(),
											emite.getInfEmisor()
													.getTipoComprobante(),
											strDate, "CT", infoEmp
													.getDirContingencias(),
											emite.getFilexml(),
											ls_clave_contingencia, emite
													.getInfEmisor()
													.getClaveAcceso(),
											ls_clave_accesoCont, ls_xml,
											ls_xml_inicial);
									
									rpSen.updateEstadoContingencia(
											CabDoc,
											"CT",
											"Transaccion en Contingencia de Recepcion",
											ls_clave_contingencia,
											ls_clave_accesoCont);
									emite.insertaBitacora(emite, "CT","" ,  "::Transaccion en Contingencia de Recepcion :: clave de acceso de contingencia >> "+ls_clave_accesoCont, "", "", "");
									ac.generaEnviaPdf(emite, infoEmp,
											"Transaccion en Contingencia de Recepcion");
									
									
								} else {

									// VPI - si la emision es dierente de "1"
									// osea que vuelva caer en contingencia
									emite.insertaBitacora(emite, "CT","" ,  "::Reproceso consulta RecepcionComprobantes sin Respuesta ::", "", "", "");
									
									CabDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
									CabDoc.setRuc(emite.getInfEmisor().getRuc());
									CabDoc.setCodEstablecimiento(emite.getInfEmisor().getCodEstablecimiento());
									CabDoc.setSecuencial(emite.getInfEmisor().getSecuencial());
									CabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
									CabDoc.setCodPuntEmision(emite.getInfEmisor().getCodPuntoEmision());
									// Actualizar estado del documento.
									try {
										rpSen.updateColaDocumentos(CabDoc,
												"CT");

									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// JZU Contingencia.
								}
							} else {
								// VPI - si no existen claves de contingencias
								// se retorna el archivo a generados
								// para que vuelva al flujo normal hasta que se
								// restablezca el servicio o en su defecto
								// hayan claves de contingencias disponible
								flagReproceso = true;
								traceError = "No existen claves de contingecia";
							}
							

						}
						//Reproceso del archivo se vuelve a enviar comprobante
						if(flagReproceso){
							emite.insertaBitacora(emite, "RE","" ,  "::Reproceso de Envio, "+ traceError +" :: >>"+resultEmail, "", "", "");
							ArchivoUtils.stringToArchivo(
									infoEmp.getDirGenerado()
											+ emite.getFilexml(),
									ArchivoUtils.archivoToString(emite
											.getFileXmlBackup()));
						}
					}
				}

			}
		}
	}

	//VPI - Se cambia orden 
	/*
	public void mostrar(){
		log.info("IdHilo::"+idHilo);
		log.info("infoEmp::"+emite.getFilexml());
	}*/
	
	//Se sincroniza el acceso al método
	public synchronized boolean validaEstadoDocumento(Emisor emite)throws Exception{
		boolean flagFile=false;
		String estado = "", mensaje = "";
		//log.info(">>statusDocumento::Inicio::"+emite.getFilexml()+"::"+toString());
        String ls_statusDocumento = emite.statusDocumento(emite.getInfEmisor().getAmbiente(), 
    		  										  	  emite.getInfEmisor().getRuc(), 
    		  											  emite.getInfEmisor().getTipoComprobante(), 
    		  											  emite.getInfEmisor().getCodEstablecimiento(), 
    		  											  emite.getInfEmisor().getCodPuntoEmision(), 
    		  											  emite.getInfEmisor().getSecuencial()).trim();
        
       // log.info("<<statusDocumento::Fin::"+emite.getFilexml()+"::Status::"+ls_statusDocumento);
        //Estado "RS" para el proceso lo vuelva a tomar por generados
		emite.insertaBitacora(emite, "EI",  "::ESTADO INICIAL DEL COMPROBANTE A PROCESAR "+ mensaje, "", "", "", "");
        if (ls_statusDocumento.equals("RS")){
        	Acciones ac = new Acciones();
        	leerXml(infoEmp.getDirRecibidos() + emite.getFilexml(), emite);
        	ac.documentoRecibido(emite, infoEmp);
        	mensaje = " El Documento ya se encuentra en estado RECIBIDO por lo cual "
        			 +" solo se procede a consultar su estado de autorizacion "
  	    		  +"Ruc::" + emite.getInfEmisor().getRuc()+ " Establecimiento::"+emite.getInfEmisor().getCodEstablecimiento()
  	    		  +" Punto de Emision::"+emite.getInfEmisor().getCodPuntoEmision()+" Secuncial::"+emite.getInfEmisor().getSecuencial()
  	    		  +" Tipo de Documento::"+emite.getInfEmisor().getTipoComprobante()+" Ambiente::"+emite.getInfEmisor().getAmbiente();
        	//log.warn("statusDocumento::ExceptionDefinida::"+emite.getFilexml()+"::Status::"+ls_statusDocumento+"::Mensaje::"+mensaje);
			
			emite.insertaBitacora(emite, "VC",  "::VALIDACION COMPROBANTE"+ mensaje, "", "", "", "");
        	throw new Exception(emite.toString()+mensaje);
        }

        if (ls_statusDocumento.equals("AT")||ls_statusDocumento.equals("SR")){
	    	if (ls_statusDocumento.equals("AT")){
	    		estado = "AUTORIZADO";
	    	}
	    	
	    	if (ls_statusDocumento.equals("RS")){
	    		estado = "Recibido por el SRI";
	    	}
	    	if (ls_statusDocumento.equals("CT")){
	    		estado = "Contingencia en Recepcion del SRI";
	    	}
    	    flagFile = true;
    	    mensaje = estado+",El Documento ya se encuentra en estado "+ estado +" por lo cual no se procede a reprocesar. " +
    	    		  "Ruc::" + emite.getInfEmisor().getRuc()+ " Establecimiento::"+emite.getInfEmisor().getCodEstablecimiento()+
    	    		  " Punto de Emision::"+emite.getInfEmisor().getCodPuntoEmision()+" Secuncial::"+emite.getInfEmisor().getSecuencial()+
    	    		  " Tipo de Documento::"+emite.getInfEmisor().getTipoComprobante()+" Ambiente::"+emite.getInfEmisor().getAmbiente();
    	    //log.warn("statusDocumento::ExceptionDefinida::"+emite.getFilexml()+"::Status::"+ls_statusDocumento+"::Mensaje::"+mensaje);
			emite.insertaBitacora(emite, "VC",  "::VALIDACION COMPROBANTE"+ mensaje, "", "", "", "");
    	    throw new Exception(emite.toString()+mensaje);
    	    
        }
        
        //log.info(">>statusDocumentoContingencia::Inicio::"+emite.getFilexml()+"::"+toString());
        
        String ls_estadoContingencia = emite.getStatusDocumentoContingencia(emite.getInfEmisor().getAmbiente(), 
																	  	    emite.getInfEmisor().getRuc(), 
																		    emite.getInfEmisor().getTipoComprobante(), 
																		    emite.getInfEmisor().getCodEstablecimiento(), 
																		    emite.getInfEmisor().getCodPuntoEmision(), 
																		    emite.getInfEmisor().getSecuencial());
        //log.info("<<statusDocumentoContingencia::Fin::"+emite.getFilexml()+"::existsContingencia::"+li_existsContingencia+"::estadoContingencia::"+ls_estadoContingencia);											        
        //if (li_existsContingencia >= 1){
        if (ls_estadoContingencia==null) ls_estadoContingencia = "";
        if (ls_estadoContingencia.equals("PC")){
        	 mensaje = estado+",El Documento ya se encuentra Procesando en estado estado de Contigencia por lo cual no se procede a reprocesar. Ruc::" +
   	    		  emite.getInfEmisor().getRuc()+ " Establecimiento::"+emite.getInfEmisor().getCodEstablecimiento()+" Punto de Emision::"+
   	    		  emite.getInfEmisor().getCodPuntoEmision()+" Secuncial::"+emite.getInfEmisor().getSecuencial()+" Tipo de Documento::"+
   	    		  emite.getInfEmisor().getTipoComprobante()+" Ambiente::"+emite.getInfEmisor().getAmbiente();
        	 //log.warn("**statusDocumentoContingencia::ExceptionDefinida::"+emite.getFilexml()+"::existsContingencia::"+li_existsContingencia+"::Mensaje::"+mensaje);
			 emite.insertaBitacora(emite, "VC",  "::VALIDACION COMPROBANTE"+ mensaje, "", "", "", "");
        	 throw new Exception(emite.toString()+mensaje);
        }else if(ls_estadoContingencia.equals("CT")){
        		emite.getInfEmisor().setTipoEmision("2");
        		flagFile= true; //Puede Procesar por Contingencia
        	 
        }else{
        	flagFile= true; //Puede Procesar Normalmente
        }
		return flagFile;
	}
	
	public void validacionAdicional(Emisor emite)throws Exception{
		String mensaje = "";
	    /*Validaciones Adicionales*/
	  	//Verificacion de si existe Establecimiento Configurado en la tabla fac_establecimiento
		if (!emite.existeEstablecimiento(emite.getInfEmisor().getRuc(),emite.getInfEmisor().getCodEstablecimiento())){
   		 	mensaje = " Establecimiento no existe o no se encuentra Activa. Ruc->" +emite.getInfEmisor().getRuc()+ 
   		 			  " Establecimient"+emite.getInfEmisor().getCodEstablecimiento();          
   		 	//log.error(mensaje);  
   		 	int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
   		 	String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite,"EX","", mensaje +" >> "+resultEmail,  "", "", "");
   		 	//log.error("Envio de Mail Error ::"+li_envio);
   	        throw new Exception(mensaje);
   	 	}
	   	//Verificacion de si existe Punto de Emision Configurado en la tabla fac_punto_emision
	   	if (!emite.existePuntoEmision(emite.getInfEmisor().getRuc(),emite.getInfEmisor().getCodEstablecimiento(), emite.getInfEmisor().getCodPuntoEmision())){
	   	       mensaje = " Punto de Emision no existe o no se encuentra Activo. Ruc->" 
	   	    		   	 +emite.getInfEmisor().getRuc()+ " Establecimiento"
	   	    		   	 +emite.getInfEmisor().getCodEstablecimiento()
	   	    		   	 + " Punto Emision"
	   	    		   	 +emite.getInfEmisor().getCodPuntoEmision();
	   	       
	   	       //log.error(mensaje);  
	   	       int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
	   	       String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
	   	       emite.insertaBitacora(emite,"EX","", mensaje +" >> "+resultEmail,  "", "", "");
	   	       //log.error("Envio de Mail Error ::"+li_envio);
	   	       throw new Exception(mensaje);
	   	}
	   	//Verificacion de si existe documento en Configurado en Punto de Emision
	   	if (!emite.existeDocumentoPuntoEmision(emite.getInfEmisor().getRuc(),emite.getInfEmisor().getCodEstablecimiento(), emite.getInfEmisor().getCodPuntoEmision(), emite.getInfEmisor().getCodDocumento())){
	   	       mensaje = " En el Establecimiento el tipo de Documento no existe o no se encuentra Activo. Ruc->" 
	   	    		   	 +emite.getInfEmisor().getRuc()+ " Establecimient"
	   	    		   	 +emite.getInfEmisor().getCodEstablecimiento()
	   	    		   	 +" Tipo de Document"+emite.getInfEmisor().getCodDocumento();
	   	       
	   	       //log.error(mensaje);
	   	       int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
		   	    String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
				emite.insertaBitacora(emite, "EX","", mensaje +" >> "+resultEmail,  "", "", "");
	   	       //log.error("Envio de Mail Error ::"+li_envio);
	   	       throw new Exception(mensaje);
	   	}
	   	 //Obtencion del ambiente de la tabla fac_punto_emision
	   	 //emite.getInfEmisor().setAmbiente(1);
	   	 //log.info("Ambiente::"+emite.getInfEmisor().getAmbiente());
	   	 //emite.getInfEmisor().setAmbiente(Integer.parseInt(emite.ambienteDocumentoPuntoEmision(emite.getInfEmisor().getRuc(),emite.getInfEmisor().getCodEstablecimiento(), emite.getInfEmisor().getCodPuntoEmision(), emite.getInfEmisor().getCodDocumento())));      
	     if (emite.getInfEmisor().getAmbiente()==-1){
   	       mensaje = " Revise el valor del Ambiente-> "
   	    		   	 +emite.getInfEmisor().getAmbiente()+". Ruc->" 
   	    		   	 +emite.getInfEmisor().getRuc()
   	    		   	 + " Establecimient"
   	    		   	 +emite.getInfEmisor().getCodEstablecimiento()
   	    		   	 +" Tipo de Document"
   	    		   	 +emite.getInfEmisor().getCodDocumento();
   	       
   	       //log.error(mensaje);
   	       int li_envio = enviaEmail("message_error", emite,  mensaje,mensaje,null, null);
	   	    String resultEmail = li_envio >= 0 ? "Mail enviado Correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite,"EX","", mensaje +" >> "+resultEmail,  "", "", "");
   	       //log.error("Envio de Mail Error ::"+li_envio);
   	       throw new Exception(mensaje);
	     }    	        
	    //Obtencion del mail del establecimiento
	     emite.obtieneMailEstablecimiento(emite.getInfEmisor());		        
	     emite.setResultado(0);
	}

	public static String getXML(String path) throws IOException{
		FileInputStream input = new FileInputStream( new File(path));

		 byte[] fileData = new byte[input.available()];

		 input.read(fileData);
		 input.close();
		 String resultadoXml = new String(fileData, "UTF-8");
		 return resultadoXml;
	}
	
	//***********************//////////////////////////////////////////////////////************************************//
	/*									   	Envios al Sri por WebServices											  */
	//***********************//////////////////////////////////////////////////////************************************//	
	public static RespuestaSolicitud solicitudRecepcion(File archivoFirmado,Emisor emi,int timeout)
	{
		ec.gob.sri.comprobantes.ws.RespuestaSolicitud respuestaRecepcion = null;
		try{		
		//String flagErrores = "";	
			respuestaRecepcion = new RespuestaSolicitud();
	    	respuestaRecepcion = EnvioComprobantesWs.obtenerRespuestaEnvio(archivoFirmado, 
	    																   emi.getInfEmisor().getRuc(), 
	    																   emi.getInfEmisor().getCodDocumento(), 
	    																   emi.getInfEmisor().getClaveAcceso(), 
	    																   FormGenerales.devuelveUrlWs(new Integer(emi.getInfEmisor().getAmbiente()).toString() ,"RecepcionComprobantes"),
	    																   timeout);
		}catch(Exception exc){
			respuestaRecepcion.setEstado(exc.getMessage());    
	    }
		return respuestaRecepcion;		
	}	
	//***********************//////////////////////////////////////////////////////************************************//
	/*									   	Envios de Mail															  */
	//***********************//////////////////////////////////////////////////////************************************//		
	public static int enviaEmail(String ls_id_mensaje, Emisor emi, String mensaje_mail, String mensaje_error, String fileAttachXml, String fileAttachPdf){
		String resultEnvioMail = "";
		String emailMensaje = "";
		String emailHost = "";
		String emailFrom = "";
		String emailTo = "";	
		String emailHelpDesk = "";
		
	       //Obtener nombre y direccion IP del equipo local juntos
	       InetAddress direccion = null;
			try {
				direccion = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
	       String nombreEquipo = "["+direccion.getHostName()+"-"+direccion.getHostAddress()+"] - ";
	       
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);
		String host = Environment.c.getString("facElectronica.alarm.email.host");
		String helpdesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		emailHost = host;
		emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
		emailHelpDesk= helpdesk;
		EmailSender emSend = new EmailSender(emailHost,emailFrom);
		String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String receivers = "";
		String user = Environment.c.getString("facElectronica.alarm.email.user");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		String port = Environment.c.getString("facElectronica.alarm.email.port");
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");
		String noDocumento = "";		
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			noDocumento = emi.getInfEmisor().getCodEstablecimiento()+emi.getInfEmisor().getCodPuntoEmision()+emi.getInfEmisor().getSecuencial();
			emailMensaje = emailMensaje.replace("|FECHA|", (emi.getInfEmisor().getFecEmision()==null?"":emi.getInfEmisor().getFecEmision().toString()));
			emailMensaje = emailMensaje.replace("|NODOCUMENTO|", (noDocumento==null?"":noDocumento));
			//PARA ERROR 
			emailMensaje = emailMensaje.replace("|MOV|", (emi.getInfEmisor().getIdMovimiento() ==null?"":emi.getInfEmisor().getIdMovimiento())); 
		}		

		emailMensaje = emailMensaje.replace("|HELPDESK|", emailHelpDesk);
		emailMensaje = StringEscapeUtils.unescapeHtml(emailMensaje);
		
		if (ls_id_mensaje.equals("message_error"))
		{
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			receivers = infoEmp==null?receivers:infoEmp.getMailEmpresa().trim();
			subject = "ERROR EN DOCUMENTO ELECTRONICO NO. "+(noDocumento==null?"":noDocumento);
			emailMensaje = emailMensaje.replace("|CabError|", "Hubo inconvenientes con");
			emailMensaje = emailMensaje.replace("|Mensaje|", mensaje_error);
		}
		if (ls_id_mensaje.equals("message_contingencia"))
		{
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			receivers = infoEmp==null?receivers:infoEmp.getMailEmpresa().trim();
			subject = "CONTG EN DOCUMENTO ELECTRONICO NO. "+(noDocumento==null?"":noDocumento);
			emailMensaje = emailMensaje.replace("|CabError|", "Hubo inconvenientes con");
			emailMensaje = emailMensaje.replace("|Mensaje|", mensaje_error);
		}
		if (ls_id_mensaje.equals("message_claves"))
		{
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			receivers = infoEmp==null?receivers:infoEmp.getMailEmpresa().trim();
			subject = Environment.c.getString("facElectronica.alarm.email.subject_soporte");
			subject = "CLAVES DE CONTINGENCIA DISPONIBLES ";
			emailMensaje = emailMensaje.replace("|CabError|", "Favor verificar disponibilidad de claves de contingecias : ");
			emailMensaje = emailMensaje.replace("|Mensaje|", mensaje_error);
		}
		if(ls_id_mensaje.endsWith("message_service_down")){
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			receivers = infoEmp==null?receivers:infoEmp.getMailEmpresa().trim();
			subject = Environment.c.getString("facElectronica.alarm.email.subject_service");
			subject = "DOWN "+subject;
			emailMensaje = emailMensaje.replace("|FECHA|", mensaje_error);
		}
		if(ls_id_mensaje.endsWith("message_service_up")){
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			receivers = infoEmp==null?receivers:infoEmp.getMailEmpresa().trim();
			subject = Environment.c.getString("facElectronica.alarm.email.subject_service");
			subject = "UP "+subject;
			emailMensaje = emailMensaje.replace("|FECHA|", mensaje_error);
		}
		if (ls_id_mensaje.equals("message_exito"))
		{
			
			emailMensaje = emailMensaje.replace("|CabMensaje|", " ");
			String subjectMensaje = subject;
			//VPI - Se parametriza subjectMensaje
			subjectMensaje = subjectMensaje.replace("|NOMEMAIL|", nombreEmpresa).toString();
			subjectMensaje = subjectMensaje.replace("|NUMDOC|", (noDocumento==null?"":noDocumento)).toString();
			String ls_tipoDoc = "";
			if (emi.getInfEmisor().getCodDocumento().equals("01")){
				ls_tipoDoc = "Ha recibido una FACTURA";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("04")){
				ls_tipoDoc = "Ha recibido una NOTA DE CREDITO";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("07")){
				ls_tipoDoc = "Ha recibido un COMPROBANTE DE RETENCION";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("06")){
				ls_tipoDoc = "Ha recibido una Guia de Remision";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("05")){
				ls_tipoDoc = "Ha recibido una NOTA DE DEBITO";
			}
			subjectMensaje = subjectMensaje.replace("|TIPODOC|", ls_tipoDoc).toString();			
			System.out.println("Subject Message :"+subjectMensaje);
			subject = subjectMensaje;
		}
		emSend.setSubject(subject);
		emSend.setPassword(clave);
		emSend.setUser(user);
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);
		emSend.setPort(port);
		
		emailTo = receivers;
		//Añadir nombre del equipo al msj 
		subject = nombreEquipo + subject;
		if ((emailTo!=null) && (emailTo.length()>0)){
			//String[] partsMail = emailTo.split(";");

			resultEnvioMail = emSend.send(emailTo
							    //partsMail[i]
							, 
								subject,
								emailMensaje,
								fileAttachXml,
								fileAttachPdf);
		}
		//VPI 
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else{
			System.err
			.println("ServiceDataHilo.enviaEmailCliente() >> Error al enviar email - "
					+ "Email : "
					+ emailMensaje
					+ " Subject :"
					+ subject
					+ "Error : "
					+ resultEnvioMail)
					;
		return -1;
		}
	}
	
	public static int enviaEmailCliente(String ls_id_mensaje, Emisor emi, String mensaje_error, String fileAttachXml, String fileAttachPdf){
		
		
		String resultEnvioMail = "";
		String host = Environment.c.getString("facElectronica.alarm.email.host");
		String helpdesk = Environment.c.getString("facElectronica.alarm.email.helpdesk");
		String portal = Environment.c.getString("facElectronica.alarm.email.portal");
		String emailMensaje = "";
		String emailHost = "";
		String emailFrom = "";	
		String emailHelpDesk = "";
		emailHost = host;
		emailFrom = Environment.c.getString("facElectronica.alarm.email.sender");
		EmailSender emSend = new EmailSender(emailHost,emailFrom);	
		emailHelpDesk =helpdesk;
		String nombreEmpresa= Environment.c.getString("facElectronica.alarm.email.nombreEmpresa");
		emailMensaje = Environment.c.getString("facElectronica.alarm.email."+ls_id_mensaje);		
		String ambiente = Environment.c.getString("facElectronica.alarm.email.ambiente");
		String clave = Environment.c.getString("facElectronica.alarm.email.password");
		
		String user = Environment.c.getString("facElectronica.alarm.email.user");
		String subject = Environment.c.getString("facElectronica.alarm.email.subject");
		String tipo_autentificacion = Environment.c.getString("facElectronica.alarm.email.tipo_autentificacion");
		String tipoMail = Environment.c.getString("facElectronica.alarm.email.tipoMail");
		String port = Environment.c.getString("facElectronica.alarm.email.port");
		
		String receivers = "";		
		if (ambiente.equals("PRUEBAS")){
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list");
		}else{
			//String emailCli = null;
			
			/*
			if (emi.getInfEmisor().getMailCliente()!=null)
				emailCli  ="email@email.com";
			else
				emailCli = null;
			
			if ((emailCli.equals("email@email.com"))||(emailCli.equals("notiene"))){
				receivers =null;  
			}else{
				receivers = emailCliente;
			}*/
			
			if (emi.getInfEmisor().getMailCliente()!=null){
				if(emi.getInfEmisor().getMailCliente().equals("notiene")){
					return -1;// Retorno -1 para que no envie correo en caso que no tenga 
				}
				receivers = emi.getInfEmisor().getMailCliente();
			}else{
				receivers  ="email@email.com";
			}
			
		}	
		
		if (receivers!=null){
		String noDocumento = "";		
		if ((emi.getInfEmisor().getCodEstablecimiento()!=null)&&(emi.getInfEmisor().getCodPuntoEmision()!=null)&&(emi.getInfEmisor().getSecuencial()!=null)){
			//System.out.println("Envio de Email");
			noDocumento = emi.getInfEmisor().getCodEstablecimiento()+emi.getInfEmisor().getCodPuntoEmision()+emi.getInfEmisor().getSecuencial();
		}		
		
		
		emailMensaje = emailMensaje.replace("|FECHA|", (emi.getInfEmisor().getFecEmision()==null?"":emi.getInfEmisor().getFecEmision().toString()));
		emailMensaje = emailMensaje.replace("|NODOCUMENTO|", (noDocumento==null?"":noDocumento));	
		emailMensaje = emailMensaje.replace("|HELPDESK|", emailHelpDesk);
		emailMensaje = emailMensaje.replace("|CLIENTE|", emi.getInfEmisor().getRazonSocialComp());
		if (emi.getInfEmisor().getIdentificacionComp()!=null)
		//emailMensaje = emailMensaje.replace("|CODCLIENTE|", emi.getInfEmisor().getIdentificacionComp().substring(0, 6).toString());
		emailMensaje = emailMensaje.replace("|PORTAL|", portal);
		String ls_tipoDocumento ="";
		if (emi.getInfEmisor().getCodDocumento().equals("01"))
			ls_tipoDocumento ="Factura";
		if (emi.getInfEmisor().getCodDocumento().equals("04"))
			ls_tipoDocumento ="Nota de Credito";
		if (emi.getInfEmisor().getCodDocumento().equals("07"))
			ls_tipoDocumento ="Comprobante de Retencion";
		emailMensaje = emailMensaje.replace("|TIPODOCUMENTO|", ls_tipoDocumento);
		
		emailMensaje = StringEscapeUtils.unescapeHtml(emailMensaje);
		//System.out.println("Content Message :"+emailMensaje);
		//System.out.println("Subject Message :"+subjectMensaje);

		//StringEscapeUtils.escapeJava()
		if (ls_id_mensaje.equals("message_error"))
		{
			receivers = Environment.c.getString("facElectronica.alarm.email.receivers-list-error");
			subject = "Error en documento electronico "+nombreEmpresa;
			emailMensaje = emailMensaje.replace("|CabError|", "Hubo inconvenientes con");
			emailMensaje = emailMensaje.replace("|Mensaje|", mensaje_error);
		}
		if (ls_id_mensaje.equals("message_exito"))
		{
			String subjectMensaje = subject;
			//VPI - se parametriza Subject
			subjectMensaje = subjectMensaje.replace("|NOMEMAIL|", nombreEmpresa).toString();
			//subjectMensaje = subjectMensaje.replace("|NOMEMAIL|", "Banco DMIRO S.A.").toString();
			subjectMensaje = subjectMensaje.replace("|NUMDOC|", (noDocumento==null?"":noDocumento)).toString();
			String ls_tipoDoc = "";
			if (emi.getInfEmisor().getCodDocumento().equals("01")){
				ls_tipoDoc = "Ha recibido una FACTURA";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("04")){
				ls_tipoDoc = "Ha recibido una NOTA DE CREDITO";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("07")){
				ls_tipoDoc = "Ha recibido un COMPROBANTE DE RETENCION";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("06")){
				ls_tipoDoc = "Ha recibido una Guia de Remision";
			}
			if (emi.getInfEmisor().getCodDocumento().equals("05")){
				ls_tipoDoc = "Ha recibido una NOTA DE DEBITO";
			}
			subjectMensaje = subjectMensaje.replace("|TIPODOC|", ls_tipoDoc).toString();			
			System.out.println("Subject Message :"+subjectMensaje);			
			emailMensaje = emailMensaje.replace("|CabMensaje|", " ");
			subject = subjectMensaje;
		}
		emSend.setPassword(clave);
		emSend.setSubject(subject);
		emSend.setUser(user);		
		emSend.setAutentificacion(tipo_autentificacion);
		emSend.setTipoMail(tipoMail);
		emSend.setPort(port);
		
		
		if (receivers!=null){
			/*
			if (receivers!=null){
				emailCliente = receivers;
			}*/
			if ((receivers!=null) && (receivers.length()>0)){
				String[] partsMail = receivers.split(";");
				
						resultEnvioMail = emSend.send(
								receivers
								// partsMail[i]
								, subject, emailMensaje, fileAttachXml,
								fileAttachPdf);
					
			}
			}
		}else{
			resultEnvioMail = "Sin Email";
		}
		if (resultEnvioMail.equals("Enviado"))
			return 0;
		else if (resultEnvioMail.equals("Sin Email"))
			return 0;
		else
			//VPI
			System.err
			.println("ServiceDataHilo.enviaEmailCliente() >> Error al enviar email - "
					+ "Email : "
					+ receivers
					+ " Subject :"
					+ subject
					+ "Error : "
					+ resultEnvioMail);
			return -1;
	}	
	
	
	//***********************//////////////////////////////////////////////////////************************************//
	/*							   	Manejo de Archivos del XML														  */
	//***********************//////////////////////////////////////////////////////************************************//	
	//Copiar Xml
	public static boolean copiarXml(String fileName){
		String fileBackup = "";
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
	    	  return true;	    	  
	      }
	      else{
	    	  return false;
	      }
		}catch(IOException e){
			return false;
		}
	}
	
	public static boolean copiarXmlDir(String fileName, String dirDestino){
		String fileBackup = "";
		try{
		  File fileOrigen = new File(fileName);
	      File fileDestino = new File(dirDestino+""+fileOrigen.getName());	      
	      if (fileOrigen.exists()) {	    	  
	    	  InputStream in = new FileInputStream(fileOrigen);
	    	  OutputStream out = new FileOutputStream(fileDestino);
	    	  byte[] buf = new byte[1024];int len; while ((len = in.read(buf)) > 0) {  out.write(buf, 0, len);}
	    	  in.close();
	    	  out.close();
	    	  fileBackup = fileName.replace(".xml", "_backup.xml");
	    	  return true;	    	  
	      }
	      else{
	    	  return false;
	      }
		}catch(IOException e){
			return false;
		}
	}
	//Eliminacion de Archivos Firmados, Generados y No Autorizados.
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
  	  /*
  	  eliminar = new File(generado+emite.getFileXmlBackup());
  	  if (eliminar.exists()) {
  		  eliminar.delete();
  	  }
  	  			            	  
  	  */				            	  
  	  if (fileDel.exists()) {
  		  fileDel.delete();
  	  }
  	  System.out.println("Delete File");
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
	//***********************//////////////////////////////////////////////////////************************************//
	/*								Lectura del Nombre del XML														  */
	//***********************//////////////////////////////////////////////////////************************************//	
		public void obtieneInfoXml(String FileName) throws Exception{
		//Ambiente
		String ambiente = null;
		try{
			ambiente = FileName.substring(0, 1).trim();
		}catch (Exception e){
			throw new Exception("Name File incorrecto->Ambiente::"+e.toString());
		}		
		if (ambiente== null){
			throw new Exception("Name File incorrecto->Ambiente es null");
		}			
		if (ambiente.length()!= 1){
			throw new Exception("Name File incorrecto->Ambiente tamaño incorrecto::"+ambiente.length());
		}
		emite.getInfEmisor().setAmbiente(Integer.parseInt(ambiente));
		
		//Ruc
		String rucFile = null;
		try{
			rucFile = FileName.substring(1, 14).trim();
		}catch (Exception e){
			throw new Exception("Name File incorrecto->Ruc::"+e.toString());
		}
		if (rucFile== null){
			throw new Exception("Name File incorrecto->Ruc es null");
		}
		if (rucFile.length()!= 13){
			throw new Exception("Name File incorrecto->Ruc tamaño incorrecto::"+rucFile.length());
		}
		emite.getInfEmisor().setRuc(rucFile);
		
		
		//TipoDocumento
		String tipoDocumento= null;
		try{
			tipoDocumento = FileName.substring(14, 16).trim();
		}catch (Exception e){
			throw new Exception("Name File incorrecto->TipoDocumento::"+e.toString());
		}
		if (tipoDocumento== null){
			throw new Exception("Name File incorrecto->TipoDocumento es null");
		}
		if (tipoDocumento.length()!= 2){
			throw new Exception("Name File incorrecto->TipoDocumento tamaño incorrecto::"+tipoDocumento.length());
		}
		emite.getInfEmisor().setTipoComprobante(tipoDocumento);	     
	    emite.getInfEmisor().setCodDocumento(tipoDocumento);
	    
	    //CodEstablecimiento
	    String CodEstablecimiento = null;
	    try{
	    	CodEstablecimiento = FileName.substring(16, 19).trim();
	    }catch (Exception e){
			throw new Exception("Name File incorrecto->CodEstablecimiento::"+e.toString());
		}
	    if (CodEstablecimiento== null){
			throw new Exception("Name File incorrecto->CodEstablecimiento es null");
		}
	    if (CodEstablecimiento.length()!= 3){
			throw new Exception("Name File incorrecto->CodEstablecimiento tamaño incorrecto::"+CodEstablecimiento.length());
		}
	    emite.getInfEmisor().setCodEstablecimiento(CodEstablecimiento);
	    
	    //CodPuntEmision
	    String CodPuntEmision = null;
	    try{
	    	CodPuntEmision = FileName.substring(19, 22);
	    }catch (Exception e){
			throw new Exception("Name File incorrecto->CodPuntEmision::"+e.toString());
		}
	    if (CodPuntEmision== null){
			throw new Exception("Name File incorrecto->CodPuntEmision es null");
		}
	    if (CodPuntEmision.length()!= 3){
			throw new Exception("Name File incorrecto->CodPuntEmision tamaño incorrecto::"+CodPuntEmision.length());
		}
	    emite.getInfEmisor().setCodPuntoEmision(CodPuntEmision);
	    
	    //secuencial
	    String secuencial = null;
	    try{
	    secuencial =  FileName.substring(22, (FileName.length()<=9?FileName.length():31));
	    }catch (Exception e){
			throw new Exception("Name File incorrecto->secuencial::"+e.toString());
		}
	    if (secuencial.length()!= 9){
			throw new Exception("Name File incorrecto->CodPuntEmision tamaño incorrecto::"+secuencial.length());
		}
	    emite.getInfEmisor().setSecuencial(secuencial);
	}
	
	
	//***********************//////////////////////////////////////////////////////************************************//
	/*								Lectura del XML																	  */
	//***********************//////////////////////////////////////////////////////************************************//
	
	public static void leerXml(String nameXml, Emisor emite) {
		System.out.println(emite.toString()+"TipoComprobante::"+emite.getInfEmisor().getTipoComprobante()+"::Leer Xml");
		if (emite.getInfEmisor().getTipoComprobante().equals("01"))
			leerFacturaXml(nameXml, emite);
		if (emite.getInfEmisor().getCodDocumento().equals("04"))
			leerNotaCreditoXml(nameXml, emite);
		if (emite.getInfEmisor().getCodDocumento().equals("05"))
			leerNotaDebitoXml(nameXml, emite);		
		// INI HFU - SE DESCOMENTÓ
		if (emite.getInfEmisor().getCodDocumento().equals("07"))
			leerComprobanteRetXml(nameXml, emite);
		// FIN HFU
			
	}
	
	//Lectura de Factura
	public static void leerFacturaXml(String nameXml, Emisor emite) {
		System.out.println(emite.toString()+"TipoComprobante::"+emite.getInfEmisor().getTipoComprobante()+"::Leer Xml Factura");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(nameXml);
            System.out.println("File>>"+nameXml);
 
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();
 
            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            
            //infoTributaria            
            XPathExpression expr = xpath.compile("/factura/infoTributaria/tipoEmision/text()");
            emite.getInfEmisor().setTipoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            //Se quita validacion para que se lea clave de acceso
            //if (emite.getInfEmisor().getTipoEmision().equals("2")){
            	expr = xpath.compile("/factura/infoTributaria/claveAcceso/text()");
                emite.getInfEmisor().setClaveAcceso((String) expr.evaluate(doc, XPathConstants.STRING));
           // }
            expr = xpath.compile("/factura/infoTributaria/ambiente/text()");
            emite.getInfEmisor().setAmbiente(Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/factura/infoTributaria/ruc/text()");
            emite.getInfEmisor().setRuc((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoTributaria/razonSocial/text()");
            emite.getInfEmisor().setRazonSocial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoTributaria/nombreComercial/text()");
            emite.getInfEmisor().setNombreComercial((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/factura/infoTributaria/codDoc/text()");
            emite.getInfEmisor().setCodDocumento((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoTributaria/estab/text()");
            emite.getInfEmisor().setCodEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/factura/infoTributaria/ptoEmi/text()");
            emite.getInfEmisor().setCodPuntoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoTributaria/secuencial/text()");
            emite.getInfEmisor().setSecuencial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoTributaria/dirMatriz/text()");
            emite.getInfEmisor().setDireccionMatriz((String) expr.evaluate(doc, XPathConstants.STRING));                        
            
            //infoFactura
            expr = xpath.compile("/factura/infoFactura/fechaEmision/text()");
            emite.getInfEmisor().setFecEmision((String)expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/factura/infoFactura/dirEstablecimiento/text()");
            emite.getInfEmisor().setDireccionEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
            
            //expr = xpath.compile("/factura/infoFactura/contribuyenteEspecial/text()");
            //emite.getInfEmisor().setContribEspecial(Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/factura/infoFactura/obligadoContabilidad/text()");
            emite.getInfEmisor().setObligContabilidad((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/tipoIdentificacionComprador/text()");
            emite.getInfEmisor().setTipoIdentificacion((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/guiaRemision/text()");
            emite.getInfEmisor().setGuiaRemision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/razonSocialComprador/text()");
            emite.getInfEmisor().setRazonSocialComp((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/identificacionComprador/text()");
            emite.getInfEmisor().setIdentificacionComp((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/totalSinImpuestos/text()");
            emite.getInfEmisor().setTotalSinImpuestos(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/factura/infoFactura/totalDescuento/text()");
            emite.getInfEmisor().setTotalDescuento(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));
                        
            expr = xpath.compile("/factura/infoFactura/propina/text()");
            emite.getInfEmisor().setPropina(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/factura/infoFactura/importeTotal/text()");
            emite.getInfEmisor().setImporteTotal(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/factura/infoFactura/moneda/text()");
            emite.getInfEmisor().setMoneda((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/factura/infoFactura/totalConImpuestos/totalImpuesto[*]/codigo/text()");
            List<String> listCodigo = new ArrayList();            
            NodeList nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigo.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/infoFactura/totalConImpuestos/totalImpuesto[*]/codigoPorcentaje/text()");
            List<String> listCodigoPorcentaje = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigoPorcentaje.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/infoFactura/totalConImpuestos/totalImpuesto[*]/baseImponible/text()");
            List<String> listBaseImponible = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listBaseImponible.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/infoFactura/totalConImpuestos/totalImpuesto[*]/valor/text()");
            List<String> listValor = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listValor.add(nodes.item(i).getNodeValue());
            }
            ArrayList<DetalleTotalImpuestos> listDetDetImpuestos = new ArrayList<DetalleTotalImpuestos>();             
            for (int i=0; i<listCodigo.size(); i++){
            	DetalleTotalImpuestos detImp = new DetalleTotalImpuestos();
            	detImp.setCodTotalImpuestos(Integer.parseInt(listCodigo.get(i).toString()));
            	detImp.setCodPorcentImp(Integer.parseInt(listCodigoPorcentaje.get(i).toString()));
            	detImp.setBaseImponibleImp(Double.parseDouble(listBaseImponible.get(i).toString()));
            	detImp.setValorImp(Double.parseDouble(listValor.get(i).toString()));
            	listDetDetImpuestos.add(detImp);
            }
            
            emite.getInfEmisor().setListDetDetImpuestos(listDetDetImpuestos);
            
            ArrayList<DetalleDocumento> listDetDocumentos = new ArrayList<DetalleDocumento>();
                                               
            expr = xpath.compile("/factura/detalles/detalle[*]/codigoPrincipal/text()");
            List<String> listCodPrin = new ArrayList();
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int l=0; l<nodes.getLength(); l++){
            	listCodPrin.add(nodes.item(l).getNodeValue());
            	//System.out.println("index::"+i);
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/codigoAuxiliar/text()");
            List<String> listCodAux = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodAux.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/descripcion/text()");
            List<String> listDescrip = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescrip.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/cantidad/text()");
            List<String> listCantidad = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCantidad.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/precioUnitario/text()");
            List<String> listPrecioUnitario = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioUnitario.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/descuento/text()");
            List<String> listDescuento = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescuento.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/factura/detalles/detalle[*]/precioTotalSinImpuesto/text()");
            List<String> listPrecioTotalSinImpuesto = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioTotalSinImpuesto.add(nodes.item(i).getNodeValue());
            }
            
            for (int i=0; i<listCodPrin.size(); i++){
            	ArrayList<DocumentoImpuestos> listDetImpuestosDocumentos = new ArrayList<DocumentoImpuestos>();            	
            	DetalleDocumento detDoc = new DetalleDocumento();            	
            	detDoc.setCodigoPrincipal(listCodPrin.get(i).toString());
            	if (listCodAux.size()>0)
            	detDoc.setCodigoAuxiliar(listCodAux.get(i).toString());
            	if (listDescrip.size()>0)
            	detDoc.setDescripcion(listDescrip.get(i).toString());
            	if (listCantidad.size()>0)
            	detDoc.setCantidad(Double.parseDouble(listCantidad.get(i).toString()));
            	if (listPrecioUnitario.size()>0)
            	detDoc.setPrecioUnitario(Double.parseDouble(listPrecioUnitario.get(i).toString()));
            	if (listDescuento.size()>0)
            	detDoc.setDescuento(Double.parseDouble(listDescuento.get(i).toString()));
            	if (listPrecioTotalSinImpuesto.size()>0)
            	detDoc.setPrecioTotalSinImpuesto(Double.parseDouble(listPrecioTotalSinImpuesto.get(i).toString()));
            	//detDoc.setListDetImpuestosDocumentos();
            	
            	expr = xpath.compile("/factura/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigo/text()");
                List<String> listCodigoImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/factura/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigoPorcentaje/text()");
                List<String> listCodigoPorcentajeImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoPorcentajeImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/factura/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/tarifa/text()");
                List<String> listTarifaImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listTarifaImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/factura/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/baseImponible/text()");
                List<String> listBaseImponibleImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listBaseImponibleImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/factura/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/valor/text()");
                List<String> listValorImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listValorImpuesto.add(nodes.item(j).getNodeValue());
                }
                                
                for (int j=0; j<listCodigoImpuesto.size(); j++){
                	DocumentoImpuestos DetDocImp = new DocumentoImpuestos();
                	DetDocImp.setImpuestoCodigo(Integer.parseInt(listCodigoImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoCodigoPorcentaje(Integer.parseInt(listCodigoPorcentajeImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoTarifa(Double.parseDouble(listTarifaImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoBaseImponible(Double.parseDouble(listBaseImponibleImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoValor(Double.parseDouble(listValorImpuesto.get(j).toString()));
                	listDetImpuestosDocumentos.add(DetDocImp);
                }
                detDoc.setListDetImpuestosDocumentos(listDetImpuestosDocumentos);
                listDetDocumentos.add(detDoc);
            }                       
            emite.getInfEmisor().setListDetDocumentos(listDetDocumentos);
            int indexCliente = 0;
            HashMap<String, String> infoAdicionalHash = new HashMap<String, String>(); 
            expr = xpath.compile("//campoAdicional/text()");
            List<String> listInfoAdicionalFacturaValue = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){            	
            	listInfoAdicionalFacturaValue.add(nodes.item(i).getNodeValue());
            	//System.out.println("NameNormalizado::"+nodes.item(i).getNodeValue());
            	//System.out.println("NameNormalizado::"+normalizeValue(nodes.item(i).getNodeValue(),300).toUpperCase());
            }
            
            expr = xpath.compile("//campoAdicional/@nombre");
            List<String> listInfoAdicionalFacturaName = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){
            	
            	listInfoAdicionalFacturaName.add(nodes.item(i).getNodeValue());
            	//System.out.println("Valor::"+nodes.item(i).getNodeValue());
            }
            ArrayList<InformacionAdicional> ListInfAdicional = new ArrayList<InformacionAdicional>();
            ListInfAdicional.clear();
            //ArrayList<InfoAdicional> listInfoAdicional = new ArrayList<InfoAdicional>();
            for (int i=0; i<listInfoAdicionalFacturaValue.size(); i++){
            	//infoAdicionalHash.put(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CLIENTE")){
            		emite.getInfEmisor().setCodCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("DIRECCION")){
            		emite.getInfEmisor().setDireccion(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("EMAIL")){
            		emite.getInfEmisor().setEmailCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("TELEFONO")){
            		emite.getInfEmisor().setTelefono(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("LOCAL")){
            		emite.getInfEmisor().setLocal(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CAJA")){
            		emite.getInfEmisor().setCaja(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("MOVIMIENTO")){
            		emite.getInfEmisor().setIdMovimiento(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	InformacionAdicional info = new InformacionAdicional(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	ListInfAdicional.add(info);
            } 
            emite.getInfEmisor().setListInfAdicional(ListInfAdicional);
        
        } catch (Exception e) {
            System.out.println("Excepcion obtenida: Error en estructura del XML");
        	System.out.println("Se procede a insertar el documento 'basico' para poder ser reprocesado desde el portal" );
        	registrarDocErroneo(emite);
        }
    }
	
	
	
	//Lectura de Nota de Credito
	public static void leerNotaCreditoXml(String nameXml, Emisor emite){
		System.out.println(emite.toString()+"TipoComprobante::"+emite.getInfEmisor().getTipoComprobante()+"::Leer Xml Nota de Credito");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(nameXml);
            System.out.println("File>>"+nameXml);
            String ls_documento = "notaCredito";
            String ls_tipoDocumento = "infoNotaCredito";
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();            
            //infoTributaria            
            XPathExpression expr = xpath.compile("/"+ls_documento+"/infoTributaria/tipoEmision/text()");
            emite.getInfEmisor().setTipoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            //Se quita validacion para que siempre lea clave de acceso
            //if (emite.getInfEmisor().getTipoEmision().equals("2")){
            	expr = xpath.compile("/"+ls_documento+"/infoTributaria/claveAcceso/text()");
                emite.getInfEmisor().setClaveAcceso((String) expr.evaluate(doc, XPathConstants.STRING));
            //}
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ambiente/text()");
            emite.getInfEmisor().setAmbiente(Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ruc/text()");
            emite.getInfEmisor().setRuc((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/razonSocial/text()");
            emite.getInfEmisor().setRazonSocial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/nombreComercial/text()");
            emite.getInfEmisor().setNombreComercial((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/codDoc/text()");
            emite.getInfEmisor().setCodDocumento((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/estab/text()");
            emite.getInfEmisor().setCodEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ptoEmi/text()");
            emite.getInfEmisor().setCodPuntoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/secuencial/text()");
            emite.getInfEmisor().setSecuencial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/dirMatriz/text()");
            emite.getInfEmisor().setDireccionMatriz((String) expr.evaluate(doc, XPathConstants.STRING));                        
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/fechaEmision/text()");
            emite.getInfEmisor().setFecEmision((String)expr.evaluate(doc, XPathConstants.STRING));
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/dirEstablecimiento/text()");
            emite.getInfEmisor().setDireccionEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
            /*
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/contribuyenteEspecial/text()");
            System.out.println("Contrib Especial::"+(String) expr.evaluate(doc, XPathConstants.STRING));
            //emite.getInfEmisor().setContribEspecial();
            */
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/obligadoContabilidad/text()");
            emite.getInfEmisor().setObligContabilidad((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/tipoIdentificacionComprador/text()");
            emite.getInfEmisor().setTipoIdentificacion((String) expr.evaluate(doc, XPathConstants.STRING));
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/razonSocialComprador/text()");
            emite.getInfEmisor().setRazonSocialComp((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/identificacionComprador/text()");
            emite.getInfEmisor().setIdentificacionComp((String) expr.evaluate(doc, XPathConstants.STRING));
                        
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/rise/text()");
            emite.getInfEmisor().setRise((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/codDocModificado/text()");
            emite.getInfEmisor().setCodDocModificado((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/numDocModificado/text()");
            emite.getInfEmisor().setNumDocModificado((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/fechaEmisionDocSustento/text()");
            emite.getInfEmisor().setFecEmisionDoc((String) expr.evaluate(doc, XPathConstants.STRING));
            
			
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/motivo/text()");
            emite.getInfEmisor().setMotivo((String) expr.evaluate(doc, XPathConstants.STRING));            
			            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalSinImpuestos/text()");
            emite.getInfEmisor().setTotalSinImpuestos(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));           
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/valorModificacion/text()");
            emite.getInfEmisor().setValorModificado(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/moneda/text()");
            emite.getInfEmisor().setMoneda((String) expr.evaluate(doc, XPathConstants.STRING));
            
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/codigo/text()");
            List<String> listCodigo = new ArrayList();            
            NodeList nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigo.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/codigoPorcentaje/text()");
            List<String> listCodigoPorcentaje = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigoPorcentaje.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/baseImponible/text()");
            List<String> listBaseImponible = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listBaseImponible.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/valor/text()");
            List<String> listValor = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listValor.add(nodes.item(i).getNodeValue());
            }
            ArrayList<DetalleTotalImpuestos> listDetDetImpuestos = new ArrayList<DetalleTotalImpuestos>();             
            for (int i=0; i<listCodigo.size(); i++){
            	DetalleTotalImpuestos detImp = new DetalleTotalImpuestos();
            	detImp.setCodTotalImpuestos(Integer.parseInt(listCodigo.get(i).toString()));
            	detImp.setCodPorcentImp(Integer.parseInt(listCodigoPorcentaje.get(i).toString()));
            	detImp.setBaseImponibleImp(Double.parseDouble(listBaseImponible.get(i).toString()));
            	detImp.setValorImp(Double.parseDouble(listValor.get(i).toString()));
            	listDetDetImpuestos.add(detImp);
            }
            
            emite.getInfEmisor().setListDetDetImpuestos(listDetDetImpuestos);
            
            ArrayList<DetalleDocumento> listDetDocumentos = new ArrayList<DetalleDocumento>();
                                               
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/codigoInterno/text()");
            List<String> listCodPrin = new ArrayList();
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int l=0; l<nodes.getLength(); l++){
            	listCodPrin.add(nodes.item(l).getNodeValue());
            	//System.out.println("index::"+i);
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/codigoAdicional/text()");
            List<String> listCodAux = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodAux.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/descripcion/text()");
            List<String> listDescrip = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescrip.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/cantidad/text()");
            List<String> listCantidad = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCantidad.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/precioUnitario/text()");
            List<String> listPrecioUnitario = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioUnitario.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/descuento/text()");
            List<String> listDescuento = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescuento.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/precioTotalSinImpuesto/text()");
            List<String> listPrecioTotalSinImpuesto = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioTotalSinImpuesto.add(nodes.item(i).getNodeValue());
            }
            
            for (int i=0; i<listCodPrin.size(); i++){
            	ArrayList<DocumentoImpuestos> listDetImpuestosDocumentos = new ArrayList<DocumentoImpuestos>();            	
            	DetalleDocumento detDoc = new DetalleDocumento();            	
            	detDoc.setCodigoPrincipal(listCodPrin.get(i).toString());
            	//VPI - HFU
            	if (listCodAux.size()>0)
            	detDoc.setCodigoAuxiliar(listCodAux.get(i).toString());
            	if (listDescrip.size()>0)
            	detDoc.setDescripcion(listDescrip.get(i).toString());
            	if (listCantidad.size()>0)
            	detDoc.setCantidad(Double.parseDouble(listCantidad.get(i).toString()));
            	if (listPrecioUnitario.size()>0)
            	detDoc.setPrecioUnitario(Double.parseDouble(listPrecioUnitario.get(i).toString()));
            	if (listDescuento.size()>0)
            	detDoc.setDescuento(Double.parseDouble(listDescuento.get(i).toString()));
            	if (listPrecioTotalSinImpuesto.size()>0)
            	detDoc.setPrecioTotalSinImpuesto(Double.parseDouble(listPrecioTotalSinImpuesto.get(i).toString()));
            	//detDoc.setListDetImpuestosDocumentos();
            	
            	expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigo/text()");
                List<String> listCodigoImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigoPorcentaje/text()");
                List<String> listCodigoPorcentajeImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoPorcentajeImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/tarifa/text()");
                List<String> listTarifaImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listTarifaImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/baseImponible/text()");
                List<String> listBaseImponibleImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listBaseImponibleImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/valor/text()");
                List<String> listValorImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listValorImpuesto.add(nodes.item(j).getNodeValue());
                }
                                
                for (int j=0; j<listCodigoImpuesto.size(); j++){
                	DocumentoImpuestos DetDocImp = new DocumentoImpuestos();
                	DetDocImp.setImpuestoCodigo(Integer.parseInt(listCodigoImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoCodigoPorcentaje(Integer.parseInt(listCodigoPorcentajeImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoTarifa(Double.parseDouble(listTarifaImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoBaseImponible(Double.parseDouble(listBaseImponibleImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoValor(Double.parseDouble(listValorImpuesto.get(j).toString()));
                	listDetImpuestosDocumentos.add(DetDocImp);
                }
                detDoc.setListDetImpuestosDocumentos(listDetImpuestosDocumentos);
                listDetDocumentos.add(detDoc);
            }                       
            emite.getInfEmisor().setListDetDocumentos(listDetDocumentos);
            
            HashMap<String, String> infoAdicionalHash = new HashMap<String, String>(); 
            expr = xpath.compile("//campoAdicional/text()");
            List<String> listInfoAdicionalFacturaValue = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){            	
            	listInfoAdicionalFacturaValue.add(nodes.item(i).getNodeValue());
            	System.out.println("NameNormalizado::"+nodes.item(i).getNodeValue());
            	//System.out.println("NameNormalizado::"+normalizeValue(nodes.item(i).getNodeValue(),300).toUpperCase());
            }
            
            expr = xpath.compile("//campoAdicional/@nombre");
            List<String> listInfoAdicionalFacturaName = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){
            	listInfoAdicionalFacturaName.add(nodes.item(i).getNodeValue());
            	System.out.println("Valor::"+nodes.item(i).getNodeValue());
            }
            ArrayList<InformacionAdicional> ListInfAdicional = new ArrayList<InformacionAdicional>();
            ListInfAdicional.clear();
            //ArrayList<InfoAdicional> listInfoAdicional = new ArrayList<InfoAdicional>();
            for (int i=0; i<listInfoAdicionalFacturaValue.size(); i++){
            	//infoAdicionalHash.put(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CLIENTE")){
            		emite.getInfEmisor().setCodCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("DIRECCION")){
            		emite.getInfEmisor().setDireccion(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("EMAIL")){
            		emite.getInfEmisor().setEmailCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("TELEFONO")){
            		emite.getInfEmisor().setTelefono(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("LOCAL")){
            		emite.getInfEmisor().setLocal(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CAJA")){
            		emite.getInfEmisor().setCaja(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("MOVIMIENTO")){
            		emite.getInfEmisor().setIdMovimiento(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	InformacionAdicional info = new InformacionAdicional(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	ListInfAdicional.add(info);
            } 
            emite.getInfEmisor().setListInfAdicional(ListInfAdicional);
        } catch (Exception e) {
            System.out.println("Excepcion obtenida: Error en estructura del XML de la nota de credito");
        	System.out.println("Se procede a insertar el documento 'basico' para poder ser consultado desde el portal" );
        	registrarDocErroneo(emite);
        }
	}
	//Lectura de Nota de Debito
	public static void leerNotaDebitoXml(String nameXml, Emisor emite) {
		System.out.println(emite.toString()+"TipoComprobante::"+emite.getInfEmisor().getTipoComprobante()+"::Leer Xml Nota de Debito");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(nameXml);
            System.out.println("File>>"+nameXml);
            String ls_documento = "notaDebito";
            String ls_tipoDocumento = "infoNotaDebito";
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();            
            //infoTributaria            
            XPathExpression expr = xpath.compile("/"+ls_documento+"/infoTributaria/tipoEmision/text()");
            emite.getInfEmisor().setTipoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            //se quita validacion para que siempre lea clave
            //if (emite.getInfEmisor().getTipoEmision().equals("2")){
            	expr = xpath.compile("/"+ls_documento+"/infoTributaria/claveAcceso/text()");
                emite.getInfEmisor().setClaveAcceso((String) expr.evaluate(doc, XPathConstants.STRING));
            //}
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ambiente/text()");
            emite.getInfEmisor().setAmbiente(Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING)));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ruc/text()");
            emite.getInfEmisor().setRuc((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/razonSocial/text()");
            emite.getInfEmisor().setRazonSocial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/nombreComercial/text()");
            emite.getInfEmisor().setNombreComercial((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/codDoc/text()");
            emite.getInfEmisor().setCodDocumento((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/estab/text()");
            emite.getInfEmisor().setCodEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
 
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/ptoEmi/text()");
            emite.getInfEmisor().setCodPuntoEmision((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/secuencial/text()");
            emite.getInfEmisor().setSecuencial((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/infoTributaria/dirMatriz/text()");
            emite.getInfEmisor().setDireccionMatriz((String) expr.evaluate(doc, XPathConstants.STRING));                        
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/fechaEmision/text()");
            emite.getInfEmisor().setFecEmision((String)expr.evaluate(doc, XPathConstants.STRING));
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/dirEstablecimiento/text()");
            emite.getInfEmisor().setDireccionEstablecimiento((String) expr.evaluate(doc, XPathConstants.STRING));
            /*
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/contribuyenteEspecial/text()");
            System.out.println("Contrib Especial::"+(String) expr.evaluate(doc, XPathConstants.STRING));
            //emite.getInfEmisor().setContribEspecial();
            */
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/obligadoContabilidad/text()");
            emite.getInfEmisor().setObligContabilidad((String) expr.evaluate(doc, XPathConstants.STRING));
            
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/tipoIdentificacionComprador/text()");
            emite.getInfEmisor().setIdentificacionComp((String) expr.evaluate(doc, XPathConstants.STRING));
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/razonSocialComprador/text()");
            emite.getInfEmisor().setRazonSocialComp((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/identificacionComprador/text()");
            emite.getInfEmisor().setIdentificacionComp((String) expr.evaluate(doc, XPathConstants.STRING));
                        
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/rise/text()");
            emite.getInfEmisor().setRise((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/codDocModificado/text()");
            emite.getInfEmisor().setCodDocModificado((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/numDocModificado/text()");
            emite.getInfEmisor().setNumDocModificado((String) expr.evaluate(doc, XPathConstants.STRING));
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/fechaEmisionDocSustento/text()");
            emite.getInfEmisor().setFecEmisionDoc((String) expr.evaluate(doc, XPathConstants.STRING));                        
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalSinImpuestos/text()");
            emite.getInfEmisor().setTotalSinImpuestos(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));           
            
            //Ojo JZURITA
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/valorTotal/text()");
            emite.getInfEmisor().setValorModificado(Double.parseDouble((String) expr.evaluate(doc, XPathConstants.STRING)));           
            
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/codigo/text()");
            List<String> listCodigo = new ArrayList();            
            NodeList nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigo.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/tarifa/text()");
            List<String> listTarifa = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigo.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/codigoPorcentaje/text()");
            List<String> listCodigoPorcentaje = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodigoPorcentaje.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/baseImponible/text()");
            List<String> listBaseImponible = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listBaseImponible.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/"+ls_tipoDocumento+"/totalConImpuestos/totalImpuesto[*]/valor/text()");
            List<String> listValor = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listValor.add(nodes.item(i).getNodeValue());
            }
            ArrayList<DetalleTotalImpuestos> listDetDetImpuestos = new ArrayList<DetalleTotalImpuestos>();             
            for (int i=0; i<listCodigo.size(); i++){
            	DetalleTotalImpuestos detImp = new DetalleTotalImpuestos();
            	detImp.setCodTotalImpuestos(Integer.parseInt(listCodigo.get(i).toString()));
            	detImp.setCodPorcentImp(Integer.parseInt(listCodigoPorcentaje.get(i).toString()));
            	//listTarifa
            	detImp.setTarifaImp(Double.parseDouble(listTarifa.get(i).toString()));
            	detImp.setBaseImponibleImp(Double.parseDouble(listBaseImponible.get(i).toString()));
            	detImp.setValorImp(Double.parseDouble(listValor.get(i).toString()));
            	listDetDetImpuestos.add(detImp);
            }
            
            emite.getInfEmisor().setListDetDetImpuestos(listDetDetImpuestos);
            
            ArrayList<DetalleDocumento> listDetDocumentos = new ArrayList<DetalleDocumento>();
                                               
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/codigoInterno/text()");
            List<String> listCodPrin = new ArrayList();
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int l=0; l<nodes.getLength(); l++){
            	listCodPrin.add(nodes.item(l).getNodeValue());
            	//System.out.println("index::"+i);
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/codigoAdicional/text()");
            List<String> listCodAux = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCodAux.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/descripcion/text()");
            List<String> listDescrip = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescrip.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/cantidad/text()");
            List<String> listCantidad = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listCantidad.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/precioUnitario/text()");
            List<String> listPrecioUnitario = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioUnitario.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/descuento/text()");
            List<String> listDescuento = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listDescuento.add(nodes.item(i).getNodeValue());
            }
            
            expr = xpath.compile("/"+ls_documento+"/detalles/detalle[*]/precioTotalSinImpuesto/text()");
            List<String> listPrecioTotalSinImpuesto = new ArrayList();            
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
            for (int i=0; i<nodes.getLength(); i++){
            	listPrecioTotalSinImpuesto.add(nodes.item(i).getNodeValue());
            }
            
            for (int i=0; i<listCodPrin.size(); i++){
            	ArrayList<DocumentoImpuestos> listDetImpuestosDocumentos = new ArrayList<DocumentoImpuestos>();            	
            	DetalleDocumento detDoc = new DetalleDocumento();            	
            	detDoc.setCodigoPrincipal(listCodPrin.get(i).toString());
            	if (listCodAux.size()>0)
            	detDoc.setCodigoAuxiliar(listCodAux.get(i).toString());
            	if (listDescrip.size()>0)
            	detDoc.setDescripcion(listDescrip.get(i).toString());
            	if (listCantidad.size()>0)
            	detDoc.setCantidad(Double.parseDouble(listCantidad.get(i).toString()));
            	if (listPrecioUnitario.size()>0)
            	detDoc.setPrecioUnitario(Double.parseDouble(listPrecioUnitario.get(i).toString()));
            	if (listDescuento.size()>0)
            	detDoc.setDescuento(Double.parseDouble(listDescuento.get(i).toString()));
            	if (listPrecioTotalSinImpuesto.size()>0)
            	detDoc.setPrecioTotalSinImpuesto(Double.parseDouble(listPrecioTotalSinImpuesto.get(i).toString()));
            	//detDoc.setListDetImpuestosDocumentos();
            	
            	expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigo/text()");
                List<String> listCodigoImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/codigoPorcentaje/text()");
                List<String> listCodigoPorcentajeImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listCodigoPorcentajeImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/tarifa/text()");
                List<String> listTarifaImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listTarifaImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/baseImponible/text()");
                List<String> listBaseImponibleImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listBaseImponibleImpuesto.add(nodes.item(j).getNodeValue());
                }
                
                expr = xpath.compile("/"+ls_documento+"/detalles/detalle[codigoPrincipal='"+listCodPrin.get(i).toString()+"']/impuestos/impuesto/valor/text()");
                List<String> listValorImpuesto = new ArrayList();            
                nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));            
                for (int j=0; j<nodes.getLength(); j++){
                	listValorImpuesto.add(nodes.item(j).getNodeValue());
                }
                                
                for (int j=0; j<listCodigoImpuesto.size(); j++){
                	DocumentoImpuestos DetDocImp = new DocumentoImpuestos();
                	DetDocImp.setImpuestoCodigo(Integer.parseInt(listCodigoImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoCodigoPorcentaje(Integer.parseInt(listCodigoPorcentajeImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoTarifa(Double.parseDouble(listTarifaImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoBaseImponible(Double.parseDouble(listBaseImponibleImpuesto.get(j).toString()));
                	DetDocImp.setImpuestoValor(Double.parseDouble(listValorImpuesto.get(j).toString()));
                	listDetImpuestosDocumentos.add(DetDocImp);
                }
                detDoc.setListDetImpuestosDocumentos(listDetImpuestosDocumentos);
                listDetDocumentos.add(detDoc);
            }                       
            emite.getInfEmisor().setListDetDocumentos(listDetDocumentos);
            
            HashMap<String, String> infoAdicionalHash = new HashMap<String, String>(); 
            expr = xpath.compile("//campoAdicional/text()");
            List<String> listInfoAdicionalFacturaValue = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){            	
            	listInfoAdicionalFacturaValue.add(nodes.item(i).getNodeValue());
            	System.out.println("NameNormalizado::"+nodes.item(i).getNodeValue());
            	//System.out.println("NameNormalizado::"+normalizeValue(nodes.item(i).getNodeValue(),300).toUpperCase());
            }
            
            expr = xpath.compile("//campoAdicional/@nombre");
            List<String> listInfoAdicionalFacturaName = new ArrayList();                                    
            nodes =((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
            for (int i=0; i<nodes.getLength(); i++){
            	listInfoAdicionalFacturaName.add(nodes.item(i).getNodeValue());
            	System.out.println("Valor::"+nodes.item(i).getNodeValue());
            }
            ArrayList<InformacionAdicional> ListInfAdicional = new ArrayList<InformacionAdicional>();
            ListInfAdicional.clear();
            //ArrayList<InfoAdicional> listInfoAdicional = new ArrayList<InfoAdicional>();
            for (int i=0; i<listInfoAdicionalFacturaValue.size(); i++){
            	//infoAdicionalHash.put(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CLIENTE")){
            		emite.getInfEmisor().setCodCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("DIRECCION")){
            		emite.getInfEmisor().setDireccion(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("EMAIL")){
            		emite.getInfEmisor().setEmailCliente(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("TELEFONO")){
            		emite.getInfEmisor().setTelefono(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("LOCAL")){
            		emite.getInfEmisor().setLocal(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("CAJA")){
            		emite.getInfEmisor().setCaja(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	if (listInfoAdicionalFacturaName.get(i).toString().equals("MOVIMIENTO")){
            		emite.getInfEmisor().setIdMovimiento(listInfoAdicionalFacturaValue.get(i).toString());
            	}
            	InformacionAdicional info = new InformacionAdicional(listInfoAdicionalFacturaName.get(i).toString(), listInfoAdicionalFacturaValue.get(i).toString());
            	ListInfAdicional.add(info);
            } 
            emite.getInfEmisor().setListInfAdicional(ListInfAdicional);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	// INI HFU MOVIDO DESDE ServiceData
	public static void leerComprobanteRetXml(String nameXml, Emisor emite) {

		
			System.out.println(emite.toString() + "TipoComprobante::"
					+ emite.getInfEmisor().getTipoComprobante()
					+ "::Leer Xml Comprobante de Retencion");
			System.out.println("-- INICIO LECTURA COMPROBANTE RETENCION --");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			Document doc = null;
			String ls_documento = "comprobanteRetencion";
			String ls_infoDocumento = "infoCompRetencion";
			try {
				System.out
						.println("-- INICIO LECTURA COMPROBANTE RETENCION -- INICIO DE TRY");
				builder = factory.newDocumentBuilder();
				doc = builder.parse(nameXml);
				System.out.println("File>>" + nameXml);
				// Create XPathFactory object
				XPathFactory xpathFactory = XPathFactory.newInstance();

				// Create XPath object
				XPath xpath = xpathFactory.newXPath();

				// infoTributaria
				XPathExpression expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/tipoEmision/text()");
				emite.getInfEmisor().setTipoEmision(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				// Se comenta para que siempre lea clave
				// if (emite.getInfEmisor().getTipoEmision().equals("2")){
				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/claveAcceso/text()");
				emite.getInfEmisor().setClaveAcceso(
						(String) expr.evaluate(doc, XPathConstants.STRING));
				// }
				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/ambiente/text()");
				emite.getInfEmisor().setAmbiente(
						Integer.parseInt((String) expr.evaluate(doc,
								XPathConstants.STRING)));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/ruc/text()");
				emite.getInfEmisor().setRuc(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/razonSocial/text()");
				emite.getInfEmisor().setRazonSocial(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/nombreComercial/text()");
				emite.getInfEmisor().setNombreComercial(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/codDoc/text()");
				emite.getInfEmisor().setCodDocumento(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/estab/text()");
				emite.getInfEmisor().setCodEstablecimiento(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/ptoEmi/text()");
				emite.getInfEmisor().setCodPuntoEmision(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/secuencial/text()");
				emite.getInfEmisor().setSecuencial(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/infoTributaria/dirMatriz/text()");
				emite.getInfEmisor().setDireccionMatriz(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				// infoFactura
				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento + "/fechaEmision/text()");
				System.out.println("OBTENGO FECHA ? ->> " + expr);
				emite.getInfEmisor().setFecEmision(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento + "/dirEstablecimiento/text()");
				emite.getInfEmisor().setDireccionEstablecimiento(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				/*
				 * expr = xpath.compile("/"+ls_documento+"/"+ls_infoDocumento+
				 * "/contribuyenteEspecial/text()");
				 * emite.getInfEmisor().setContribEspecial
				 * (Integer.parseInt((String) expr.evaluate(doc,
				 * XPathConstants.STRING)));
				 */
				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento + "/obligadoContabilidad/text()");
				emite.getInfEmisor().setObligContabilidad(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento
						+ "/tipoIdentificacionSujetoRetenido/text()");
				emite.getInfEmisor().setTipoIdentificacion(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento
						+ "/razonSocialSujetoRetenido/text()");
				emite.getInfEmisor().setRazonSocialComp(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento
						+ "/identificacionSujetoRetenido/text()");
				emite.getInfEmisor().setIdentificacionComp(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento + "/"
						+ ls_infoDocumento + "/periodoFiscal/text()");
				emite.getInfEmisor().setPeriodoFiscal(
						(String) expr.evaluate(doc, XPathConstants.STRING));

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/codigo/text()");
				List<String> listCodigo = new ArrayList();
				NodeList nodes = ((NodeList) expr.evaluate(doc,
						XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("codigo::"
							+ nodes.item(i).getNodeValue());
					listCodigo.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/codigoRetencion/text()");
				List<String> listCodigoRetencion = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("codigoRetencion::"
							+ nodes.item(i).getNodeValue());
					listCodigoRetencion.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/baseImponible/text()");
				List<String> listBaseImponible = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("baseImponible::"
							+ nodes.item(i).getNodeValue());
					listBaseImponible.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/porcentajeRetener/text()");
				List<String> listPorcentajeRetener = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("porcentajeRetener::"
							+ nodes.item(i).getNodeValue());
					listPorcentajeRetener.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/valorRetenido/text()");
				List<String> listValorRetenido = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("valorRetenido::"
							+ nodes.item(i).getNodeValue());
					listValorRetenido.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/codDocSustento/text()");
				List<String> listCodDocSustento = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("codDocSustento::"
							+ nodes.item(i).getNodeValue());
					listCodDocSustento.add(nodes.item(i).getNodeValue());
				}

				expr = xpath.compile("/" + ls_documento
						+ "/impuestos/impuesto[*]/numDocSustento/text()");
				List<String> listNumDocSustento = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("numDocSustento::"
							+ nodes.item(i).getNodeValue());
					listNumDocSustento.add(nodes.item(i).getNodeValue());
				}

				expr = xpath
						.compile("/"
								+ ls_documento
								+ "/impuestos/impuesto[*]/fechaEmisionDocSustento/text()");
				List<String> listFechaEmisionDocSustento = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					System.out.println("fechaEmisionDocSustento::"
							+ nodes.item(i).getNodeValue());
					listFechaEmisionDocSustento.add(nodes.item(i)
							.getNodeValue());
				}
				double totalRetencion = 0;
				ArrayList<DetalleImpuestosRetenciones> listDetDetImpuestosRet = new ArrayList<DetalleImpuestosRetenciones>();
				for (int i = 0; i < listCodigo.size(); i++) {
					DetalleImpuestosRetenciones detImp = new DetalleImpuestosRetenciones();
					detImp.setCodigo((listCodigo.get(i).toString()));
					detImp.setCodigoRetencion((listCodigoRetencion.get(i)
							.toString()));
					detImp.setBaseImponible(Double
							.parseDouble(listBaseImponible.get(i).toString()));
					detImp.setPorcentajeRetener(Integer
							.parseInt(listPorcentajeRetener.get(i).toString()));
					totalRetencion = totalRetencion
							+ Double.parseDouble(listValorRetenido.get(i)
									.toString());
					detImp.setValorRetenido(Double
							.parseDouble(listValorRetenido.get(i).toString()));
					detImp.setCodDocSustento(listCodDocSustento.get(i)
							.toString());
					if (listNumDocSustento.size() > 0) {
						detImp.setNumDocSustento(listNumDocSustento.get(i)
								.toString());
					}
					if (listFechaEmisionDocSustento.size() > 0) {
						detImp.setFechaEmisionDocSustento(listFechaEmisionDocSustento
								.get(i).toString());
					}
					listDetDetImpuestosRet.add(detImp);
				}

				emite.getInfEmisor().setImporteTotal(totalRetencion);

				emite.getInfEmisor().setListDetImpuestosRetenciones(
						listDetDetImpuestosRet);

				int indexCliente = 0;
				HashMap<String, String> infoAdicionalHash = new HashMap<String, String>();
				expr = xpath.compile("//campoAdicional/text()");
				List<String> listInfoAdicionalFacturaValue = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {
					listInfoAdicionalFacturaValue.add(nodes.item(i)
							.getNodeValue());
					// System.out.println("NameNormalizado::"+nodes.item(i).getNodeValue());
					// System.out.println("NameNormalizado::"+normalizeValue(nodes.item(i).getNodeValue(),300).toUpperCase());
				}

				expr = xpath.compile("//campoAdicional/@nombre");
				List<String> listInfoAdicionalFacturaName = new ArrayList();
				nodes = ((NodeList) expr.evaluate(doc, XPathConstants.NODESET));
				for (int i = 0; i < nodes.getLength(); i++) {

					listInfoAdicionalFacturaName.add(nodes.item(i)
							.getNodeValue());
					// System.out.println("Valor::"+nodes.item(i).getNodeValue());
				}
				ArrayList<InformacionAdicional> ListInfAdicional = new ArrayList<InformacionAdicional>();
				ListInfAdicional.clear();
				// ArrayList<InfoAdicional> listInfoAdicional = new
				// ArrayList<InfoAdicional>();
				for (int i = 0; i < listInfoAdicionalFacturaValue.size(); i++) {
					// infoAdicionalHash.put(listInfoAdicionalFacturaName.get(i).toString(),
					// listInfoAdicionalFacturaValue.get(i).toString());
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("CLIENTE")) {
						emite.getInfEmisor()
								.setCodCliente(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("DIRECCION")) {
						emite.getInfEmisor()
								.setDireccion(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("EMAIL")) {
						emite.getInfEmisor()
								.setEmailCliente(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("TELEFONO")) {
						emite.getInfEmisor()
								.setTelefono(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("LOCAL")) {
						emite.getInfEmisor()
								.setLocal(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("CAJA")) {
						emite.getInfEmisor()
								.setCaja(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					if (listInfoAdicionalFacturaName.get(i).toString()
							.equals("MOVIMIENTO")) {
						emite.getInfEmisor()
								.setIdMovimiento(
										listInfoAdicionalFacturaValue.get(i)
												.toString());
					}
					InformacionAdicional info = new InformacionAdicional(
							listInfoAdicionalFacturaName.get(i).toString(),
							listInfoAdicionalFacturaValue.get(i).toString());
					ListInfAdicional.add(info);
				}
				emite.getInfEmisor().setListInfAdicional(ListInfAdicional);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al leer comprobante de retención");
				System.out.println("Se procede a almacenar la versión mínima necesaria del mismo");
				registrarDocErroneo(emite);
			}
			System.out.println("-- FIN LECTURA COMPROBANTE RETENCION --");
			}
	
	/**
	 * 
     * Converts XMLGregorianCalendar to java.util.Date in Java
     */
    public static Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }
    
    
    public static void registrarDocErroneo(Emisor emite){
    	
    	Acciones registrador = new Acciones();
		//Se sincroniza el acceso a la variable:
    	//synchronized(ServiceDataHilo.class){
			//docIncompleto = registrador.registraDocumentoIncompleto(emite);
		//}
    	registrador.registraDocumentoIncompleto(emite);
    }
    
	
	/*Preparacion de Documentos a PDF.*/
    public static FacCabDocumento preparaCabDocumentoFac(com.sun.businessLogic.validate.Emisor emite, String ruc, String codEst, String codPtoEmi, String tipoDocumento, String secuencial, String msg_error, String estado){
    	try{    	
		FacCabDocumento cabDoc = new FacCabDocumento();
		//emite.getInfEmisor().setMailEmpresa("jzurita@cimait.com.ec");
		//System.out.println("MailEmpresa::"+emite.getInfEmisor().getMailEmpresa());
		cabDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
		//System.out.println("getAmbiente::"+cabDoc.getAmbiente());
		cabDoc.setRuc(ruc);
		//System.out.println("getRuc::"+cabDoc.getRuc());
		///System.out.println("TipoIdentificacion()::"+emite.getInfEmisor().getTipoIdentificacion());
		cabDoc.setTipoIdentificacion(emite.getInfEmisor().getTipoIdentificacion());
		//System.out.println("TipoIdentificacion()::"+cabDoc.getTipoIdentificacion());
		cabDoc.setIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		//System.out.println("getIdentificacionComprador::"+cabDoc.getIdentificacionComprador());
		cabDoc.setCodEstablecimiento(codEst);
		cabDoc.setCodPuntEmision(codPtoEmi);
		cabDoc.setSecuencial(secuencial);
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());
		cabDoc.setGuiaRemision(emite.getInfEmisor().getGuiaRemision());		
		cabDoc.setRazonSocialComprador(emite.getInfEmisor().getRazonSocialComp());
		cabDoc.setDirEstablecimiento(emite.getInfEmisor().getDireccionEstablecimiento());
		//cabDoc.setIdentificacionComprador(emite.getInfEmisor().getTipoIdentificacion());
		cabDoc.setTotalSinImpuesto(emite.getInfEmisor().getTotalSinImpuestos());
		cabDoc.setTotalDescuento(emite.getInfEmisor().getTotalDescuento());
		cabDoc.setEmail(emite.getInfEmisor().getMailEmpresa());
		cabDoc.setPropina(emite.getInfEmisor().getPropina());
		cabDoc.setMoneda("0");
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		cabDoc.setObligadoContabilidad(emite.getInfEmisor().getObligContabilidad());
		
		cabDoc.setCodCliente((emite.getInfEmisor().getCodCliente()));		
		cabDoc.setDireccion(emite.getInfEmisor().getDireccion());
		cabDoc.setEmailCliente(emite.getInfEmisor().getEmailCliente());
		cabDoc.setTelefono(emite.getInfEmisor().getTelefono());
		
		String infoAdicional = "";
		if(emite.getInfEmisor().getListInfAdicional()!=null)
		{
			for (int i = 0; i<emite.getInfEmisor().getListInfAdicional().size(); i++)
			infoAdicional = infoAdicional + "/" + emite.getInfEmisor().getListInfAdicional().get(i).getName() + "-" +emite.getInfEmisor().getListInfAdicional().get(i).getValue(); 		
		}
		cabDoc.setInfoAdicional(infoAdicional);
		if (emite.getInfEmisor().getPeriodoFiscal()!=null)
		cabDoc.setPeriodoFiscal(emite.getInfEmisor().getPeriodoFiscal().toString());
		
		cabDoc.setRise(emite.getInfEmisor().getRise());
		cabDoc.setFechaInicioTransporte(emite.getInfEmisor().getFechaIniTransp());
		cabDoc.setFechaFinTransporte(emite.getInfEmisor().getFechaFinTransp());
		cabDoc.setPlaca(emite.getInfEmisor().getPlaca());
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());
		cabDoc.setMotivoRazon(emite.getInfEmisor().getMotivo());
		//cabDoc.setAutorizacion(emite.getInfEmisor().getNumeroAutorizacion());
		//cabDoc.setFechaautorizacion(emite.getInfEmisor().getFechaAutorizacion());
		cabDoc.setClaveAcceso(emite.getInfEmisor().getClaveAcceso());
		cabDoc.setImporteTotal(emite.getInfEmisor().getImporteTotal());
		cabDoc.setTotalSinImpuesto(emite.getInfEmisor().getTotalSinImpuestos());
		cabDoc.setTotalDescuento(emite.getInfEmisor().getTotalDescuento());
		cabDoc.setEmail(emite.getInfEmisor().getMailEmpresa());
		cabDoc.setPropina(emite.getInfEmisor().getPropina());
		
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		cabDoc.setCodDocModificado(emite.getInfEmisor().getCodDocModificado());
		cabDoc.setNumDocModificado(emite.getInfEmisor().getNumDocModificado());
		cabDoc.setMotivoValor(emite.getInfEmisor().getMotivoValorND());
		
		cabDoc.setTipoEmision(emite.getInfEmisor().getTipoEmision());
		cabDoc.setListInfAdicional(emite.getInfEmisor().getListInfAdicional());
		
		cabDoc.setSubtotalNoIva(emite.getInfEmisor().getSubTotalNoSujeto());
		cabDoc.setTotalvalorICE(emite.getInfEmisor().getTotalICE());
		cabDoc.setIva12(emite.getInfEmisor().getTotalIva12());       
		cabDoc.setIsActive("1");
		cabDoc.setESTADO_TRANSACCION(estado);
		cabDoc.setMSJ_ERROR(msg_error);
		//System.out.println("Totales de Impuestos");
		ArrayList<DetalleTotalImpuestos> lisDetImp = emite.getInfEmisor().getListDetDetImpuestos();
		if (lisDetImp != null){
		for ( DetalleTotalImpuestos det : lisDetImp){
			//System.out.println("codTotalImpuestos::"+det.getCodTotalImpuestos());
			//System.out.println("codPorcentImpuestos::"+det.getCodPorcentImp());
			//System.out.println("baseImponible::"+det.getBaseImponibleImp());
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 2)){
				cabDoc.setSubtotal12(det.getBaseImponibleImp());
				cabDoc.setIva12(det.getValorImp());
				//System.out.println("Valor::getSubtotal12::"+cabDoc.getSubtotal12());
				
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 0)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotal0(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubTotal0::"+cabDoc.getSubtotal0());
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 6)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotalNoIva(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubtotalNoIva::"+cabDoc.getSubtotalNoIva());
			}
		}				
	}
		
		
		if (emite.getInfEmisor().getListDetDocumentos().size()>0){
			List<FacDetDocumento> detalles = new ArrayList<FacDetDocumento>();
			for (int i=0; i<emite.getInfEmisor().getListDetDocumentos().size();i++){
				FacDetDocumento DetDoc = new FacDetDocumento();
				DetDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
				DetDoc.setRuc(ruc);
				DetDoc.setCodEstablecimiento(emite.getInfEmisor().getCodEstablecimiento());
				DetDoc.setCodPuntEmision(emite.getInfEmisor().getCodPuntoEmision());
				DetDoc.setSecuencial(emite.getInfEmisor().getSecuencial());
				DetDoc.setCodPrincipal(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoPrincipal());
				DetDoc.setCodAuxiliar(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoAuxiliar());
				DetDoc.setDescripcion(emite.getInfEmisor().getListDetDocumentos().get(i).getDescripcion());
				//VPI
				DetDoc.setCantidad(emite.getInfEmisor().getListDetDocumentos().get(i).getCantidad());
				DetDoc.setPrecioUnitario(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioUnitario());
				DetDoc.setDescuento(emite.getInfEmisor().getListDetDocumentos().get(i).getDescuento());
				DetDoc.setPrecioTotalSinImpuesto(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioTotalSinImpuesto());
				int flagIce=0;
				if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size()>0){
					for (int j=0; j<emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size();j++){
						if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoCodigo()==3){
							DetDoc.setValorIce(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoValor());
							flagIce = 1;
						}
					}
				}	
				
				if (flagIce==0)
				DetDoc.setValorIce(0);
				
				DetDoc.setSecuencialDetalle(emite.getInfEmisor().getListDetDocumentos().get(i).getLineaFactura());
				DetDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
				detalles.add(DetDoc);
			}
			cabDoc.setListDetalleDocumento(detalles);
		}
		cabDoc.setListInfAdicional(emite.getInfEmisor().getListInfAdicional());
		return cabDoc;
		
    	}catch(Exception e){
    		emite.insertaBitacora(emite, "ER",  "Prepara cab documento: Error preparando el documento", "", "", "", "");
    		return null;
		}
		
	}
	public static FacCabDocumento preparaCabDocumentoRet(com.sun.businessLogic.validate.Emisor emite, String ruc, String codEst, String codPtoEmi, String tipoDocumento, String secuencial, String msg_error, String estado){
		try{
		FacCabDocumento cabDoc = new FacCabDocumento();
		//emite.getInfEmisor().setMailEmpresa("jzurita@cimait.com.ec");
		cabDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
		cabDoc.setRuc(ruc);
		cabDoc.setTipoIdentificacion((emite.getInfEmisor().getTipoIdentificacion()));
		
		
		//System.out.println("TipoIdentificacion()::"+cabDoc.getTipoIdentificacion());
		cabDoc.setIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		//System.out.println("getIdentificacionComprador::"+cabDoc.getIdentificacionComprador());
		
		cabDoc.setCodEstablecimiento(codEst);
		cabDoc.setCodPuntEmision(codPtoEmi);
		cabDoc.setSecuencial(secuencial);
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());				
		cabDoc.setRazonSocialComprador(emite.getInfEmisor().getRazonSocialComp());
		cabDoc.setDirEstablecimiento(emite.getInfEmisor().getDireccionEstablecimiento());
				
		cabDoc.setEmail(emite.getInfEmisor().getMailEmpresa());		
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		cabDoc.setObligadoContabilidad(emite.getInfEmisor().getObligContabilidad());
		cabDoc.setCodCliente((emite.getInfEmisor().getCodCliente()));		
		cabDoc.setDireccion(emite.getInfEmisor().getDireccion());
		cabDoc.setEmailCliente(emite.getInfEmisor().getEmailCliente());
		cabDoc.setTelefono(emite.getInfEmisor().getTelefono());
		String infoAdicional = "";
		
				
		if(emite.getInfEmisor().getListInfAdicional()!=null)
		{
			for (int i = 0; i<emite.getInfEmisor().getListInfAdicional().size(); i++)
			infoAdicional = infoAdicional + "/" + emite.getInfEmisor().getListInfAdicional().get(i).getName() + "-" +emite.getInfEmisor().getListInfAdicional().get(i).getValue(); 		
		}
		cabDoc.setInfoAdicional(infoAdicional);
		if (emite.getInfEmisor().getPeriodoFiscal()!=null)
		cabDoc.setPeriodoFiscal(emite.getInfEmisor().getPeriodoFiscal().toString());
				
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());
		cabDoc.setListImpuestosRetencion(emite.getInfEmisor().getListDetImpuestosRetenciones());
		double total = 0;
		for (DetalleImpuestosRetenciones impuestoRetencion : emite.getInfEmisor().getListDetImpuestosRetenciones()) {
			total = total + impuestoRetencion.getValorRetenido();
		}
		cabDoc.setImporteTotal(Double.valueOf(total));

		cabDoc.setClaveAcceso(emite.getInfEmisor().getClaveAcceso());
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		
		cabDoc.setCodDocModificado(emite.getInfEmisor().getCodDocModificado());
		cabDoc.setNumDocModificado(emite.getInfEmisor().getNumDocModificado());
		
		//cabDoc.setTipIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		cabDoc.setTipoEmision(emite.getInfEmisor().getTipoEmision());
		
		cabDoc.setIsActive("1");
		cabDoc.setESTADO_TRANSACCION(estado);
		cabDoc.setMSJ_ERROR(msg_error);
		
		return cabDoc;
	}catch(Exception e){
		emite.insertaBitacora(emite, "ER",  "Prepara cab documento: Error preparando el documento", "", "", "", "");
		return null;
	}
		
	}
	
	public static FacCabDocumento preparaCabDocumentoCre(com.sun.businessLogic.validate.Emisor emite, String ruc, String codEst, String codPtoEmi, String tipoDocumento, String secuencial, String msg_error, String estado){
		try{
		FacCabDocumento cabDoc = new FacCabDocumento();
		//emite.getInfEmisor().setMailEmpresa("jzurita@cimait.com.ec");
		cabDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
		cabDoc.setRuc(ruc);
	
		cabDoc.setCodEstablecimiento(codEst);
		cabDoc.setCodPuntEmision(codPtoEmi);
		cabDoc.setSecuencial(secuencial);
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());				
		cabDoc.setRazonSocialComprador(emite.getInfEmisor().getRazonSocialComp());
		cabDoc.setDirEstablecimiento(emite.getInfEmisor().getDireccionEstablecimiento());
		
		
		cabDoc.setCodDocModificado(emite.getInfEmisor().getCodDocModificado());
		
		//VPI se agrega Motivo
		cabDoc.setMotivoRazon(emite.getInfEmisor().getMotivo());
		cabDoc.setCodDocSustento(emite.getInfEmisor().getCodDocModificado());
		cabDoc.setNumDocSustento(emite.getInfEmisor().getNumDocModificado());
		cabDoc.setFecEmisionDocSustento(emite.getInfEmisor().getFecEmisionDoc());
		
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		String strFecha = emite.getInfEmisor().getFecEmisionDoc();
		Date fechaDate = null;
		try {
			fechaDate = formato.parse(strFecha);
                        System.out.println(fechaDate.toString());
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
		cabDoc.setFechaEmisionDocSustento(fechaDate);
		
		//System.out.println("TipoIdentificacion()::"+emite.getInfEmisor().getTipoIdentificacion());
		cabDoc.setTipoIdentificacion(emite.getInfEmisor().getTipoIdentificacion());
		//System.out.println("TipoIdentificacion()::"+cabDoc.getTipoIdentificacion());
		cabDoc.setIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		//System.out.println("getIdentificacionComprador::"+cabDoc.getIdentificacionComprador());
		
		cabDoc.setEmail(emite.getInfEmisor().getMailEmpresa());		
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		cabDoc.setObligadoContabilidad(emite.getInfEmisor().getObligContabilidad());
		
		if ((emite.getInfEmisor().getCodCliente()!=null)&&(emite.getInfEmisor().getCodCliente().length()>0))
			cabDoc.setCodCliente((emite.getInfEmisor().getCodCliente()));		
		
		cabDoc.setDireccion(emite.getInfEmisor().getDireccion());
		cabDoc.setEmailCliente(emite.getInfEmisor().getEmailCliente());
		cabDoc.setTelefono(emite.getInfEmisor().getTelefono());
		String infoAdicional = "";
		if(emite.getInfEmisor().getListInfAdicional()!=null)
		{
			for (int i = 0; i<emite.getInfEmisor().getListInfAdicional().size(); i++)
			infoAdicional = infoAdicional + "/" + emite.getInfEmisor().getListInfAdicional().get(i).getName() + "-" +emite.getInfEmisor().getListInfAdicional().get(i).getValue(); 		
		}
		cabDoc.setInfoAdicional(infoAdicional);
		if (emite.getInfEmisor().getPeriodoFiscal()!=null)
		cabDoc.setPeriodoFiscal(emite.getInfEmisor().getPeriodoFiscal().toString());
		
		
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());
		cabDoc.setListImpuestosRetencion(emite.getInfEmisor().getListDetImpuestosRetenciones());
		
		cabDoc.setClaveAcceso(emite.getInfEmisor().getClaveAcceso());
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		
		//cabDoc.setTipIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		cabDoc.setTipoEmision(emite.getInfEmisor().getTipoEmision());
		
		cabDoc.setIsActive("1");
		cabDoc.setESTADO_TRANSACCION(estado);
		cabDoc.setMSJ_ERROR(msg_error);
		
		cabDoc.setTotalSinImpuesto(emite.getInfEmisor().getTotalSinImpuestos());
		cabDoc.setTotalDescuento(emite.getInfEmisor().getTotalDescuento());
		cabDoc.setPropina(emite.getInfEmisor().getPropina());

		//System.out.println("Totales de Impuestos");
		ArrayList<DetalleTotalImpuestos> lisDetImp = emite.getInfEmisor().getListDetDetImpuestos();
		for ( DetalleTotalImpuestos det : lisDetImp){
			//System.out.println("codTotalImpuestos::"+det.getCodTotalImpuestos());
			//System.out.println("codPorcentImpuestos::"+det.getCodPorcentImp());
			//System.out.println("baseImponible::"+det.getBaseImponibleImp());
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 2)){
				cabDoc.setSubtotal12(det.getBaseImponibleImp());
				cabDoc.setIva12(det.getValorImp());
				//System.out.println("Valor::getSubtotal12::"+cabDoc.getSubtotal12());
				
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 0)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotal0(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubTotal0::"+cabDoc.getSubtotal0());
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 6)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotalNoIva(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubtotalNoIva::"+cabDoc.getSubtotalNoIva());
			}
		}
		
		double total =  cabDoc.getSubtotal12()+
				   		cabDoc.getSubtotalNoIva()+
				   		cabDoc.getSubtotal0()+
				   		cabDoc.getIva12()+
				   		cabDoc.getTotalvalorICE();	
		cabDoc.setImporteTotal(total);
		/*
		cabDoc.setSubtotalNoIva(emite.geltInfEmisor().getSubTotalNoSujeto());
		cabDoc.setTotalvalorICE(emite.getInfEmisor().getTotalICE());
		cabDoc.setIva12(emite.getInfEmisor().getTotalIva12());
		*/
	
		if (emite.getInfEmisor().getListDetDocumentos().size()>0){
			List<FacDetDocumento> detalles = new ArrayList<FacDetDocumento>();
			for (int i=0; i<emite.getInfEmisor().getListDetDocumentos().size();i++){
				FacDetDocumento DetDoc = new FacDetDocumento();
				DetDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
				DetDoc.setRuc(ruc);
				DetDoc.setCodEstablecimiento(emite.getInfEmisor().getCodEstablecimiento());
				DetDoc.setCodPuntEmision(emite.getInfEmisor().getCodPuntoEmision());
				DetDoc.setSecuencial(emite.getInfEmisor().getSecuencial());
				DetDoc.setCodPrincipal(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoPrincipal());
				DetDoc.setCodAuxiliar(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoAuxiliar());
				DetDoc.setDescripcion(emite.getInfEmisor().getListDetDocumentos().get(i).getDescripcion());
				//VPI se cambia variable cantidad por double
				DetDoc.setCantidad(emite.getInfEmisor().getListDetDocumentos().get(i).getCantidad());
				DetDoc.setPrecioUnitario(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioUnitario());
				DetDoc.setDescuento(emite.getInfEmisor().getListDetDocumentos().get(i).getDescuento());
				DetDoc.setPrecioTotalSinImpuesto(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioTotalSinImpuesto());
				int flagIce=0;
				if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size()>0){
					for (int j=0; j<emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size();j++){
						if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoCodigo()==3){
							DetDoc.setValorIce(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoValor());
							flagIce = 1;
						}
					}
				}	
				
				if (flagIce==0)
				DetDoc.setValorIce(0);
				
				DetDoc.setSecuencialDetalle(emite.getInfEmisor().getListDetDocumentos().get(i).getLineaFactura());
				DetDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
				detalles.add(DetDoc);
			}
			cabDoc.setListDetalleDocumento(detalles);
		}
		return cabDoc;
	}catch(Exception e){
		emite.insertaBitacora(emite, "ER",  "Prepara cab documento: Error preparando el documento", "", "", "", "");
		return null;
	}
	}

	public static FacCabDocumento preparaCabDocumentoCreResp(com.sun.businessLogic.validate.Emisor emite, String ruc, String codEst, String codPtoEmi, String tipoDocumento, String secuencial, String msg_error, String estado){
		FacCabDocumento cabDoc = new FacCabDocumento();
		//emite.getInfEmisor().setMailEmpresa("jzurita@cimait.com.ec");
		cabDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
		cabDoc.setRuc(ruc);
	
		cabDoc.setCodEstablecimiento(codEst);
		cabDoc.setCodPuntEmision(codPtoEmi);
		cabDoc.setSecuencial(secuencial);
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());				
		cabDoc.setRazonSocialComprador(emite.getInfEmisor().getRazonSocialComp());
		cabDoc.setDirEstablecimiento(emite.getInfEmisor().getDireccionEstablecimiento());
		
		cabDoc.setNumDocSustento(emite.getInfEmisor().getNumDocModificado());
		cabDoc.setFecEmisionDocSustento(emite.getInfEmisor().getFecEmisionDoc());
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		String strFecha = emite.getInfEmisor().getFecEmisionDoc();
		Date fechaDate = null;
		try {
			fechaDate = formato.parse(strFecha);
                        System.out.println(fechaDate.toString());
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
		cabDoc.setFechaEmisionDocSustento(fechaDate);
		
		//System.out.println("TipoIdentificacion()::"+emite.getInfEmisor().getTipoIdentificacion());
		cabDoc.setTipoIdentificacion(emite.getInfEmisor().getTipoIdentificacion());
		//System.out.println("TipoIdentificacion()::"+cabDoc.getTipoIdentificacion());
		cabDoc.setIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		//System.out.println("getIdentificacionComprador::"+cabDoc.getIdentificacionComprador());
		
		cabDoc.setEmail(emite.getInfEmisor().getMailEmpresa());		
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		cabDoc.setObligadoContabilidad(emite.getInfEmisor().getObligContabilidad());
		/*
		 "::Ruc::"+detalles.get(0).getRuc()+
    		 			"::CodEstablecimiento::"+ detalles.get(0).getCodEstablecimiento()+
    		 			"::PuntoEmision::"+ detalles.get(0).getCodPuntEmision()+
    		 			"::Secuencial::"+ detalles.get(0).getSecuencial()+
    		 			"::TipoDocumento::"+ detalles.get(0).getCodigoDocumento();
		 */
		
		if ((emite.getInfEmisor().getCodCliente()!=null)&&(emite.getInfEmisor().getCodCliente().length()>0))
			cabDoc.setCodCliente((emite.getInfEmisor().getCodCliente()));		
		
		cabDoc.setDireccion(emite.getInfEmisor().getDireccion());
		cabDoc.setEmailCliente(emite.getInfEmisor().getEmailCliente());
		cabDoc.setTelefono(emite.getInfEmisor().getTelefono());
		String infoAdicional = "";
		if(emite.getInfEmisor().getListInfAdicional()!=null)
		{
			for (int i = 0; i<emite.getInfEmisor().getListInfAdicional().size(); i++)
			infoAdicional = infoAdicional + "/" + emite.getInfEmisor().getListInfAdicional().get(i).getName() + "-" +emite.getInfEmisor().getListInfAdicional().get(i).getValue(); 		
		}
		cabDoc.setInfoAdicional(infoAdicional);
		if (emite.getInfEmisor().getPeriodoFiscal()!=null)
		cabDoc.setPeriodoFiscal(emite.getInfEmisor().getPeriodoFiscal().toString());
		
		
		cabDoc.setFechaEmision(emite.getInfEmisor().getFecEmision());
		cabDoc.setListImpuestosRetencion(emite.getInfEmisor().getListDetImpuestosRetenciones());
		
		cabDoc.setClaveAcceso(emite.getInfEmisor().getClaveAcceso());
		cabDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
		
		cabDoc.setCodDocModificado(emite.getInfEmisor().getCodDocModificado());
		cabDoc.setNumDocModificado(emite.getInfEmisor().getNumDocModificado());
		
		cabDoc.setFecEmisionDocSustento(emite.getInfEmisor().getFecEmisionDoc());
		
		//cabDoc.setTipIdentificacionComprador(emite.getInfEmisor().getIdentificacionComp());
		cabDoc.setTipoEmision(emite.getInfEmisor().getTipoEmision());
		
		cabDoc.setIsActive("1");
		cabDoc.setESTADO_TRANSACCION(estado);
		cabDoc.setMSJ_ERROR(msg_error);
		
		//System.out.println("Totales de Impuestos");
		ArrayList<DetalleTotalImpuestos> lisDetImp = emite.getInfEmisor().getListDetDetImpuestos();
		for ( DetalleTotalImpuestos det : lisDetImp){
			//System.out.println("codTotalImpuestos::"+det.getCodTotalImpuestos());
			//System.out.println("codPorcentImpuestos::"+det.getCodPorcentImp());
			//System.out.println("baseImponible::"+det.getBaseImponibleImp());
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 2)){
				cabDoc.setSubtotal12(det.getBaseImponibleImp());
				cabDoc.setIva12(det.getValorImp());
				//System.out.println("Valor::getSubtotal12::"+cabDoc.getSubtotal12());
				
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 0)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotal0(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubTotal0::"+cabDoc.getSubtotal0());
			}
			if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 6)){
				//cabDoc.setSubtotal0(det.getValorImp());
				cabDoc.setSubtotalNoIva(det.getBaseImponibleImp());
				//System.out.println("Valor::getSubtotalNoIva::"+cabDoc.getSubtotalNoIva());
			}
		}
		
		double total = cabDoc.getSubtotal12()+
				   cabDoc.getSubtotalNoIva()+
				   cabDoc.getSubtotal0()+
				   cabDoc.getIva12()+
				   cabDoc.getTotalvalorICE();	
	
		cabDoc.setSubtotalNoIva(emite.getInfEmisor().getSubTotalNoSujeto());
		cabDoc.setTotalvalorICE(emite.getInfEmisor().getTotalICE());
		cabDoc.setIva12(emite.getInfEmisor().getTotalIva12());
	
		if (emite.getInfEmisor().getListDetDocumentos().size()>0){
			List<FacDetDocumento> detalles = new ArrayList<FacDetDocumento>();
			for (int i=0; i<emite.getInfEmisor().getListDetDocumentos().size();i++){
				FacDetDocumento DetDoc = new FacDetDocumento();
				DetDoc.setAmbiente(emite.getInfEmisor().getAmbiente());
				DetDoc.setRuc(ruc);
				DetDoc.setCodEstablecimiento(emite.getInfEmisor().getCodEstablecimiento());
				DetDoc.setCodPuntEmision(emite.getInfEmisor().getCodPuntoEmision());
				DetDoc.setSecuencial(emite.getInfEmisor().getSecuencial());
				DetDoc.setCodPrincipal(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoPrincipal());
				DetDoc.setCodAuxiliar(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoAuxiliar());
				DetDoc.setDescripcion(emite.getInfEmisor().getListDetDocumentos().get(i).getDescripcion());
				//VPI
				DetDoc.setCantidad(emite.getInfEmisor().getListDetDocumentos().get(i).getCantidad());
				DetDoc.setPrecioUnitario(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioUnitario());
				DetDoc.setDescuento(emite.getInfEmisor().getListDetDocumentos().get(i).getDescuento());
				DetDoc.setPrecioTotalSinImpuesto(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioTotalSinImpuesto());
				int flagIce=0;
				if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size()>0){
					for (int j=0; j<emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().size();j++){
						if(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoCodigo()==3){
							DetDoc.setValorIce(emite.getInfEmisor().getListDetDocumentos().get(i).getListDetImpuestosDocumentos().get(j).getImpuestoValor());
							flagIce = 1;
						}
					}
				}	
				
				if (flagIce==0)
				DetDoc.setValorIce(0);
				
				DetDoc.setSecuencialDetalle(emite.getInfEmisor().getListDetDocumentos().get(i).getLineaFactura());
				DetDoc.setCodigoDocumento(emite.getInfEmisor().getCodDocumento());
				detalles.add(DetDoc);
			}
			cabDoc.setListDetalleDocumento(detalles);
		}
		return cabDoc;
	}

	
	@Override
	//Heredado de la Clase GenericTransaction
	public void run() {
		try{
			//Dentro del metodo se realiza la inicializacion
			//de la conexion a la Base para el hilo en ejecucion
			atiendeHilo();	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//Se cierra la conexion al finalizar ejecucion del Hilo 
			 if (ConexionBase.DBManager.get()!= null){
				   ConexionBase.cerrarConexionBD();
				 }
			System.out.println("Cerrando conexion del Hilo");
		}
	}

	public int getIdHilo() {
		return idHilo;
	}

	public void setIdHilo(int idHilo) {
		this.idHilo = idHilo;
	}

	public InfoEmpresa getInfoEmp() {
		return infoEmp;
	}

	public void setInfoEmp(InfoEmpresa infoEmp) {
		this.infoEmp = infoEmp;
	}

	public Emisor getEmite() {
		return emite;
	}

	public void setEmite(Emisor emite) {
		this.emite = emite;
	}


	public File[] getContenido() {
		return contenido;
	}

	public void setContenido(File[] contenido) {
		this.contenido = contenido;
	}
}
