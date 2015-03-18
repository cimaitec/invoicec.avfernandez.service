/*     */ package com.tradise.reportes.entidades;
/*     */ 
/*     */ public class FacDetDocumento
/*     */ {
			//VPI SE CAMBIA TIPO VARIABLE
/*     */   //private Integer cantidad;
			private double cantidad;
/*     */   private String codAuxiliar;
/*     */   private String codPrincipal;
/*     */   private String descripcion;
/*     */   private double descuento;
/*     */   private double precioTotalSinImpuesto;
/*     */   private double precioUnitario;
/*     */   private double valorIce;
/*     */   private String ruc;
/*     */   private String codEstablecimiento;
/*     */   private String codPuntEmision;
/*     */   private String secuencial;
/*     */   private String codigoDocumento;
/*     */   private Integer secuencialDetalle;
			private int ambiente;
/*     */ 
/*     */   public String getRuc()
/*     */   {
/*  24 */     return this.ruc;
/*     */   }
/*     */   public void setRuc(String ruc) {
/*  27 */     this.ruc = ruc;
/*     */   }
/*     */   public String getCodEstablecimiento() {
/*  30 */     return this.codEstablecimiento;
/*     */   }
/*     */   public void setCodEstablecimiento(String codEstablecimiento) {
/*  33 */     this.codEstablecimiento = codEstablecimiento;
/*     */   }
/*     */   public String getCodPuntEmision() {
/*  36 */     return this.codPuntEmision;
/*     */   }
/*     */   public void setCodPuntEmision(String codPuntEmision) {
/*  39 */     this.codPuntEmision = codPuntEmision;
/*     */   }
/*     */   public String getSecuencial() {
/*  42 */     return this.secuencial;
/*     */   }
/*     */   public void setSecuencial(String secuencial) {
/*  45 */     this.secuencial = secuencial;
/*     */   }
/*     */   public String getCodigoDocumento() {
/*  48 */     return this.codigoDocumento;
/*     */   }
/*     */   public void setCodigoDocumento(String codigoDocumento) {
/*  51 */     this.codigoDocumento = codigoDocumento;
/*     */   }
/*     */   public Integer getSecuencialDetalle() {
/*  54 */     return this.secuencialDetalle;
/*     */   }
/*     */   public void setSecuencialDetalle(Integer secuencialDetalle) {
/*  57 */     this.secuencialDetalle = secuencialDetalle;
/*     */   }
/*     */   public double getCantidad() {
/*  60 */     return this.cantidad;
/*     */   }
/*     */   public void setCantidad(double cantidad) {
/*  63 */     this.cantidad = cantidad;
/*     */   }
/*     */   public String getCodAuxiliar() {
/*  66 */     return this.codAuxiliar;
/*     */   }
/*     */   public void setCodAuxiliar(String codAuxiliar) {
/*  69 */     this.codAuxiliar = codAuxiliar;
/*     */   }
/*     */   public String getCodPrincipal() {
/*  72 */     return this.codPrincipal;
/*     */   }
/*     */   public void setCodPrincipal(String codPrincipal) {
/*  75 */     this.codPrincipal = codPrincipal;
/*     */   }
/*     */   public String getDescripcion() {
/*  78 */     return this.descripcion;
/*     */   }
/*     */   public void setDescripcion(String descripcion) {
/*  81 */     this.descripcion = descripcion;
/*     */   }
/*     */   public double getDescuento() {
/*  84 */     return this.descuento;
/*     */   }
/*     */ 
/*     */   public void setDescuento(double descuento) {
/*  88 */     this.descuento = descuento;
/*     */   }
/*     */ 
/*     */   public double getPrecioTotalSinImpuesto() {
/*  92 */     return this.precioTotalSinImpuesto;
/*     */   }
/*     */ 
/*     */   public void setPrecioTotalSinImpuesto(double precioTotalSinImpuesto) {
/*  96 */     this.precioTotalSinImpuesto = precioTotalSinImpuesto;
/*     */   }
/*     */ 
/*     */   public double getPrecioUnitario() {
/* 100 */     return this.precioUnitario;
/*     */   }
/*     */ 
/*     */   public void setPrecioUnitario(double precioUnitario) {
/* 104 */     this.precioUnitario = precioUnitario;
/*     */   }
/*     */ 
/*     */   public double getValorIce() {
/* 108 */     return this.valorIce;
/*     */   }
/*     */ 
/*     */   public void setValorIce(double valorIce) {
/* 112 */     this.valorIce = valorIce;
/*     */   }
public int getAmbiente() {
	return ambiente;
}
public void setAmbiente(int ambiente) {
	this.ambiente = ambiente;
}
			
/*     */ }

/* Location:           C:\resources\reportes\printReportFacturacion.jar
 * Qualified Name:     cimait.entidades.FacDetDocumento
 * JD-Core Version:    0.6.2
 */