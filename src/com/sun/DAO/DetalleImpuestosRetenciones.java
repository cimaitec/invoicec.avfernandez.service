package com.sun.DAO;

public class DetalleImpuestosRetenciones {
	
	private String codigo;
	private String codigoRetencion;
	private double baseImponible;
	private int porcentajeRetener;
	private double valorRetenido;
	private String codDocSustento;
	private String numDocSustento;
	private String fechaEmisionDocSustento;

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigoRetencion() {
		return codigoRetencion;
	}
	public void setCodigoRetencion(String codigoRetencion) {
		this.codigoRetencion = codigoRetencion;
	}
	public double getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(double baseImponible) {
		this.baseImponible = baseImponible;
	}
	public int getPorcentajeRetener() {
		return porcentajeRetener;
	}
	public void setPorcentajeRetener(int porcentajeRetener) {
		this.porcentajeRetener = porcentajeRetener;
	}
	public double getValorRetenido() {
		return valorRetenido;
	}
	public void setValorRetenido(double valorRetenido) {
		this.valorRetenido = valorRetenido;
	}
	public String getCodDocSustento() {
		return codDocSustento;
	}
	public void setCodDocSustento(String codDocSustento) {
		this.codDocSustento = codDocSustento;
	}
	public String getNumDocSustento() {
		return numDocSustento;
	}
	public void setNumDocSustento(String numDocSustento) {
		this.numDocSustento = numDocSustento;
	}
	public String getFechaEmisionDocSustento() {
		return fechaEmisionDocSustento;
	}
	public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
		this.fechaEmisionDocSustento = fechaEmisionDocSustento;
	}	
}
