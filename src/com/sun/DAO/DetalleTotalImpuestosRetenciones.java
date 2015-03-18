package com.sun.DAO;

import java.util.Date;

public class DetalleTotalImpuestosRetenciones {

	private int codigo;
	private String codigoRetencion;
	private double baseImponible;
	private double porcentajeRetener;
	private double valorRetenido;
	private int codDocSustento;
	private long numDocSustento;
	private Date fechaEmisionDocSustento;
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
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
	public double getPorcentajeRetener() {
		return porcentajeRetener;
	}
	public void setPorcentajeRetener(double porcentajeRetener) {
		this.porcentajeRetener = porcentajeRetener;
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
}
