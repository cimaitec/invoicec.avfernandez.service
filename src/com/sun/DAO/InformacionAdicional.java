package com.sun.DAO;

public class InformacionAdicional {

	private String name;
	private String value;
	
	public InformacionAdicional(){
		   this.name = "";
		   this.value = "";
	}
	public InformacionAdicional(String nombre, String valor){
		   this.name = nombre;
		   this.value = valor;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
