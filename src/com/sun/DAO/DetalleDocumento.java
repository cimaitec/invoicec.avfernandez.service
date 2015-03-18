package com.sun.DAO;

import java.util.ArrayList;

public class DetalleDocumento {
			
	private String codigoPrincipal;
	private String codigoAuxiliar;
	private String descripcion;
	private double cantidad;
	private double precioUnitario;
	private double descuento;
	private double precioTotalSinImpuesto;
	private int lineaFactura;
	private String identificacionDestinatario; 
	
	private ArrayList<DocumentoImpuestos> ListDetImpuestosDocumentos;	
	
	private ArrayList<DetallesAdicionales> ListDetAdicionalesDocumentos;	
	
	public String getCodigoPrincipal() {
		return codigoPrincipal;
	}
	public void setCodigoPrincipal(String codigoPrincipal) {
		this.codigoPrincipal = codigoPrincipal;
	}
	public String getCodigoAuxiliar() {
		return codigoAuxiliar;
	}
	public void setCodigoAuxiliar(String codigoAuxiliar) {
		this.codigoAuxiliar = codigoAuxiliar;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public double getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	public double getPrecioTotalSinImpuesto() {
		return precioTotalSinImpuesto;
	}
	public void setPrecioTotalSinImpuesto(double precioTotalSinImpuesto) {
		this.precioTotalSinImpuesto = precioTotalSinImpuesto;
	}
	public ArrayList<DocumentoImpuestos> getListDetImpuestosDocumentos() {
		return ListDetImpuestosDocumentos;
	}
	public void setListDetImpuestosDocumentos(
			ArrayList<DocumentoImpuestos> listDetImpuestosDocumentos) {
		ListDetImpuestosDocumentos = listDetImpuestosDocumentos;
	}
	public ArrayList<DetallesAdicionales> getListDetAdicionalesDocumentos() {
		return ListDetAdicionalesDocumentos;
	}
	public void setListDetAdicionalesDocumentos(
			ArrayList<DetallesAdicionales> listDetAdicionalesDocumentos) {
		ListDetAdicionalesDocumentos = listDetAdicionalesDocumentos;
	}
	public int getLineaFactura() {
		return lineaFactura;
	}
	public void setLineaFactura(int lineaFactura) {
		this.lineaFactura = lineaFactura;
	}
	public String getIdentificacionDestinatario() {
		return identificacionDestinatario;
	}
	public void setIdentificacionDestinatario(String identificacionDestinatario) {
		this.identificacionDestinatario = identificacionDestinatario;
	}
	
}
