package com.sun.directory.examples;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.datatype.XMLGregorianCalendar;

import sun.print.resources.serviceui;

import com.sun.businessLogic.validate.Emisor;
import com.sun.businessLogic.validate.LeerDocumentos;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.tradise.reportes.entidades.FacCabDocumento;
import com.tradise.reportes.servicios.ReporteSentencias;
import com.util.util.key.Environment;

public class Acciones {
	/*************************************************************************/
	/* Validacion de Estado RECIBIDA */
	/*************************************************************************/
	/****************** Insert con la Base de Datos ***************************/
	public synchronized void documentoRecibido(Emisor emite, InfoEmpresa infoEmp) {
		int intentos = Environment.c
				.getInt("facElectronica.general.ws.consultaAutorizacion.intentos");
		int timeIntentos = Environment.c
				.getInt("facElectronica.general.ws.consultaAutorizacion.timeIntentos");
		
		FacCabDocumento CabDoc = null;
		ReporteSentencias rpSen = new ReporteSentencias();
		String nameFile = "",traceError ="";
		boolean flagReproceso = false;
		int	li_envio = -1;
		String resultEmail ="";
		//SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
		//String strDate = sm.format(new Date());
		
		//Metodo para registrar comprobante en BD 
		CabDoc = registroComprobante(emite, infoEmp, "S","Recibido por SRI","RS");
		/*
		 * ======================================================== FIN -
		 * Validaciones por cada tipo de documento
		 * ========================================================
		 */

		String respAutorizacion = "";
		String[] infoAutorizacion = new String[10];
		/*
		 * ======================================================== INI -
		 * Obtengo estado del docuemnto - 2da consulta
		 * ========================================================
		 */
		
		try {
			respAutorizacion = AutorizacionComprobantesWs
					.autorizarComprobanteIndividual(emite.getInfEmisor()
							.getClaveAcceso(), emite.getFilexml(), new Integer(
							emite.getInfEmisor().getAmbiente()).toString(),
							infoEmp.getDirAutorizados(), infoEmp
									.getDirNoAutorizados(), infoEmp
									.getDirFirmados(),
							// VPI - Time out parametrizar
									intentos, timeIntentos, emite.getInfEmisor().getSecuencial());
			
				if (respAutorizacion.equals("")) {
					infoAutorizacion[0] = "SIN-RESPUESTA";
				} else {
					infoAutorizacion = respAutorizacion.split("\\|");
				}
		} catch (Exception excep) {
			infoAutorizacion[0] = "SIN-RESPUESTA";
		}
		/*
		 * ======================================================== INI -
		 * Obtengo estado del docuemnto - 2da consulta
		 * ========================================================
		 */
		String reportePdf = "";
		if (infoAutorizacion[0].trim().equals("AUTORIZADO"))
		{
			String xmlString = "";
			String numeroAutorizacion = infoAutorizacion[1];
			String fechaAutorizacion = infoAutorizacion[2];
			XMLGregorianCalendar xmlFechaAutorizacion = XMLGregorianCalendarImpl.parse(fechaAutorizacion);
			Date dateFechaAutorizacion = ServiceDataHilo.toDate(xmlFechaAutorizacion);
			CabDoc.setAutorizacion(numeroAutorizacion);

			System.out.println("Copiando AUTORIZADO::" + infoEmp.getDirAutorizados() + emite.getFilexml());
			try {
				File verificaXml = new File(infoEmp.getDirAutorizados()
						+ emite.getFilexml());
				if (verificaXml.exists()) {
					System.out.println("Copiando AUTORIZADO");
					xmlString = ArchivoUtils.archivoToString(infoEmp
							.getDirAutorizados() + emite.getFilexml());
				} else {
					verificaXml = new File(infoEmp.getDirFirmados()
							+ emite.getFilexml());
					if (verificaXml.exists())
						xmlString = ArchivoUtils.archivoToString(infoEmp
								.getDirFirmados() + emite.getFilexml());
					System.out.println("Copiando FIRMADO");
				}
			} catch (Exception e) {
				System.out.println("Error:: COPIANDO AUTORIZADO");
				e.printStackTrace();
			}
			
			//Ojo con este metodo xq siempre a sobreescribir la informacion en la base
			//con esatdo RS y luego el siguiente metodo lo actualiza con el estado que debe quedar
			//Metodo para registrar comprobante en BD 
			registroComprobante(emite, infoEmp, "N","Autorizado por SRI","AT");
			
			// VPI - Se modifica control de excepciones
			rpSen.updateEstadoAutorizacionXmlDocumento(CabDoc, xmlString,
					fechaAutorizacion, dateFechaAutorizacion, xmlString);

			emite.getInfEmisor().setNumeroAutorizacion(infoAutorizacion[1]);
			emite.getInfEmisor().setFechaAutorizacion(fechaAutorizacion);
			emite.getInfEmisor().setFechaAutorizado(dateFechaAutorizacion);
			nameFile = emite.getFilexml().replace("xml", "pdf");
			ServiceDataHilo.delFile(emite, "", infoEmp.getDirGenerado(), "");

			try {
				System.out
						.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
				System.out.println("pathReports::" + infoEmp.getPathReports());
				System.out.println("rutaFirmado::" + infoEmp.getDirFirmados());
				System.out.println("nameFile::" + nameFile);
				System.out
						.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
				reportePdf = com.tradise.reportes.reportes.ReporteUtil
						.generaPdfDocumentos(emite, emite.getInfEmisor()
								.getRuc(), emite.getInfEmisor()
								.getCodEstablecimiento(), emite.getInfEmisor()
								.getCodPuntoEmision(), emite.getInfEmisor()
								.getCodDocumento(), emite.getInfEmisor()
								.getSecuencial(), infoEmp.getPathReports(),
								infoEmp.getDirFirmados(), nameFile);
				System.out.println("reportePdf::" + reportePdf);
				System.out.println("reporteXml::" + infoEmp.getDirAutorizados()
						+ emite.getFilexml());
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Se comenta para que la validacion de si existe el usuario se dentro del 
			// metodo de insercion 
/*
			if (rpSen.existeUsuario(infoEmp.getRuc(), CabDoc.getCodCliente(),
					"C") <= 0) {*/
				
			String clave = (CabDoc.getIdentificacionComprador() != null && CabDoc
						.getIdentificacionComprador().length() > 6) == true ? CabDoc
						.getIdentificacionComprador().substring(0, 6) : CabDoc
						.getIdentificacionComprador();

				String msg = ReporteSentencias.insertaUsuarioRol(infoEmp.getRuc(),
						CabDoc.getRazonSocialComprador(),
						CabDoc.getIdentificacionComprador(),
						FacEncriptarcadenasControlador.encrypt(clave), "Clien",
						"C");
				System.out.println(msg);

		//	}
			

			
			//Servicio adicionales
			//Actualizacion ERP con informacion SRI 
			//============================================================
			int respUdateErp = 1;
			//Flag de autualizacion en ERP
			if(ServiceDataHilo.flagUpdateERP.endsWith("S")){
				respUdateErp =	rpSen.updateERPInfoSRI(emite, respAutorizacion,"S","S");		
			}
			if(respUdateErp<=0)	{ 
				flagReproceso = true;
				traceError = "Error al actualizar en ERP - verificar enlace de datos ::Transaccion autorizada::";
				li_envio = ServiceDataHilo.enviaEmail("message_error", emite,
						"",respAutorizacion+" >> "+traceError,
						infoEmp.getDirAutorizados()+ emite.getFilexml(), null);
				resultEmail = li_envio >= 0 ? "Mail enviado correctamente": "Error en envio de Mail";
			//============================================================
			}else{
				// VPI cambio en el nombre del archivo enviado por correo y control
				// de excepciones interno
				 li_envio = ServiceDataHilo.enviaEmailCliente("message_exito",
						emite,"",
						infoEmp.getDirAutorizados()+ emite.getFilexml(),
						reportePdf);
				
			resultEmail = li_envio >= 0 ? "Mail enviado correctamente": "No se envio de Mail";
			emite.insertaBitacora(emite, "AT", "Docuemnto Autorizado "+" >>"+resultEmail,"",  "", "", "");
			System.out.println((li_envio >= 0) ? "Mail enviado Correctamente"
					: "Error en envio de Mail");
			}
			
			//============================================================
			
			// VPI se modifica las condiciones
		} else if (infoAutorizacion[0].equals("NO AUTORIZADO")) {
			if ((emite.getInfEmisor().getCodDocumento().equals("01"))
					|| (emite.getInfEmisor().getCodDocumento().equals("04"))) {
				// VPI - Se quita descripcion
				// respAutorizacion = respAutorizacion;

				ServiceDataHilo.delFile(emite, infoEmp.getDirFirmados(),
						infoEmp.getDirRecibidos(),
						infoEmp.getDirNoAutorizados());

				// VPI - Se manda actualizar respuesta de SRI - consultar si
				// todos estan de acuerdo
				// y que tome la emision propia del documento
				// rpSen.updateEstadoDocumento("NA", respAutorizacion,
				// emite.getInfEmisor().getTipoEmision(),
				// CabDoc);

				// HFU
				String xmlStringNoAutorizado = ArchivoUtils
						.archivoToString(infoEmp.getDirNoAutorizados()
								+ emite.getFilexml());
				
				
				//Ojo con este metodo xq siempre a sobreescribir la informacion en la base
				//con esatdo RS y luego el siguiente metodo lo actualiza con el estado que debe quedar
				//Metodo para registrar comprobante en BD 
								
				registroComprobante(emite, infoEmp, "S","No autorizado por SRI","NA");
				respAutorizacion = respAutorizacion.replaceAll("\\n","<br>");
				//Actualizo informacion
				rpSen.updateEstadoDocumento("NA", respAutorizacion, emite
						.getInfEmisor().getTipoEmision(), CabDoc,
						xmlStringNoAutorizado);
				

				
			}
			//Servicio adicionales
			//Actualizacion ERP con informacion SRI 
			//============================================================
			int respUdateErp = 1;
			//Flag de autualizacion en ERP
			if(ServiceDataHilo.flagUpdateERP.endsWith("S")){
				respUdateErp =	rpSen.updateERPInfoSRI(emite, respAutorizacion,"N","N");		
			}
			if(respUdateErp<=0)	{ 
				flagReproceso = true;
				traceError = "Error al actualizar en ERP - verificar enlace de datos ::Transaccion no autorizada:: ";
				li_envio = ServiceDataHilo.enviaEmail("message_error", emite,
						"", respAutorizacion+" >> "+traceError,
						infoEmp.getDirNoAutorizados()+ emite.getFilexml(), null);
				resultEmail = li_envio >= 0 ? "Mail enviado correctamente": "Error en envio de Mail";
			//============================================================
			}else{
			li_envio = ServiceDataHilo.enviaEmail("message_error", emite,
					"", respAutorizacion,
					infoEmp.getDirNoAutorizados()+ emite.getFilexml(), null);
			resultEmail = li_envio >= 0 ? "Mail enviado correctamente": "Error en envio de Mail";
			emite.insertaBitacora(emite, "NA", respAutorizacion+" >>"+resultEmail,"",  "", "", "");
			}
		} else {

			// if (infoAutorizacion[0].equals("TRANSMITIDO SIN RESPUESTA")) {
			// Se mueve el archivo a generados para que el proceso lo vuelva a
			// tomar
			
			if (infoAutorizacion[0].trim().equals("NO-EXISTE-DOCUMENTO")) {
				//Se reenvia el documento al SRI 
					emite.insertaBitacora(emite, "RE","" ,  "::Reproceso de Envio, consulta autorizacion no retorno comprobantes ::", "", "", "");
				
				//Se actualiza estado del documento para que vuelva a ser tomado por el proceso
				//GBM: Existen momentos en los cuales ingresa a esta sección a pesar de que el documento esté autorizado!!! 
				//Se agrega segundo control para evitar esto
				try {
					synchronized (this) {
						String estadoDoc = emite.statusDocumento(
								emite.getInfEmisor().getAmbiente(),
								emite.getInfEmisor().getRuc(),
								emite.getInfEmisor().getTipoComprobante(),
								emite.getInfEmisor().getCodEstablecimiento(),
								emite.getInfEmisor().getCodPuntoEmision(),
								emite.getInfEmisor().getSecuencial()).trim();
						
						if (!estadoDoc.equals("AT") || !estadoDoc.equals("SR")) {
							rpSen.updateEstadoDocumento("RE",
									"Reproceso Envio", emite.getInfEmisor()
											.getTipoEmision(), CabDoc, null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
				
				ArchivoUtils.stringToArchivo(
						infoEmp.getDirGenerado() + emite.getFilexml(),
						ArchivoUtils.archivoToString(emite.getFileXmlBackup()));
				
			
			} else {
				flagReproceso = true;
				traceError = "consulta autorizacion sin Respuesta";
			}
			

			/*
			 * // Se envia documento por contingencia
			 * documentoContingencia(emite, respAutorizacion, infoEmp, CabDoc,
			 * "Error en Consulta de Autorizacion ");
			 */
		}
		
		if(flagReproceso){
				emite.insertaBitacora(emite, "RC","" ,  "::Reproceso Consulta, "+ traceError +" :: >>"+resultEmail, "", "", "");

			ArchivoUtils.stringToArchivo(
					infoEmp.getDirGenerado() + emite.getFilexml(),
					ArchivoUtils.archivoToString(infoEmp.getDirRecibidos()
							+ emite.getFilexml()));
		}
		
	}

	public FacCabDocumento registroComprobante(Emisor emite, InfoEmpresa infoEmp,
			String validaExistencia, String descripcion, String estado) {

		FacCabDocumento CabDoc = null;
		ReporteSentencias rpSen = new ReporteSentencias();

		/*
		 * ======================================================== INI -
		 * Validaciones por cada tipo de documento
		 * ========================================================
		 */
		// Validacion para Facturas
		System.out.println("Inicio Registro comprobante ::"+emite.toString());
		try {
			if (emite.getInfEmisor().getCodDocumento().equals("01")) {
				CabDoc = ServiceDataHilo.preparaCabDocumentoFac(emite, infoEmp
						.getRuc(),
						emite.getInfEmisor().getCodEstablecimiento(), emite
								.getInfEmisor().getCodPuntoEmision(), emite
								.getInfEmisor().getTipoComprobante(), emite
								.getInfEmisor().getSecuencial(),
								descripcion, estado);
				// Agrego vakidacion para validar existencia de documento en la
				// base
				// en caso de un reproceso la informacion se actualizada
				// si es 'N' el mandatorio es validaexistencia
				// si es 'S' el mandatorio es
				// !rpSen.existFacCabDocumentos(CabDoc)				
			if (CabDoc!= null)	{				
			
				//Si no existe DEBE insertar:
				if (validaExistencia.equalsIgnoreCase("N")){
					rpSen.insertFacCabDocumentos(CabDoc);
					rpSen.insertFacDetallesDocumento(CabDoc
							.getListDetalleDocumento());
					rpSen.insertInfoAdicional(emite);
				}
				else{
					if (!rpSen.existFacCabDocumentos(CabDoc)) {
						rpSen.insertFacCabDocumentos(CabDoc);
						rpSen.insertFacDetallesDocumento(CabDoc
								.getListDetalleDocumento());
						rpSen.insertInfoAdicional(emite);
					}
				}
				
				if (estado.equalsIgnoreCase("AT")){
					int existCli = rpSen.existeCliente(infoEmp.getRuc(),
							CabDoc.getCodCliente(), "C");

					if (existCli == 0) {
						rpSen.insertaClientes(infoEmp.getRuc(),
								CabDoc.getRazonSocialComprador(),
								CabDoc.getDireccion(),
								CabDoc.getEmailCliente(), "C",
								CabDoc.getTipoIdentificacion(),
								CabDoc.getRise(), CabDoc.getTelefono(),
								CabDoc.getIdentificacionComprador(),
								String.valueOf(CabDoc.getCodCliente()));
					}
				}
			}
			}
			// Validacion para retenciones
			if (emite.getInfEmisor().getCodDocumento().equals("07")) {
				CabDoc = ServiceDataHilo.preparaCabDocumentoRet(emite, infoEmp
						.getRuc(),
						emite.getInfEmisor().getCodEstablecimiento(), emite
								.getInfEmisor().getCodPuntoEmision(), emite
								.getInfEmisor().getTipoComprobante(), emite
								.getInfEmisor().getSecuencial(),
						descripcion, estado);

				// Agrego vakidacion para validar existencia de documento en la
				// base
				// en caso de un reproceso la informacion se actualizada
				// si es 'N' el mandatorio es validaexistencia
				// si es 'S' el mandatorio es
				// !rpSen.existFacCabDocumentos(CabDoc)
				
				if(CabDoc != null){				
				//Si no existe DEBE insertar:
				if (validaExistencia.equalsIgnoreCase("N")){					
					rpSen.insertFacCabDocumentos(CabDoc);
					rpSen.insertFacDetallesRetenciones(CabDoc);
					rpSen.insertInfoAdicional(emite);
				}
				else{
					if (!rpSen.existFacCabDocumentos(CabDoc)) {
						rpSen.insertFacCabDocumentos(CabDoc);
						rpSen.insertFacDetallesRetenciones(CabDoc);
						rpSen.insertInfoAdicional(emite);
					}
				}
								
	
					
				int existCli = rpSen.existeCliente(infoEmp.getRuc(),
							CabDoc.getCodCliente(), "P");
				if(estado.equalsIgnoreCase("AT")){
					if (existCli == 0) {
						rpSen.insertaClientes(
								infoEmp.getRuc(),
								CabDoc.getRazonSocialComprador(),
								(CabDoc.getDireccion() == null ? "" : CabDoc
										.getDireccion()),
								(CabDoc.getEmailCliente() == null ? "" : CabDoc
										.getEmailCliente()),
								"P",
								CabDoc.getTipoIdentificacion(),
								CabDoc.getRise(),
								(CabDoc.getTelefono() == null ? "" : CabDoc
										.getTelefono()), CabDoc
										.getIdentificacionComprador(), String
										.valueOf(CabDoc.getCodCliente()));
					}
					}
			}
			}
			// Validacion para Notas credito
			if (emite.getInfEmisor().getCodDocumento().equals("04")) {
				CabDoc = ServiceDataHilo.preparaCabDocumentoCre(emite, infoEmp
						.getRuc(),
						emite.getInfEmisor().getCodEstablecimiento(), emite
								.getInfEmisor().getCodPuntoEmision(), emite
								.getInfEmisor().getTipoComprobante(), emite
								.getInfEmisor().getSecuencial(),
								descripcion, estado);

				// Agrego vakidacion para validar existencia de documento en la
				// base
				// en caso de un reproceso la informacion se actualizada
				// si es 'N' el mandatorio es validaexistencia
				// si es 'S' el mandatorio es
				// !rpSen.existFacCabDocumentos(CabDoc)
				
				if (CabDoc!= null){
					
				
				//Si no existe DEBE insertar:
				if (validaExistencia.equalsIgnoreCase("N")){					
					rpSen.insertFacCabDocumentos(CabDoc);
					rpSen.insertInfoAdicional(emite);
					rpSen.insertFacDetallesDocumento(CabDoc
							.getListDetalleDocumento());
				}
				else{
					if (!rpSen.existFacCabDocumentos(CabDoc)) {
						rpSen.insertFacCabDocumentos(CabDoc);
						rpSen.insertInfoAdicional(emite);
						rpSen.insertFacDetallesDocumento(CabDoc
								.getListDetalleDocumento());
					}
				}

				
				if(estado.equalsIgnoreCase("AT")){
					
				
					int existCli = rpSen.existeCliente(infoEmp.getRuc(),
							CabDoc.getCodCliente(), "C");
					if (existCli == 0) {
						rpSen.insertaClientes(infoEmp.getRuc(),
								CabDoc.getRazonSocialComprador(),
								CabDoc.getDireccion(),
								CabDoc.getEmailCliente(), "C",
								CabDoc.getTipoIdentificacion(),
								CabDoc.getRise(), CabDoc.getTelefono(),
								CabDoc.getIdentificacionComprador(),
								String.valueOf(CabDoc.getCodCliente()));
					}
				}				
			}
		}
			
			System.out.println("Fin Registro comprobante ::"+emite.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Registro comprobante ::"+emite.toString());
		}
		return CabDoc;
	}

	// =======================================================================
	// Funcion para generar y enviar PDF
	// =======================================================================
	public void generaEnviaPdf(Emisor emite, InfoEmpresa infoEmp,
			String mensajeAutorizacion) {
		
		String reportePdfContingencia = null;
		String enviaPdf = Environment.c
				.getString("facElectronica.general.contingencia.enviaPdf");

		String envioCliente = Environment.c
				.getString("facElectronica.general.contingencia.envioCliente");

		if (enviaPdf.equals("S")) {
			try {

				String nameFile = emite.getFilexml().replace("xml", "pdf");
				System.out
						.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
				System.out.println("pathReports::" + infoEmp.getPathReports());
				System.out.println("rutaFirmado::" + infoEmp.getDirFirmados());
				System.out.println("nameFile::" + nameFile);
				System.out
						.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
				// emite.getInfEmisor().setClaveAcceso("");
				reportePdfContingencia = com.tradise.reportes.reportes.ReporteUtil
						.generaPdfDocumentos(emite, emite.getInfEmisor()
								.getRuc(), emite.getInfEmisor()
								.getCodEstablecimiento(), emite.getInfEmisor()
								.getCodPuntoEmision(), emite.getInfEmisor()
								.getCodDocumento(), emite.getInfEmisor()
								.getSecuencial(), infoEmp.getPathReports(),
								infoEmp.getDirFirmados(), nameFile);
				System.out.println(emite.toString() + "reportePdf::"
						+ reportePdfContingencia);
				System.out.println(emite.toString() + "reporteXml::"
						+ infoEmp.getDirFirmados() + emite.getFilexml());

				int li_envio = -1;
				String destinatario = "";
				if (envioCliente.equals("S")) {
					li_envio = ServiceDataHilo.enviaEmailCliente(
							"message_exito", emite, "", null,
							reportePdfContingencia);
					destinatario ="Cliente :" + emite.getInfEmisor().getEmailCliente();
				} else {
					li_envio = ServiceDataHilo.enviaEmail("message_contingencia",
							emite, "", mensajeAutorizacion, null,
							reportePdfContingencia);
					destinatario ="Help desk"+Environment.c.getString("facElectronica.alarm.email.receivers-list");;
				}
				
				String resultEmail = li_envio >= 0 ? "Mail contingencia enviado correctamente": "Error en envio de Mail contingencia";
				emite.insertaBitacora(emite, "MC", "Generacion y envio de pdf de contingencia::"+ " >> "+resultEmail +" a destinatario : "+destinatario,"",  "", "", "");

				if (li_envio >= 0)
					System.out
							.println("Mail contingencia enviado Correctamente");
				else
					System.out.println("Error en envio de Mail contingencia");
				
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Acciones.generaEnviaPdf() >> Error en generacion-envio de mail contingencia");
				emite.insertaBitacora(emite,"EX","", "Acciones.generaEnviaPdf() >> Error en generacion-envio de mail contingencia::"+ex.getMessage(),  "", "", "");
			}
		}

	}	
	
	
	
	/**
	 * Método para registrar documentos que causan excepciones por estar incompletos. Estado EX (Error XML)
	 * Inserta los valores mínimos necesarios para que aparezca el documento en el portal y pueda ser reprocesado (APLICA ÚNICAMENTE A MICROS Y OPERA)
	 * @param e
	 */	
	public boolean registraDocumentoIncompleto(Emisor e){
		
		FacCabDocumento docIncompleto = new FacCabDocumento();
		boolean registrado = false;
    	
		
		//Seteando los campos necesarios a nivel de base:
    	docIncompleto.setAmbiente(e.getInfEmisor().getAmbiente());
    	docIncompleto.setRuc(e.getInfEmisor().getRuc());
    	docIncompleto.setCodEstablecimiento(e.getInfEmisor().getCodEstablecimiento());
    	docIncompleto.setCodPuntEmision(e.getInfEmisor().getCodPuntoEmision());
    	docIncompleto.setSecuencial(e.getInfEmisor().getSecuencial());
    	docIncompleto.setCodigoDocumento(e.getInfEmisor().getCodDocumento());    	
    	docIncompleto.setESTADO_TRANSACCION("EX");
    	
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    	docIncompleto.setFechaEmision(formato.format(cal.getTime()));
    	    	
    	
    	//Seteando los campos necesarios para el portal:
    	docIncompleto.setTotalSinImpuesto(0);
    	docIncompleto.setTotalDescuento(0);
    	docIncompleto.setPropina(0);
    	docIncompleto.setMoneda("0");
    	docIncompleto.setAutorizacion("0");
    	docIncompleto.setClaveAcceso("0");
    	docIncompleto.setImporteTotal(e.getInfEmisor().getImporteTotal());
    	docIncompleto.setTipIdentificacionComprador(e.getInfEmisor().getTipoIdentificacion()==null?"0":e.getInfEmisor().getTipoIdentificacion());
    	docIncompleto.setIdentificacionComprador(e.getInfEmisor().getIdentificacionComp()==null?"SIN ID":e.getInfEmisor().getIdentificacionComp());
    	docIncompleto.setRazonSocialComprador(e.getInfEmisor().getRazonSocialComp()==null?"NO DISPONIBLE":e.getInfEmisor().getRazonSocialComp());
    	    	
    	docIncompleto.setTipoEmision("0");
    	docIncompleto.setSubtotal12(0);
    	docIncompleto.setSubtotal0(0);
    	docIncompleto.setSubtotalNoIva(0);
    	docIncompleto.setIva12(0);
    	docIncompleto.setTotalvalorICE(0);
    	docIncompleto.setMotivoValor(0);
    	docIncompleto.setIsActive("Y"); //Qué sucede en N?
    	  	
    	ReporteSentencias crud = new ReporteSentencias();
    	
    	//No se valida si existe, porque igual hace un delete antes del INSERT
    	//Si el documento estaba en otro estado x ej: No Autorizado, será eliminado y actualizado a EX (Error XML)
    	//Si no existe el documento, insertarlo
    	/*if (!crud.existFacCabDocumentos(docIncompleto)){
    		crud.insertDocumentoEX(docIncompleto);
    	} */   	
    	String msj = crud.insertDocumentoEX(docIncompleto);
    	if (msj.equals("OK")){
    		registrado = true;
    	}
    	else{
    		registrado = false;
    	}
    	return registrado;
	}
	
	
public void actualizarEstadoErrorDocumento(Emisor e, String mensaje){
		
		FacCabDocumento docError = new FacCabDocumento();
    	
		docError.setAmbiente(e.getInfEmisor().getAmbiente());
		docError.setRuc(e.getInfEmisor().getRuc());
		docError.setCodEstablecimiento(e.getInfEmisor().getCodEstablecimiento());
		docError.setCodPuntEmision(e.getInfEmisor().getCodPuntoEmision());
		docError.setSecuencial(e.getInfEmisor().getSecuencial());
		docError.setCodigoDocumento(e.getInfEmisor().getCodDocumento());    	
    	
    	ReporteSentencias crud = new ReporteSentencias();
    	//Si existe el documento, actualizar el estado de error
    	if (crud.existFacCabDocumentos(docError)){
    		crud.updateMsjError(mensaje, docError);
    	}
    	
		
	}
	
	
	// =======================================================================
	// Funcion para estados de contingencia
	// =======================================================================
	public void documentoContingencia(Emisor emite, String respAutorizacion,
			InfoEmpresa infoEmp, FacCabDocumento CabDoc,
			String mensajeContingencia) {
		ReporteSentencias rpSen = new ReporteSentencias();
		SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = sm.format(new Date());
		int clavesDisponibles = 0;
		List umbralClavesContingencia = Environment.c
				.getList("facElectronica.database.facturacion.sql.umbralClavesContingencias");

		if (emite.getInfEmisor().getTipoEmision().equals("1")) {
			clavesDisponibles = emite.verificaClavesContingencia(String
					.valueOf(emite.getInfEmisor().getAmbiente()), emite
					.getInfEmisor().getRuc());

			// VPI - se agrega validacion de claves de contingencias disponibles
			if (clavesDisponibles > 0) {
				for (int i = 0; i < umbralClavesContingencia.size(); i++) {
					System.out.println(" Valor en lista :"
							+ umbralClavesContingencia.get(i).toString());
					if (Integer.valueOf(umbralClavesContingencia.get(i)
							.toString()) == clavesDisponibles) {
						// Se debe enviar notificacion por mail de que ya quedan
						// pocas
						// claves de contingencia
						System.out.println(" Envio Notificacion :"
								+ umbralClavesContingencia.get(i).toString());
						break;
					}
				}

				// continua flujo normal
				// VPI - se quita validacion por tipos de documentos
				/*
				 * if ((emite.getInfEmisor().getCodDocumento().equals("01")) ||
				 * (emite.getInfEmisor().getCodDocumento().equals("07"))) {
				 */

				respAutorizacion = respAutorizacion
						+ " >>"
						+ mensajeContingencia
						+ ", Verificar que la transaccion se va por el Esquema de Contingencia ";
				// VPI - Se comenta hasta saber con fin se los elimina
				/*
				 * ServiceDataHilo.delFile(emite, infoEmp.getDirFirmados(),
				 * infoEmp.getDirRecibidos(), infoEmp.getDirNoAutorizados());
				 */

				rpSen.updateEstadoDocumento("CT", "Contingencia por SRI", "2",
						CabDoc, null);

				// VPI - Falta un metodo que avise si se acabaron las claves
				// de contingencias y que las transacciones las deje en
				// algun
				// estado para que vuelvan a ser tomadas una vez que se
				// reestablezca el servicio o se reestablezca el servicio
				// SRI.

				String ls_clave_accesoCont = "";
				String ls_clave_contingencia = "";
				try {
					ls_clave_contingencia = emite.obtieneClaveContingencia(
							emite.getInfEmisor().getRuc(), emite.getInfEmisor()
									.getAmbiente(), "0");

					ls_clave_accesoCont = LeerDocumentos
							.generarClaveAccesoContingencia(emite,
									ls_clave_contingencia);
					// VPI - xq le pone que es diferente de 49
					if (ls_clave_accesoCont.length() != 49) {

						ls_clave_accesoCont = LeerDocumentos
								.generarClaveAccesoContingencia(emite,
										ls_clave_contingencia);
					}
				} catch (Exception excep) {
					excep.printStackTrace();
				}

				emite.getInfEmisor().setTipoEmision("2");
				emite.getInfEmisor().setClaveAcceso(ls_clave_accesoCont);

				String ls_xml_inicial = ArchivoUtils.archivoToString(infoEmp
						.getDirRecibidos()
						+ emite.getFilexml().replace(".xml", "_backup.xml"));

				ServiceDataHilo.copiarXmlDir(
						infoEmp.getDirRecibidos() + emite.getFilexml(),
						infoEmp.getDirContingencias());

				String ls_xml = ArchivoUtils.archivoToString(infoEmp
						.getDirContingencias() + emite.getFilexml());
				// Verifico el tipo de emision para saber si es la primera vez
				// que
				// cae
				// en contingencia inserto caso contrario actualizo.

				emite.insertaColaDocumentos(String.valueOf(emite.getInfEmisor()
						.getAmbiente()), emite.getInfEmisor().getRuc(), emite
						.getInfEmisor().getCodEstablecimiento(), emite
						.getInfEmisor().getCodPuntoEmision(), emite
						.getInfEmisor().getSecuencial(), emite.getInfEmisor()
				// VPI se cambia estado TD por "CT"
						.getTipoComprobante(), strDate, "CT", infoEmp
						.getDirContingencias(), emite.getFilexml(),
						ls_clave_contingencia, emite.getInfEmisor()
								.getClaveAcceso(), ls_clave_accesoCont, ls_xml,
						ls_xml_inicial);
				// Genero y envio el pdf segun configuracion
				generaEnviaPdf(emite, infoEmp, respAutorizacion);
			} else {
				// VPI - si no existen claves de contingencias se retorna el
				// archivo
				// a generados
				// para que vuelva al flujo normal hasta que se restablezca el
				// servicio o en su defecto
				// hayan claves de contingencias disponibles
				ArchivoUtils.stringToArchivo(
						infoEmp.getDirGenerado() + emite.getFilexml(),
						ArchivoUtils.archivoToString(emite.getFileXmlBackup()));
			}

		} else {
			try {
				// VPI - si la emision es dierente de "1" osea que vuelva
				// caer en contingencia
				emite.insertaBitacoraDocumento(
						String.valueOf(emite.getInfEmisor().getAmbiente()),
						emite.getInfEmisor().getRuc(),
						emite.getInfEmisor().getCodEstablecimiento(),
						emite.getInfEmisor().getCodPuntoEmision(),
						emite.getInfEmisor().getSecuencial(),
						emite.getInfEmisor().getTipoComprobante(),
						strDate,
						"CT",
						emite.toString()
								+ "Reproceso Consulta consulta autorizacion sin Respuesta",
						"", "", "", "", "", emite.getInfEmisor()
								.getTipoEmision());

				// Actualizar estado del documento en la cola
				rpSen.updateColaDocumentos(CabDoc, "CT");

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
}
