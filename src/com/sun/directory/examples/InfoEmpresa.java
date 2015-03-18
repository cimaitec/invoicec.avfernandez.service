package com.sun.directory.examples;

public class InfoEmpresa {

	private String ruc;
	private String razonSocial;
	private String razonComercial;
	private String dirMatriz;
	private String contribEspecial;
	private String obligContabilidad;
	private String FecResolContribEspecial;
	
	private String directorio;
	private String dirGenerado;       
	private String dirRecibidos;
	private String dirFirmados;
	private String dirAutorizados;
	private String dirNoAutorizados;
	private String dirContingencias;
	
	private String rutaFirma;
	private String rucFirmante;
	private String claveFirma;
	private String tipoFirma;	
	
   	private String mailEmpresa;    
	private String pathReports;
	private String pathXsd;
	
	private String dirLogo;
	private String servidorSmtp;
	private String portSmtp;
	private String userSmtp;
	private String passSmtp;
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getRazonComercial() {
		return razonComercial;
	}
	public void setRazonComercial(String razonComercial) {
		this.razonComercial = razonComercial;
	}
	public String getDirMatriz() {
		return dirMatriz;
	}
	public void setDirMatriz(String dirMatriz) {
		this.dirMatriz = dirMatriz;
	}
	public String getContribEspecial() {
		return contribEspecial;
	}
	public void setContribEspecial(String contribEspecial) {
		this.contribEspecial = contribEspecial;
	}
	public String getObligContabilidad() {
		return obligContabilidad;
	}
	public void setObligContabilidad(String obligContabilidad) {
		this.obligContabilidad = obligContabilidad;
	}
	public String getFecResolContribEspecial() {
		return FecResolContribEspecial;
	}
	public void setFecResolContribEspecial(String fecResolContribEspecial) {
		FecResolContribEspecial = fecResolContribEspecial;
	}
	public String getDirectorio() {
		return directorio;
	}
	public void setDirectorio(String directorio) {
		this.directorio = directorio;
	}
	public String getDirGenerado() {
		return dirGenerado;
	}
	public void setDirGenerado(String dirGenerado) {
		this.dirGenerado = dirGenerado;
	}
	public String getDirRecibidos() {
		return dirRecibidos;
	}
	public void setDirRecibidos(String dirRecibidos) {
		this.dirRecibidos = dirRecibidos;
	}
	public String getDirFirmados() {
		return dirFirmados;
	}
	public void setDirFirmados(String dirFirmados) {
		this.dirFirmados = dirFirmados;
	}
	public String getDirAutorizados() {
		return dirAutorizados;
	}
	public void setDirAutorizados(String dirAutorizados) {
		this.dirAutorizados = dirAutorizados;
	}
	public String getDirNoAutorizados() {
		return dirNoAutorizados;
	}
	public void setDirNoAutorizados(String dirNoAutorizados) {
		this.dirNoAutorizados = dirNoAutorizados;
	}
	public String getDirContingencias() {
		return dirContingencias;
	}
	public void setDirContingencias(String dirContingencias) {
		this.dirContingencias = dirContingencias;
	}
	public String getRutaFirma() {
		return rutaFirma;
	}
	public void setRutaFirma(String rutaFirma) {
		this.rutaFirma = rutaFirma;
	}
	public String getRucFirmante() {
		return rucFirmante;
	}
	public void setRucFirmante(String rucFirmante) {
		this.rucFirmante = rucFirmante;
	}
	public String getClaveFirma() {
		return claveFirma;
	}
	public void setClaveFirma(String claveFirma) {
		this.claveFirma = claveFirma;
	}
	public String getTipoFirma() {
		return tipoFirma;
	}
	public void setTipoFirma(String tipoFirma) {
		this.tipoFirma = tipoFirma;
	}
	public String getMailEmpresa() {
		return mailEmpresa;
	}
	public void setMailEmpresa(String mailEmpresa) {
		this.mailEmpresa = mailEmpresa;
	}
	public String getPathReports() {
		return pathReports;
	}
	public void setPathReports(String pathReports) {
		this.pathReports = pathReports;
	}
	public String getPathXsd() {
		return pathXsd;
	}
	public void setPathXsd(String pathXsd) {
		this.pathXsd = pathXsd;
	}
	public String getDirLogo() {
		return dirLogo;
	}
	public void setDirLogo(String dirLogo) {
		this.dirLogo = dirLogo;
	}
	public String getServidorSmtp() {
		return servidorSmtp;
	}
	public void setServidorSmtp(String servidorSmtp) {
		this.servidorSmtp = servidorSmtp;
	}
	public String getPortSmtp() {
		return portSmtp;
	}
	public void setPortSmtp(String portSmtp) {
		this.portSmtp = portSmtp;
	}
	public String getUserSmtp() {
		return userSmtp;
	}
	public void setUserSmtp(String userSmtp) {
		this.userSmtp = userSmtp;
	}
	public String getPassSmtp() {
		return passSmtp;
	}
	public void setPassSmtp(String passSmtp) {
		this.passSmtp = passSmtp;
	}
    
  /*
  "UrlWebServices" character varying(2000), -- Url Web Services de empresa Emisora
  "ColorEmpresa" character varying(50), -- Color de la empresa Emisora
    
  
  "sslSMTP" boolean, -- variable que contiene si se requiere de autentificación de SMTP
  
  "marcaAgua" character(1), -- S Si desea marca de Agua...
  "pathMarcaAgua" character varying(300),
  "PathCompRecepcion" character varying(2000), -- Ruta del comprobante de archivo xml de la empresa de  Recepcion
  "CorreoRecepcion" character varying(500), -- contiene el correo de recepcion para envio de correo    
     * */
    
}
