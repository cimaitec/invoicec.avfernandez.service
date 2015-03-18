package com.sun.DAO;

import java.util.ArrayList;
import java.util.Date;

import com.sun.DAO.InformacionAdicional;

public class InformacionTributaria {
	
	//VE //Cabecera
	private String tipoComprobante;
	private String idComprobante;
	private String version;
	private int sizeVE = 3;
	
	//IT //Cabecera
	private int    ambiente;
	private String tipoEmision; 
	private String razonSocial;	
	private String nombreComercial;
	private String ruc;
	private String claveAcceso;
	private String fechaAutorizacion;
	private Date   fechaAutorizado;
	private String NumeroAutorizacion;

	private String codDocumento;
	private String codEstablecimiento;	
	private String codPuntoEmision;
	private String secuencial;
	private String direccionMatriz;
	private String mailEmpresa;		
	private int    sizeIT = 12;
	
	//Informacion Adicional Tomada desde la Base de Datos
	private String ambientePuntoEmision;
	private String mailEstablecimiento;
	private String pathAnexoEstablecimiento;
	private String mensajeEstablecimiento;
	private String telefonoEstablecimiento;
	private String IpEstablecimiento;
	
	
	//Informacion Adicional Tomada desde la Base de Datos
	private String rucFirmante;
	
	//IC //Cabecera
	private String   fecEmision;
	private String direccionEstablecimiento;
	private int    contribEspecial;
	private String obligContabilidad;
	private String tipoIdentificacion;
	private String guiaRemision;
	private String razonSocialComp;
	private String identificacionComp;
	private String mailComp;
	private String moneda;
	private String rise;
	private	String codDocModificado;
	private	String numDocModificado;
	private String   fecEmisionDoc;
	private double valorModificado;
	private String motivo;		
	private String   periodoFiscal;	
	private String dirPartida;
	private String razonSocTransp;
	private int    tipoIdentTransp;
	private String rucTransp;
	private String aux1;
	private String aux2;
	private String   fechaIniTransp;
	private String   fechaFinTransp;
	private String placa;
	private int    sizeIC = 25;
	
	//T //Cabecera
	private double subTotal12=0;
	private double subTotal0=0;
	private double subTotalNoSujeto=0;
	private double totalSinImpuestos=0;
	private double totalDescuento=0;
	private double totalICE=0;
	private double totalIva12=0;
	private double importeTotal=0;
	private double propina=0;
	private double importePagar=0;
	private int    sizeT = 10;
	private String codCliente;
	private String direccion;
	private String telefono;
	private String emailCliente;
	private String local;
	private String caja;
	private String idMovimiento;
	
	/*
	private int codImpRetenidos;
	private String codRetencion;
	private double baseImponible;
	private double porcRetenido;
	private double valorRetenido;
	private int codDocSustento;
	private long numDocSustento;
	private Date fechaEmisionDocSustento;
	private int sizeTIR = 8;
	*/
	
	/*//TI //Cabecera
	private int codTotalImpuestos;
	private int codPorcentImp;
	private long tarifaImp;
	private int baseImponibleImp;
	private double valorImp;
	private String impuestoImp;
	private int sizeTI = 6;*/
	
	//MO //Cabecera
	private String motivoRazonND;
	private double motivoValorND;
	private int    sizeMO = 2;
	
	//DEST //Cabecera
	/*
	private String identDestinatario;
	private String razonSocialDestinatario;
	private String dirDestinatario;
	private String motTraslDestinatario;
	private String docAduanero;
	private int codEstabDestino;
	private String rutaDest;
	private int codDocSustentoDest;
	private long numDocSustentoDest;
	private long numAutDocSustDest;
	private Date fechEmisionDocSustDest;
	private int sizeDEST = 11;*/
	
	private ArrayList<Destinatarios> ListDetDestinatarios;
	
	//TIR
	//Detalle de Impuestos Totales Retenciones
	private ArrayList<DetalleTotalImpuestosRetenciones> ListDetDetImpuestosRetenciones;
	
	//TI
	//Detalle de Impuestos Totales
	private ArrayList<DetalleTotalImpuestos> ListDetDetImpuestos;
	
	//Detalles del Documento
	private ArrayList<DetalleDocumento> ListDetDocumentos;
	
	//IA
	private ArrayList<InformacionAdicional> ListInfAdicional;
	
