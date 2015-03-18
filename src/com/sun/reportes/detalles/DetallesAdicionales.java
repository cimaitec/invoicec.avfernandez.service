 package com.sun.reportes.detalles; 
 import java.util.List; 
 public class DetallesAdicionales
 {
   private String Comprobante;
   private String nombreComprobante;
   private String numeroComprobante;
   private String fechaEmision;
   private String fechaEmisionCcompModificado;
   private String peridoFiscal;
   private String baseImponible;
   private String nombreImpuesto;
   private String porcentajeRetencion;
   private String porcentajeRetener;
   private String precioUnitario;
   private String valorRetenido;
   private String razonModificacion;
   private String valorModificacion;
   
   private List<InfoAdicional> infoAdicional;
 
   public String getRazonModificacion()
   {
     return this.razonModificacion;
   }
   public void setRazonModificacion(String razonModificacion) {
     this.razonModificacion = razonModificacion;
   }
   public String getValorModificacion() {
     return this.valorModificacion;
   }
   public void setValorModificacion(String valorModificacion) {
     this.valorModificacion = valorModificacion;
   }
   public String getPorcentajeRetener() {
     return this.porcentajeRetener;
   }
   public void setPorcentajeRetener(String porcentajeRetener) {
     this.porcentajeRetener = porcentajeRetener;
   }
   public String getFechaEmisionCcompModificado() {
     return this.fechaEmisionCcompModificado;
   }
   public void setFechaEmisionCcompModificado(String fechaEmisionCcompModificado) {
     this.fechaEmisionCcompModificado = fechaEmisionCcompModificado;
   }
   public String getNombreComprobante() {
     return this.nombreComprobante;
   }
   public void setNombreComprobante(String nombreComprobante) {
     this.nombreComprobante = nombreComprobante;
   }
   public String getValorRetenido() {
     return this.valorRetenido;
   }
   public void setValorRetenido(String valorRetenido) {
     this.valorRetenido = valorRetenido;
   }
   public String getComprobante() {
     return this.Comprobante;
   }
   public void setComprobante(String comprobante) {
     this.Comprobante = comprobante;
   }
   public String getNumeroComprobante() {
     return this.numeroComprobante;
   }
   public void setNumeroComprobante(String numeroComprobante) {
     this.numeroComprobante = numeroComprobante;
   }
   public String getFechaEmision() {
     return this.fechaEmision;
   }
   public void setFechaEmision(String fechaEmision) {
     this.fechaEmision = fechaEmision;
   }
   public String getPeridoFiscal() {
     return this.peridoFiscal;
   }
   public void setPeridoFiscal(String peridoFiscal) {
     this.peridoFiscal = peridoFiscal;
   }
   public String getBaseImponible() {
     return this.baseImponible;
   }
   public void setBaseImponible(String baseImponible) {
     this.baseImponible = baseImponible;
   }
   public String getNombreImpuesto() {
     return this.nombreImpuesto;
   }
   public void setNombreImpuesto(String nombreImpuesto) {
     this.nombreImpuesto = nombreImpuesto;
   }
   public String getPorcentajeRetencion() {
     return this.porcentajeRetencion;
   }
   public void setPorcentajeRetencion(String porcentajeRetencion) {
     this.porcentajeRetencion = porcentajeRetencion;
   }
   public String getPrecioUnitario() {
     return this.precioUnitario;
   }
   public void setPrecioUnitario(String precioUnitario) {
     this.precioUnitario = precioUnitario;
   }
   public List<InfoAdicional> getInfoAdicional() {
     return this.infoAdicional;
   }
   public void setInfoAdicional(List<InfoAdicional> infoAdicional) {
     this.infoAdicional = infoAdicional;
   }
 }