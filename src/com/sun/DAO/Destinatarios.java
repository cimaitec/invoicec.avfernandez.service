package com.sun.DAO;

import java.util.Date;

public class Destinatarios {

	//DEST //Cabecera
	private int codigo;
	private String identDestinatario;
	private String razonSocialDestinatario;
	private String dirDestinatario;
	private String motTraslDestinatario;
	private String docAduanero;
	private int codEstabDestino;
	private String rutaDest;
	private int codDocSustentoDest;
	private String numDocSustentoDest;
	private String numAutDocSustDest;
	private Date fechEmisionDocSustDest;
	private int sizeDEST = 11;
	
	public String getIdentDestinatario() {
		return identDestinatario;
	}
	public void setIdentDestinatario(String identDestinatario) {
		this.identDestinatario = identDestinatario;
	}
	public String getRazonSocialDestinatario() {
		return razonSocialDestinatario;
	}
	public void setRazonSocialDestinatario(String razonSocialDestinatario) {
		this.razonSocialDestinatario = razonSocialDestinatario;
	}
	public String getDirDestinatario() {
		return dirDestinatario;
	}
	public void setDirDestinatario(String dirDestinatario) {
		this.dirDestinatario = dirDestinatario;
	}
	public String getMotTraslDestinatario() {
		return motTraslDestinatario;
	}
	public void setMotTraslDestinatario(String motTraslDestinatario) {
		this.motTraslDestinatario = motTraslDestinatario;
	}
	public String getDocAduanero() {
		return docAduanero;
	}
	public void setDocAduanero(String docAduanero) {
		this.docAduanero = docAduanero;
	}
	public int getCodEstabDestino() {
		return codEstabDestino;
	}
	public void setCodEstabDestino(int codEstabDestino) {
		this.codEstabDestino = codEstabDestino;
	}
	public String getRutaDest() {
		return rutaDest;
	}
	public void setRutaDest(String rutaDest) {
		this.rutaDest = rutaDest;
	}
	public int getCodDocSustentoDest() {
		return codDocSustentoDest;
	}
	public void setCodDocSustentoDest(int codDocSustentoDest) {
		this.codDocSustentoDest = codDocSustentoDest;
	}
	public String getNumDocSustentoDest() {
		return numDocSustentoDest;
	}
	public void setNumDocSustentoDest(String numDocSustentoDest) {
		this.numDocSustentoDest = numDocSustentoDest;
	}
	public String getNumAutDocSustDest() {
		return numAutDocSustDest;
	}
	public void setNumAutDocSustDest(String numAutDocSustDest) {
		this.numAutDocSustDest = numAutDocSustDest;
	}
	public Date getFechEmisionDocSustDest() {
		return fechEmisionDocSustDest;
	}
	public void setFechEmisionDocSustDest(Date fechEmisionDocSustDest) {
		this.fechEmisionDocSustDest = fechEmisionDocSustDest;
	}
	public int getSizeDEST() {
		return sizeDEST;
	}
	public void setSizeDEST(int sizeDEST) {
		this.sizeDEST = sizeDEST;
	}
}