	//MO
	private ArrayList<InformacionAdicional> ListInfMotivos;
	
	private ArrayList<DetalleImpuestosRetenciones> ListDetImpuestosRetenciones;
	
	//Informacion Adicional
	private static String _pathGenerados;
	private static String _pathFirmados;	
	private static String _pathInfoRecibida;
	private static String _pathAutorizados;
	private static String _pathNoAutorizados;
	private static String _PathCompContingencia;
	private static String _PathFirma;
	private static String _ClaveFirma;
	private static String _TipoFirma;
	private static String _pathXsd;
	private static String _pathJasper;
	
	public String getMailCliente(){
		String mail = "";
		if (ListInfAdicional!= null){
				if (ListInfAdicional.size()>0) {
			   		  for (int i = 0; i < ListInfAdicional.size(); i++) {
			   		    InformacionAdicional infoAd = new InformacionAdicional();
			   		    	infoAd = ListInfAdicional.get(i);
			   		    	if (infoAd.getName().equals("EMAIL")){
			   		    		mail=infoAd.getValue(); 
			   		    	}
			   		  }
				}
		}
		
	   	return mail;	   		    
	}
	//----------------------------------------GET AND SETTER	
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	public String getIdComprobante() {
		return idComprobante;
	}
	public void setIdComprobante(String idComprobante) {
		this.idComprobante = idComprobante;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getSizeVE() {
		return sizeVE;
	}
	public void setSizeVE(int sizeVE) {
		this.sizeVE = sizeVE;
	}
	public int getAmbiente() {
		return ambiente;
	}
	public void setAmbiente(int ambiente) {
		this.ambiente = ambiente;
	}
	public String getTipoEmision() {
		return tipoEmision;
	}
	public void setTipoEmision(String tipoEmision) {
		this.tipoEmision = tipoEmision;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getNombreComercial() {
		return nombreComercial;
	}
	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial = nombreComercial;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getClaveAcceso() {
		return claveAcceso;
	}
	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}
	public String getCodDocumento() {
		return codDocumento;
	}
	public void setCodDocumento(String codDocumento) {
		this.codDocumento = codDocumento;
	}
	public String getCodEstablecimiento() {
		return codEstablecimiento;
	}
	public void setCodEstablecimiento(String codEstablecimiento) {
		this.codEstablecimiento = codEstablecimiento;
	}
	public String getCodPuntoEmision() {
		return codPuntoEmision;
	}
	public void setCodPuntoEmision(String codPuntoEmision) {
		this.codPuntoEmision = codPuntoEmision;
	}
	public String getSecuencial() {
		return secuencial;
	}
	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}
	public String getDireccionMatriz() {
		return direccionMatriz;
	}
	public void setDireccionMatriz(String direccionMatriz) {
		this.direccionMatriz = direccionMatriz;
	}
	public String getMailEmpresa() {
		return mailEmpresa;
	}
	public void setMailEmpresa(String mailEmpresa) {
		this.mailEmpresa = mailEmpresa;
	}
	public int getSizeIT() {
		return sizeIT;
	}
	public void setSizeIT(int sizeIT) {
		this.sizeIT = sizeIT;
	}
	public String getFecEmision() {
		return fecEmision;
	}
	public void setFecEmision(String fecEmision) {
		this.fecEmision = fecEmision;
	}
	public String getDireccionEstablecimiento() {
		return direccionEstablecimiento;
	}
	public void setDireccionEstablecimiento(String direccionEstablecimiento) {
		this.direccionEstablecimiento = direccionEstablecimiento;
	}
	
		
	public String getTelefonoEstablecimiento() {
		return telefonoEstablecimiento;
	}
	public void setTelefonoEstablecimiento(String telefonoEstablecimiento) {
		this.telefonoEstablecimiento = telefonoEstablecimiento;
	}
	
