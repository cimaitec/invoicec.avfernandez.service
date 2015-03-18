 package com.sun.reportes.detalles;
 
 public class InfoAdicional
 {
   public InfoAdicional(String nombre, String valor){
	   this.nombre = nombre;
	   this.valor = valor;
   }
   private String nombre;
   private String valor;
 
   public String getNombre()
   {
     return this.nombre;
   }
   public void setNombre(String nombre) {
	 this.nombre = nombre;
   }
 
   public String getValor() {
     return this.valor;
   }
 
   public void setValor(String valor) {
     this.valor = valor;
   }
 }
