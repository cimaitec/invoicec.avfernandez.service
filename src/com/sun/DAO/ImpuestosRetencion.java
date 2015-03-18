package com.sun.DAO;

public class ImpuestosRetencion {
	
	private int codigo;
	private int codigoRetencion;	
	private double baseImponible;
	private int porcentajeRetener;
	private double valorRetenido;
	private String codDocSustento;
	private String numDocSustento;
	
	
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public int getCodigoRetencion() {
		return codigoRetencion;
	}
	public void setCodigoRetencion(int codigoRetencion) {
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
}