	public String getIpEstablecimiento() {
		return IpEstablecimiento;
	}
	public void setIpEstablecimiento(String ipEstablecimiento) {
		IpEstablecimiento = ipEstablecimiento;
	}
	public int getContribEspecial() {
		return contribEspecial;
	}
	public void setContribEspecial(int contribEspecial) {
		this.contribEspecial = contribEspecial;
	}
	public String getObligContabilidad() {
		return obligContabilidad;
	}
	public void setObligContabilidad(String obligContabilidad) {
		this.obligContabilidad = obligContabilidad;
	}
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}
	public String getGuiaRemision() {
		return guiaRemision;
	}
	public void setGuiaRemision(String guiaRemision) {
		this.guiaRemision = guiaRemision;
	}
	public String getRazonSocialComp() {
		return razonSocialComp;
	}
	public void setRazonSocialComp(String razonSocialComp) {
		this.razonSocialComp = razonSocialComp;
	}
	public String getIdentificacionComp() {
		return identificacionComp;
	}
	public void setIdentificacionComp(String identificacionComp) {
		this.identificacionComp = identificacionComp;
	}	
	public String getMailComp() {
		return mailComp;
	}
	public void setMailComp(String mailComp) {
		this.mailComp = mailComp;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getRise() {
		return rise;
	}
	public void setRise(String rise) {
		this.rise = rise;
	}
	public String getCodDocModificado() {
		return codDocModificado;
	}
	public void setCodDocModificado(String codDocModificado) {
		this.codDocModificado = codDocModificado;
	}
	public String getNumDocModificado() {
		return numDocModificado;
	}
	public void setNumDocModificado(String numDocModificado) {
		this.numDocModificado = numDocModificado;
	}
	public String getFecEmisionDoc() {
		return fecEmisionDoc;
	}
	public void setFecEmisionDoc(String fecEmisionDoc) {
		this.fecEmisionDoc = fecEmisionDoc;
	}
	public double getValorModificado() {
		return valorModificado;
	}
	public void setValorModificado(double valorModificado) {
		this.valorModificado = valorModificado;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getPeriodoFiscal() {
		return periodoFiscal;
	}
	public void setPeriodoFiscal(String periodoFiscal) {
		this.periodoFiscal = periodoFiscal;
	}
	public String getDirPartida() {
		return dirPartida;
	}
	public void setDirPartida(String dirPartida) {
		this.dirPartida = dirPartida;
	}
	public String getRazonSocTransp() {
		return razonSocTransp;
	}
	public void setRazonSocTransp(String razonSocTransp) {
		this.razonSocTransp = razonSocTransp;
	}
	public int getTipoIdentTransp() {
		return tipoIdentTransp;
	}
	public void setTipoIdentTransp(int tipoIdentTransp) {
		this.tipoIdentTransp = tipoIdentTransp;
	}
	public String getRucTransp() {
		return rucTransp;
	}
	public void setRucTransp(String rucTransp) {
		this.rucTransp = rucTransp;
	}
	public String getAux1() {
		return aux1;
	}
	public void setAux1(String aux1) {
		this.aux1 = aux1;
	}
	public String getAux2() {
		return aux2;
	}
	public void setAux2(String aux2) {
		this.aux2 = aux2;
	}
	public String getFechaIniTransp() {
		return fechaIniTransp;
	}
	public void setFechaIniTransp(String fechaIniTransp) {
		this.fechaIniTransp = fechaIniTransp;
	}
	public String getFechaFinTransp() {
		return fechaFinTransp;
	}
	public void setFechaFinTransp(String fechaFinTransp) {
		this.fechaFinTransp = fechaFinTransp;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public int getSizeIC() {
		return sizeIC;
	}
	public void setSizeIC(int sizeIC) {
		this.sizeIC = sizeIC;
	}
	public double getSubTotal12() {
		return subTotal12;
	}
	public void setSubTotal12(double subTotal12) {
		this.subTotal12 = subTotal12;
	}
	public double getSubTotal0() {
		return subTotal0;
	}
	public void setSubTotal0(double subTotal0) {
		this.subTotal0 = subTotal0;
	}
	public double getSubTotalNoSujeto() {
		return subTotalNoSujeto;
	}
	public void setSubTotalNoSujeto(double subTotalNoSujeto) {
		this.subTotalNoSujeto = subTotalNoSujeto;
	}
	public double getTotalSinImpuestos() {
		return totalSinImpuestos;
	}
	public void setTotalSinImpuestos(double totalSinImpuestos) {
		this.totalSinImpuestos = totalSinImpuestos;
	}
	public double getTotalDescuento() {
		return totalDescuento;
	}
	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}
	public double getTotalICE() {
		return totalICE;
	}
	public void setTotalICE(double totalICE) {
		this.totalICE = totalICE;
	}
	public double getTotalIva12() {
		return totalIva12;
	}
	public void setTotalIva12(double totalIva12) {
		this.totalIva12 = totalIva12;
	}
	public double getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}
	public double getPropina() {
		return propina;
	}
	public void setPropina(double propina) {
		this.propina = propina;
	}
	public double getImportePagar() {
		return importePagar;
	}
	public void setImportePagar(double importePagar) {
		this.importePagar = importePagar;
	}
	public int getSizeT() {
		return sizeT;
	}
	public void setSizeT(int sizeT) {
		this.sizeT = sizeT;
	}
	/*
	public int getCodImpRetenidos() {
		return codImpRetenidos;
	}
	public void setCodImpRetenidos(int codImpRetenidos) {
		this.codImpRetenidos = codImpRetenidos;
	}
	public String getCodRetencion() {
		return codRetencion;
	}
	public void setCodRetencion(String codRetencion) {
		this.codRetencion = codRetencion;
	}
	public double getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(double baseImponible) {
		this.baseImponible = baseImponible;
	}
	public double getPorcRetenido() {
		return porcRetenido;
	}
	public void setPorcRetenido(double porcRetenido) {
		this.porcRetenido = porcRetenido;
	}
	public double getValorRetenido() {
		return valorRetenido;
	}
	public void setValorRetenido(double valorRetenido) {
		this.valorRetenido = valorRetenido;
	}
	public int getCodDocSustento() {
		return codDocSustento;
	}
	public void setCodDocSustento(int codDocSustento) {
		this.codDocSustento = codDocSustento;
	}
	public long getNumDocSustento() {
		return numDocSustento;
	}
	public void setNumDocSustento(long numDocSustento) {
		this.numDocSustento = numDocSustento;
	}
	public Date getFechaEmisionDocSustento() {
		return fechaEmisionDocSustento;
	}
	public void setFechaEmisionDocSustento(Date fechaEmisionDocSustento) {
		this.fechaEmisionDocSustento = fechaEmisionDocSustento;
	}
	public int getSizeTIR() {
		return sizeTIR;
	}
	public void setSizeTIR(int sizeTIR) {
		this.sizeTIR = sizeTIR;
	}
	
	public int getCodTotalImpuestos() {
		return codTotalImpuestos;
	}
	public void setCodTotalImpuestos(int codTotalImpuestos) {
		this.codTotalImpuestos = codTotalImpuestos;
	}
	public int getCodPorcentImp() {
		return codPorcentImp;
	}
	public void setCodPorcentImp(int codPorcentImp) {
		this.codPorcentImp = codPorcentImp;
	}
	public long getTarifaImp() {
		return tarifaImp;
	}
	public void setTarifaImp(long tarifaImp) {
		this.tarifaImp = tarifaImp;
	}
	public int getBaseImponibleImp() {
		return baseImponibleImp;
	}
	public void setBaseImponibleImp(int baseImponibleImp) {
		this.baseImponibleImp = baseImponibleImp;
	}
	public double getValorImp() {
		return valorImp;
	}
	public void setValorImp(double valorImp) {
		this.valorImp = valorImp;
	}
	public String getImpuestoImp() {
		return impuestoImp;
	}
	public void setImpuestoImp(String impuestoImp) {
		this.impuestoImp = impuestoImp;
	}
	public int getSizeTI() {
		return sizeTI;
	}
	public void setSizeTI(int sizeTI) {
		this.sizeTI = sizeTI;
	}*/
	public String getMotivoRazonND() {
		return motivoRazonND;
	}
	public void setMotivoRazonND(String motivoRazonND) {
		this.motivoRazonND = motivoRazonND;
	}
	public double getMotivoValorND() {
		return motivoValorND;
	}
	public void setMotivoValorND(double motivoValorND) {
		this.motivoValorND = motivoValorND;
	}
	public int getSizeMO() {
		return sizeMO;
	}
	public void setSizeMO(int sizeMO) {
		this.sizeMO = sizeMO;
	}
	
	/*
	public String getIdentDestinatario() {
		return identDestinatario;
	}
	public void setIdentDestinatario(String identDestinatario) {
		this.identDestinatario = identDestinatario;
	}
	public String getRazonSocialDestinatario() {
		return razonSocialDestinatario;
	}
	public void setRazonSocialDestinatario(String razonSocialDestinatario) {
		this.razonSocialDestinatario = razonSocialDestinatario;
	}
	public String getDirDestinatario() {
		return dirDestinatario;
	}
	public void setDirDestinatario(String dirDestinatario) {
		this.dirDestinatario = dirDestinatario;
	}
	public String getMotTraslDestinatario() {
		return motTraslDestinatario;
	}
	public void setMotTraslDestinatario(String motTraslDestinatario) {
		this.motTraslDestinatario = motTraslDestinatario;
	}
	public String getDocAduanero() {
		return docAduanero;
	}
	public void setDocAduanero(String docAduanero) {
		this.docAduanero = docAduanero;
	}
	public int getCodEstabDestino() {
		return codEstabDestino;
	}
	public void setCodEstabDestino(int codEstabDestino) {
		this.codEstabDestino = codEstabDestino;
	}
	public String getRutaDest() {
		return rutaDest;
	}
	public void setRutaDest(String rutaDest) {
		this.rutaDest = rutaDest;
	}
	public int getCodDocSustentoDest() {
		return codDocSustentoDest;
	}
	public void setCodDocSustentoDest(int codDocSustentoDest) {
		this.codDocSustentoDest = codDocSustentoDest;
	}
	public long getNumDocSustentoDest() {
		return numDocSustentoDest;
	}
	public void setNumDocSustentoDest(long numDocSustentoDest) {
		this.numDocSustentoDest = numDocSustentoDest;
	}
	public long getNumAutDocSustDest() {
		return numAutDocSustDest;
	}
	public void setNumAutDocSustDest(long numAutDocSustDest) {
		this.numAutDocSustDest = numAutDocSustDest;
	}
	public Date getFechEmisionDocSustDest() {
		return fechEmisionDocSustDest;
	}
	public void setFechEmisionDocSustDest(Date fechEmisionDocSustDest) {
		this.fechEmisionDocSustDest = fechEmisionDocSustDest;
	}
	public int getSizeDEST() {
		return sizeDEST;
	}
	public void setSizeDEST(int sizeDEST) {
		this.sizeDEST = sizeDEST;
	}	
	*/
	
	//------------------Detalles
	public ArrayList<DetalleDocumento> getListDetDocumentos() {
		return ListDetDocumentos;
	}
	public ArrayList<Destinatarios> getListDetDestinatarios() {
		return ListDetDestinatarios;
	}
	public void setListDetDestinatarios(
			ArrayList<Destinatarios> listDetDestinatarios) {
		ListDetDestinatarios = listDetDestinatarios;
	}
	public void setListDetDocumentos(ArrayList<DetalleDocumento> listDetDocumentos) {
		ListDetDocumentos = listDetDocumentos;
	}
	
	public ArrayList<DetalleTotalImpuestos> getListDetDetImpuestos() {
		return ListDetDetImpuestos;
	}
	public void setListDetDetImpuestos(
			ArrayList<DetalleTotalImpuestos> listDetDetImpuestos) {
		ListDetDetImpuestos = listDetDetImpuestos;
	}
	public ArrayList<DetalleTotalImpuestosRetenciones> getListDetDetImpuestosRetenciones() {
		return ListDetDetImpuestosRetenciones;
	}
	public void setListDetDetImpuestosRetenciones(
			ArrayList<DetalleTotalImpuestosRetenciones> listDetDetImpuestosRetenciones) {
		ListDetDetImpuestosRetenciones = listDetDetImpuestosRetenciones;
	}
		
	//---------------------Informacion Adicional
	public static String get_pathGenerados() {
		return _pathGenerados;
	}
	public static void set_pathGenerados(String _pathGenerados) {
		InformacionTributaria._pathGenerados = _pathGenerados;
	}
	public static String get_pathFirmados() {
		return _pathFirmados;
	}
	public static void set_pathFirmados(String _pathFirmados) {
		InformacionTributaria._pathFirmados = _pathFirmados;
	}
	public static String get_pathInfoRecibida() {
		return _pathInfoRecibida;
	}
	public static void set_pathInfoRecibida(String _pathInfoRecibida) {
		InformacionTributaria._pathInfoRecibida = _pathInfoRecibida;
	}
	public ArrayList<InformacionAdicional> getListInfAdicional() {
		return ListInfAdicional;
	}
	public void setListInfAdicional(ArrayList<InformacionAdicional> listInfAdicional) {
		ListInfAdicional = listInfAdicional;
	}
	public static String get_pathAutorizados() {
		return _pathAutorizados;
	}
	public static void set_pathAutorizados(String _pathAutorizados) {
		InformacionTributaria._pathAutorizados = _pathAutorizados;
	}
	public static String get_pathNoAutorizados() {
		return _pathNoAutorizados;
	}
	public static void set_pathNoAutorizados(String _pathNoAutorizados) {
		InformacionTributaria._pathNoAutorizados = _pathNoAutorizados;
	}
	public ArrayList<InformacionAdicional> getListInfMotivos() {
		return ListInfMotivos;
	}
	public void setListInfMotivos(ArrayList<InformacionAdicional> listInfMotivos) {
		ListInfMotivos = listInfMotivos;
	}
	public static String get_PathCompContingencia() {
		return _PathCompContingencia;
	}
	public static void set_PathCompContingencia(String _PathCompContingencia) {
		InformacionTributaria._PathCompContingencia = _PathCompContingencia;
	}
	public String getAmbientePuntoEmision() {
		return ambientePuntoEmision;
	}
	public void setAmbientePuntoEmision(String ambientePuntoEmision) {
		this.ambientePuntoEmision = ambientePuntoEmision;
	}
	public String getMailEstablecimiento() {
		return mailEstablecimiento;
	}
	public void setMailEstablecimiento(String mailEstablecimiento) {
		this.mailEstablecimiento = mailEstablecimiento;
	}
	public String getPathAnexoEstablecimiento() {
		return pathAnexoEstablecimiento;
	}
	public void setPathAnexoEstablecimiento(String pathAnexoEstablecimiento) {
		this.pathAnexoEstablecimiento = pathAnexoEstablecimiento;
	}
	public String getMensajeEstablecimiento() {
		return mensajeEstablecimiento;
	}
	public void setMensajeEstablecimiento(String mensajeEstablecimiento) {
		this.mensajeEstablecimiento = mensajeEstablecimiento;
	}
	
	
	public String getFechaAutorizacion() {
		return fechaAutorizacion;
	}
	public void setFechaAutorizacion(String fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}
	public String getNumeroAutorizacion() {
		return NumeroAutorizacion;
	}
	public void setNumeroAutorizacion(String numeroAutorizacion) {
		NumeroAutorizacion = numeroAutorizacion;
	}
	public static String get_ClaveFirma() {
		return _ClaveFirma;
	}
	public static void set_ClaveFirma(String claveFirma) {
		_ClaveFirma = claveFirma;
	}
	public static String get_PathFirma() {
		return _PathFirma;
	}
	public static void set_PathFirma(String pathFirma) {
		_PathFirma = pathFirma;
	}
	public static String get_TipoFirma() {
		return _TipoFirma;
	}
	public static void set_TipoFirma(String tipoFirma) {
		_TipoFirma = tipoFirma;
	}
	public static String get_pathXsd() {
		return _pathXsd;
	}
	public static void set_pathXsd(String _pathXsd) {
		InformacionTributaria._pathXsd = _pathXsd;
	}
	public static String get_pathJasper() {
		return _pathJasper;
	}
	public static void set_pathJasper(String _pathJasper) {
		InformacionTributaria._pathJasper = _pathJasper;
	}
	public String getRucFirmante() {
		return rucFirmante;
	}
	public void setRucFirmante(String rucFirmante) {
		this.rucFirmante = rucFirmante;
	}
	public ArrayList<DetalleImpuestosRetenciones> getListDetImpuestosRetenciones() {
		return ListDetImpuestosRetenciones;
	}
	public void setListDetImpuestosRetenciones(
			ArrayList<DetalleImpuestosRetenciones> listDetImpuestosRetenciones) {
		ListDetImpuestosRetenciones = listDetImpuestosRetenciones;
	}
	public String getCodCliente() {
		return codCliente;
	}
	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmailCliente() {
		return emailCliente;
	}
	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getCaja() {
		return caja;
	}
	public void setCaja(String caja) {
		this.caja = caja;
	}
	public String getIdMovimiento() {
		return idMovimiento;
	}
	public void setIdMovimiento(String idMovimiento) {
		this.idMovimiento = idMovimiento;
	}
	public Date getFechaAutorizado() {
		return fechaAutorizado;
	}
	public void setFechaAutorizado(Date fechaAutorizado) {
		this.fechaAutorizado = fechaAutorizado;
	}

	
}
