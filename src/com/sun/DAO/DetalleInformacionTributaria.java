package com.sun.DAO;

public class DetalleInformacionTributaria {
	    //DE	//Detalle
		private String codPrincipalDet;
		private String codAuxiliarDet;
		private String descripcionDet;
		private double cantidadDet;
		private double precioUnitario;
		private double descuento;
		private double precioTotalSinImp;
		private int sizeDE = 7;
		
		//IM	//Detalle
		private int impCod;
		private double impCodPorc;
		private double impTarifa;
		private int impBaseImp;
		private double impValor;
		private int sizeIM = 5;
		
		//DA	//Detalle		
		//No se encuentra en documentacion.
		
		//----------------------------------------GET AND SETTER		
		public String getCodPrincipalDet() {
			return codPrincipalDet;
		}
		public void setCodPrincipalDet(String codPrincipalDet) {
			this.codPrincipalDet = codPrincipalDet;
		}
		public String getCodAuxiliarDet() {
			return codAuxiliarDet;
		}
		public void setCodAuxiliarDet(String codAuxiliarDet) {
			this.codAuxiliarDet = codAuxiliarDet;
		}
		public String getDescripcionDet() {
			return descripcionDet;
		}
		public void setDescripcionDet(String descripcionDet) {
			this.descripcionDet = descripcionDet;
		}
		public double getCantidadDet() {
			return cantidadDet;
		}
		public void setCantidadDet(double cantidadDet) {
			this.cantidadDet = cantidadDet;
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
		public double getPrecioTotalSinImp() {
			return precioTotalSinImp;
		}
		public void setPrecioTotalSinImp(double precioTotalSinImp) {
			this.precioTotalSinImp = precioTotalSinImp;
		}
}
