package com.sun.DAO;

public class DocumentoImpuestos {

	private int impuestoCodigo;
	private int impuestoCodigoPorcentaje;
	private double impuestoTarifa;
	private  double impuestoBaseImponible;
	private double impuestoValor;
	private int lineaFactura;
	
	public int getImpuestoCodigo() {
		return impuestoCodigo;
	}
	public void setImpuestoCodigo(int impuestoCodigo) {
		this.impuestoCodigo = impuestoCodigo;
	}
	public int getImpuestoCodigoPorcentaje() {
		return impuestoCodigoPorcentaje;
	}
	public void setImpuestoCodigoPorcentaje(int impuestoCodigoPorcentaje) {
		this.impuestoCodigoPorcentaje = impuestoCodigoPorcentaje;
	}
	public double getImpuestoTarifa() {
		return impuestoTarifa;
	}
	public void setImpuestoTarifa(double impuestoTarifa) {
		this.impuestoTarifa = impuestoTarifa;
	}
	public double getImpuestoBaseImponible() {
		return impuestoBaseImponible;
	}
	public void setImpuestoBaseImponible(double impuestoBaseImponible) {
		this.impuestoBaseImponible = impuestoBaseImponible;
	}
	public double getImpuestoValor() {
		return impuestoValor;
	}
	public void setImpuestoValor(double impuestoValor) {
		this.impuestoValor = impuestoValor;
	}
	public int getLineaFactura() {
		return lineaFactura;
	}
	public void setLineaFactura(int lineaFactura) {
		this.lineaFactura = lineaFactura;
	}
}
