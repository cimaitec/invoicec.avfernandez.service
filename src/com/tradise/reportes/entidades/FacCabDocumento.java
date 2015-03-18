 package com.tradise.reportes.entidades;
 
 import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.DAO.DetalleImpuestosRetenciones;
import com.sun.DAO.ImpuestosRetencion;
import com.sun.DAO.InformacionAdicional;
 
 public class FacCabDocumento
 {
   private Integer ambiente;
   private String autorizacion;
   private String claveAcceso;
   private String codDocModificado;
   private String codDocSustento;
   private String codEstablecimientoDest;
   private String direccionDestinatario;
   private String docAduaneroUnico;
   private String email;
   private String fecEmisionDocSustento;

   private String fechaautorizacion;
   private String fechaEmision;
   private Date fechaEmisionDocSustento;
   private String fechaFinTransporte;
   private String fechaInicioTransporte;
   private String guiaRemision;
   private String identificacionComprador;
   private String identificacionDestinatario;
   private double importeTotal;
   private String infoAdicional;
   private String isActive;
   private double iva12;
   private String moneda;
   private String motivoRazon;
   private String motivoTraslado;
   private double motivoValor;
   private String numAutDocSustento;
   private String numDocModificado;
   private String numDocSustento;
   private String partida;
   private String periodoFiscal;
   private String placa;
   private double propina;
   private String razonSocialComprador;
   private String razonSocialDestinatario;
   private String rise;
   private String ruta;
   private double subtotal0;
   private double subtotal12;
   private double subtotalNoIva;
   private String tipIdentificacionComprador;
   private String tipoEmision;
   private String tipoIdentificacion;
   private double totalDescuento;
   private double totalSinImpuesto;
   private double totalvalorICE;
   private String ruc;
   private String codEstablecimiento;
   private String codPuntEmision;
   private String secuencial;
   private String codigoDocumento;
   private String ESTADO_TRANSACCION;
   private String MSJ_ERROR;
   private String dirEstablecimiento;
   private String obligadoContabilidad;
   private List<FacDetDocumento> listDetalleDocumento = null;
   private List<DetalleImpuestosRetenciones> listImpuestosRetencion = null;
   private ArrayList<InformacionAdicional> ListInfAdicional = null;
   private String codCliente;
   private String direccion;
   private String telefono;
   private String emailCliente;
   
   public String getRuc()
   {
     return this.ruc;
   }
   public void setRuc(String ruc) {
     this.ruc = ruc;
   }
   public String getCodEstablecimiento() {
     return this.codEstablecimiento;
   }
   public void setCodEstablecimiento(String codEstablecimiento) {
     this.codEstablecimiento = codEstablecimiento;
   }
   public String getCodPuntEmision() {
     return this.codPuntEmision;
   }
   public void setCodPuntEmision(String codPuntEmision) {
     this.codPuntEmision = codPuntEmision;
   }
   public String getSecuencial() {
     return this.secuencial;
   }
   public void setSecuencial(String secuencial) {
     this.secuencial = secuencial;
   }
   public String getCodigoDocumento() {
     return this.codigoDocumento;
   }
   public void setCodigoDocumento(String codigoDocumento) {
     this.codigoDocumento = codigoDocumento;
   }
 
   public Integer getAmbiente() {
     return this.ambiente;
   }
 
   public void setAmbiente(Integer ambiente) {
     this.ambiente = ambiente;
   }
 
   public String getAutorizacion() {
     return this.autorizacion;
   }
 
   public void setAutorizacion(String autorizacion) {
     this.autorizacion = autorizacion;
   }
 
   public String getClaveAcceso() {
     return this.claveAcceso;
   }
 
   public void setClaveAcceso(String claveAcceso) {
     this.claveAcceso = claveAcceso;
   }
 
   public String getCodDocModificado() {
     return this.codDocModificado;
   }
 
   public void setCodDocModificado(String codDocModificado) {
     this.codDocModificado = codDocModificado;
   }
 
   public String getCodDocSustento() {
     return this.codDocSustento;
   }
 
   public void setCodDocSustento(String codDocSustento) {
     this.codDocSustento = codDocSustento;
   }
 
   public String getCodEstablecimientoDest() {
     return this.codEstablecimientoDest;
   }
 
   public void setCodEstablecimientoDest(String codEstablecimientoDest) {
     this.codEstablecimientoDest = codEstablecimientoDest;
   }
 
   public String getDireccionDestinatario() {
     return this.direccionDestinatario;
   }
 
   public void setDireccionDestinatario(String direccionDestinatario) {
     this.direccionDestinatario = direccionDestinatario;
   }
 
   public String getDocAduaneroUnico() {
     return this.docAduaneroUnico;
   }
 
   public void setDocAduaneroUnico(String docAduaneroUnico) {
     this.docAduaneroUnico = docAduaneroUnico;
   }
 
   public String getEmail() {
     return this.email;
   }
 
   public void setEmail(String email) {
     this.email = email;
   }
 
   public String getFecEmisionDocSustento() {
     return this.fecEmisionDocSustento;
   }
 
   public void setFecEmisionDocSustento(String fecEmisionDocSustento) {
     this.fecEmisionDocSustento = fecEmisionDocSustento;
   }
 
   public String getFechaautorizacion() {
     return this.fechaautorizacion;
   }
 
   public void setFechaautorizacion(String fechaautorizacion) {
     this.fechaautorizacion = fechaautorizacion;
   }
 
   public String getFechaEmision() {
     return this.fechaEmision;
   }
 
   public void setFechaEmision(String fechaEmision) {
     this.fechaEmision = fechaEmision;
   }
 
   public Date getFechaEmisionDocSustento() {
     return this.fechaEmisionDocSustento;
   }
 
   public void setFechaEmisionDocSustento(Date fechaEmisionDocSustento) {
     this.fechaEmisionDocSustento = fechaEmisionDocSustento;
   }
 
   public String getFechaFinTransporte() {
     return this.fechaFinTransporte;
   }
 
   public void setFechaFinTransporte(String fechaFinTransporte) {
     this.fechaFinTransporte = fechaFinTransporte;
   }
 
   public String getFechaInicioTransporte() {
     return this.fechaInicioTransporte;
   }
 
   public void setFechaInicioTransporte(String fechaInicioTransporte) {
     this.fechaInicioTransporte = fechaInicioTransporte;
   }
 
   public String getGuiaRemision() {
     return this.guiaRemision;
   }
 
   public void setGuiaRemision(String guiaRemision) {
     this.guiaRemision = guiaRemision;
   }
 
   public String getIdentificacionComprador() {
     return this.identificacionComprador;
   }
 
   public void setIdentificacionComprador(String identificacionComprador) {
     this.identificacionComprador = identificacionComprador;
   }
 
   public String getIdentificacionDestinatario() {
     return this.identificacionDestinatario;
   }
 
   public void setIdentificacionDestinatario(String identificacionDestinatario) {
     this.identificacionDestinatario = identificacionDestinatario;
   }
 
   public double getImporteTotal() {
     return this.importeTotal;
   }
 
   public void setImporteTotal(double importeTotal) {
     this.importeTotal = importeTotal;
   }
 
   public String getInfoAdicional() {
     return this.infoAdicional;
   }
 
   public void setInfoAdicional(String infoAdicional) {
     this.infoAdicional = infoAdicional;
   }
 
   public String getIsActive() {
     return this.isActive;
   }
 
   public void setIsActive(String isActive) {
     this.isActive = isActive;
   }
 
   public double getIva12() {
     return this.iva12;
   }
 
   public void setIva12(double iva12) {
     this.iva12 = iva12;
   }
 
   public String getMoneda() {
     return this.moneda;
   }
 
   public void setMoneda(String moneda) {
     this.moneda = moneda;
   }
 
   public String getMotivoRazon() {
     return this.motivoRazon;
   }
 
   public void setMotivoRazon(String motivoRazon) {
     this.motivoRazon = motivoRazon;
   }
 
   public String getMotivoTraslado() {
     return this.motivoTraslado;
   }
 
   public void setMotivoTraslado(String motivoTraslado) {
     this.motivoTraslado = motivoTraslado;
   }
 
   public double getMotivoValor() {
     return this.motivoValor;
   }
 
   public void setMotivoValor(double motivoValor) {
     this.motivoValor = motivoValor;
   }
 
   public String getNumAutDocSustento() {
     return this.numAutDocSustento;
   }
 
   public void setNumAutDocSustento(String numAutDocSustento) {
     this.numAutDocSustento = numAutDocSustento;
   }
 
   public String getNumDocModificado() {
     return this.numDocModificado;
   }
 
   public void setNumDocModificado(String numDocModificado) {
     this.numDocModificado = numDocModificado;
   }
 
   public String getNumDocSustento() {
     return this.numDocSustento;
   }
 
   public void setNumDocSustento(String numDocSustento) {
     this.numDocSustento = numDocSustento;
   }
 
   public String getPartida() {
     return this.partida;
   }
 
   public void setPartida(String partida) {
     this.partida = partida;
   }
 
   public String getPeriodoFiscal() {
     return this.periodoFiscal;
   }
 
   public void setPeriodoFiscal(String periodoFiscal) {
     this.periodoFiscal = periodoFiscal;
   }
 
   public String getPlaca() {
     return this.placa;
   }
 
   public void setPlaca(String placa) {
     this.placa = placa;
   }
 
   public double getPropina() {
     return this.propina;
   }
 
   public void setPropina(double propina) {
     this.propina = propina;
   }
 
   public String getRazonSocialComprador() {
     return this.razonSocialComprador;
   }
 
   public void setRazonSocialComprador(String razonSocialComprador) {
     this.razonSocialComprador = razonSocialComprador;
   }
 
   public String getRazonSocialDestinatario() {
     return this.razonSocialDestinatario;
   }
 
   public void setRazonSocialDestinatario(String razonSocialDestinatario) {
     this.razonSocialDestinatario = razonSocialDestinatario;
   }
 
   public String getRise() {
     return this.rise;
   }
 
   public void setRise(String rise) {
     this.rise = rise;
   }
 
   public String getRuta() {
     return this.ruta;
   }
 
   public void setRuta(String ruta) {
     this.ruta = ruta;
   }
 
   public double getSubtotal0() {
     return this.subtotal0;
   }
 
   public void setSubtotal0(double subtotal0) {
     this.subtotal0 = subtotal0;
   }
 
   public double getSubtotal12() {
     return this.subtotal12;
   }
 
   public void setSubtotal12(double subtotal12) {
     this.subtotal12 = subtotal12;
   }
 
   public double getSubtotalNoIva() {
     return this.subtotalNoIva;
   }
 
   public void setSubtotalNoIva(double subtotalNoIva) {
     this.subtotalNoIva = subtotalNoIva;
   }
 
   public String getTipIdentificacionComprador() {
     return this.tipIdentificacionComprador;
   }
 
   public void setTipIdentificacionComprador(String tipIdentificacionComprador) {
     this.tipIdentificacionComprador = tipIdentificacionComprador;
   }
 
   public String getTipoEmision() {
     return this.tipoEmision;
   }
 
   public void setTipoEmision(String tipoEmision) {
     this.tipoEmision = tipoEmision;
   }
 
   public String getTipoIdentificacion() {
     return this.tipoIdentificacion;
   }
 
   public void setTipoIdentificacion(String tipoIdentificacion) {
     this.tipoIdentificacion = tipoIdentificacion;
   }
 
   public double getTotalDescuento() {
     return this.totalDescuento;
   }
 
   public void setTotalDescuento(double totalDescuento) {
     this.totalDescuento = totalDescuento;
   }
 
   public double getTotalSinImpuesto() {
     return this.totalSinImpuesto;
   }
 
   public void setTotalSinImpuesto(double totalSinImpuesto) {
     this.totalSinImpuesto = totalSinImpuesto;
   }
 
   public double getTotalvalorICE() {
     return this.totalvalorICE;
   }
 
   public void setTotalvalorICE(double totalvalorICE) {
     this.totalvalorICE = totalvalorICE;
   }
 
   public String getESTADO_TRANSACCION() {
     return this.ESTADO_TRANSACCION;
   }
 
   public void setESTADO_TRANSACCION(String eSTADO_TRANSACCION) {
     this.ESTADO_TRANSACCION = eSTADO_TRANSACCION;
   }
 
   public String getMSJ_ERROR() {
     return this.MSJ_ERROR;
   }
 
   public void setMSJ_ERROR(String mSJ_ERROR) {
     this.MSJ_ERROR = mSJ_ERROR;
   }
   public List<FacDetDocumento> getListDetalleDocumento() {
	 return listDetalleDocumento;
   }
   public void setListDetalleDocumento(List<FacDetDocumento> listDetalleDocumento) {
	 this.listDetalleDocumento = listDetalleDocumento;
   }
public String getDirEstablecimiento() {
	return dirEstablecimiento;
}
public void setDirEstablecimiento(String dirEstablecimiento) {
	this.dirEstablecimiento = dirEstablecimiento;
}
public String getObligadoContabilidad() {
	return obligadoContabilidad;
}
public void setObligadoContabilidad(String obligadoContabilidad) {
	this.obligadoContabilidad = obligadoContabilidad;
}
public List<DetalleImpuestosRetenciones> getListImpuestosRetencion() {
	return listImpuestosRetencion;
}
public void setListImpuestosRetencion(
		List<DetalleImpuestosRetenciones> listImpuestosRetencion) {
	this.listImpuestosRetencion = listImpuestosRetencion;
}
public ArrayList<InformacionAdicional> getListInfAdicional() {
	return ListInfAdicional;
}
public void setListInfAdicional(ArrayList<InformacionAdicional> listInfAdicional) {
	ListInfAdicional = listInfAdicional;
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
   
}