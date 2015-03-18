package com.sun.format;

public class Formatos {

	private String alias;
	private String valor;	
	private int orden;
	
	public Formatos(String ls_alias, int li_orden, String ls_valor){
		alias = ls_alias;
		orden = li_orden;
		valor = ls_valor;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}	
	
	
}
