/*     */ package com.tradise.reportes.entidades;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class FacProducto
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Integer codPrincipal;
/*     */   private String atributo1;
/*     */   private String atributo2;
/*     */   private String atributo3;
/*     */   private Integer codAuxiliar;
/*     */   private Integer codIce;
/*     */   private String descripcion;
/*     */   private Integer tipoIva;
/*     */   private String tipoProducto;
/*     */   private String valor1;
/*     */   private String valor2;
/*     */   private String valor3;
/*     */   private double valorUnitario;
/*     */ 
/*     */   public Integer getCodPrincipal()
/*     */   {
/*  33 */     return this.codPrincipal;
/*     */   }
/*     */ 
/*     */   public void setCodPrincipal(Integer codPrincipal) {
/*  37 */     this.codPrincipal = codPrincipal;
/*     */   }
/*     */ 
/*     */   public String getAtributo1() {
/*  41 */     return this.atributo1;
/*     */   }
/*     */ 
/*     */   public void setAtributo1(String atributo1) {
/*  45 */     this.atributo1 = atributo1;
/*     */   }
/*     */ 
/*     */   public String getAtributo2() {
/*  49 */     return this.atributo2;
/*     */   }
/*     */ 
/*     */   public void setAtributo2(String atributo2) {
/*  53 */     this.atributo2 = atributo2;
/*     */   }
/*     */ 
/*     */   public String getAtributo3() {
/*  57 */     return this.atributo3;
/*     */   }
/*     */ 
/*     */   public void setAtributo3(String atributo3) {
/*  61 */     this.atributo3 = atributo3;
/*     */   }
/*     */ 
/*     */   public Integer getCodAuxiliar() {
/*  65 */     return this.codAuxiliar;
/*     */   }
/*     */ 
/*     */   public void setCodAuxiliar(Integer codAuxiliar) {
/*  69 */     this.codAuxiliar = codAuxiliar;
/*     */   }
/*     */ 
/*     */   public Integer getCodIce() {
/*  73 */     return this.codIce;
/*     */   }
/*     */ 
/*     */   public void setCodIce(Integer codIce) {
/*  77 */     this.codIce = codIce;
/*     */   }
/*     */ 
/*     */   public String getDescripcion() {
/*  81 */     return this.descripcion;
/*     */   }
/*     */ 
/*     */   public void setDescripcion(String descripcion) {
/*  85 */     this.descripcion = descripcion;
/*     */   }
/*     */ 
/*     */   public Integer getTipoIva() {
/*  89 */     return this.tipoIva;
/*     */   }
/*     */ 
/*     */   public void setTipoIva(Integer tipoIva) {
/*  93 */     this.tipoIva = tipoIva;
/*     */   }
/*     */ 
/*     */   public String getTipoProducto() {
/*  97 */     return this.tipoProducto;
/*     */   }
/*     */ 
/*     */   public void setTipoProducto(String tipoProducto) {
/* 101 */     this.tipoProducto = tipoProducto;
/*     */   }
/*     */ 
/*     */   public String getValor1() {
/* 105 */     return this.valor1;
/*     */   }
/*     */ 
/*     */   public void setValor1(String valor1) {
/* 109 */     this.valor1 = valor1;
/*     */   }
/*     */ 
/*     */   public String getValor2() {
/* 113 */     return this.valor2;
/*     */   }
/*     */ 
/*     */   public void setValor2(String valor2) {
/* 117 */     this.valor2 = valor2;
/*     */   }
/*     */ 
/*     */   public String getValor3() {
/* 121 */     return this.valor3;
/*     */   }
/*     */ 
/*     */   public void setValor3(String valor3) {
/* 125 */     this.valor3 = valor3;
/*     */   }
/*     */ 
/*     */   public double getValorUnitario() {
/* 129 */     return this.valorUnitario;
/*     */   }
/*     */ 
/*     */   public void setValorUnitario(double valorUnitario) {
/* 133 */     this.valorUnitario = valorUnitario;
/*     */   }
/*     */ }

/* Location:           C:\resources\reportes\printReportFacturacion.jar
 * Qualified Name:     cimait.entidades.FacProducto
 * JD-Core Version:    0.6.2
 */