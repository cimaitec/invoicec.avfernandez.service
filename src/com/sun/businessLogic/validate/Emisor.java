package com.sun.businessLogic.validate;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.naming.NamingException;

import com.sun.DAO.InformacionTributaria;
import com.sun.database.ConexionBase;
import com.sun.directory.examples.InfoEmpresa;
import com.sun.directory.examples.ServiceData;
import com.util.util.key.Environment;
import com.util.util.key.Util;

public class Emisor {

	private InformacionTributaria infEmisor;
	private String filexml;
	private String fileXmlBackup;
	private String fileTxt;	
	private int resultado;
	private String xml;
	private int ambiente;
	private String codEstablecimiento;
	private String codPuntoEmision;
	private String secuencial;
	private String codigoDocumento;
	private Date fechaEncolada;
	private String ruta;
	private String nameFile;
	private String claveAcceso;
	private String estadoAutorizacion;
	private String numeroAutorizacion;
	private String fechaAutorizacion;
	private Date dateFechaAutorizacion;
	
	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public String getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(String fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

	public Date getDateFechaAutorizacion() {
		return dateFechaAutorizacion;
	}

	public void setDateFechaAutorizacion(Date dateFechaAutorizacion) {
		this.dateFechaAutorizacion = dateFechaAutorizacion;
	}

	public String getEstadoAutorizacion() {
		return estadoAutorizacion;
	}

	public void setEstadoAutorizacion(String estadoAutorizacion) {
		this.estadoAutorizacion = estadoAutorizacion;
	}

	public Emisor() {
		infEmisor = new InformacionTributaria();
	}
	
	public Emisor(String filexml,
				  int codContribEspecial,
				  String ObligadoLlevarContabilidad) {
		infEmisor = new InformacionTributaria();
		this.filexml = filexml;
		infEmisor.setContribEspecial(codContribEspecial);		
		infEmisor.setObligContabilidad(ObligadoLlevarContabilidad);
	}

	public InformacionTributaria getInfEmisor() {
		return infEmisor;
	}

	public void setInfEmisor(InformacionTributaria infEmisor) {
		this.infEmisor = infEmisor;
	}	

	public String statusDocumento(int ambiente, String ruc, String codigoDoc,
			String establecimiento, String puntoEmision, String secuencial)
			throws SQLException, IOException, NamingException,
			ClassNotFoundException {
		
		String status = "";
		//Connection Con = ConexionBase.getConexionBD();
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		
		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " Select \"ESTADO_TRANSACCION\" from "
					+ ConexionBase.getSchema()
					+ "fac_cab_documentos "
					+ "where ambiente = ? and \"Ruc\" = ? and \"CodigoDocumento\" = ? "
					+ " and \"CodEstablecimiento\" = ? and \"CodPuntEmision\" = ? and secuencial = ? ";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, ambiente);
			pst.setString(2, ruc);
			pst.setString(3, codigoDoc);
			pst.setString(4, establecimiento);
			pst.setString(5, puntoEmision);
			pst.setString(6, secuencial);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				status = Rs.getString(1);
			}
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.statusDocumento() >> Error "
					+ "al obtener estado de docuemento en fac_cab_documentos "
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.statusDocumento() >> Error al cerrar conexiones");
			}
		}
		return status;
	}

	
	public int statusDocumentoContingencia(int ambiente, String ruc,
			String codigoDoc, String establecimiento, String puntoEmision,
			String secuencial) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		int cantidad = 0;
		//Connection Con = ConexionBase.getConexionBD();
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		
		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " Select Count(1) from "
					+ ConexionBase.getSchema()
					+ "fac_cola_documentos "
					+ "where ambiente = ? and \"Ruc\" = ? and \"CodigoDocumento\" = ? "
					+ " and \"CodEstablecimiento\" = ? and \"CodPuntoEmision\" = ? and secuencial = ? and \"EstadoDocumento\" in('TD','CD') ";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, ambiente);
			pst.setString(2, ruc);
			pst.setString(3, codigoDoc);
			pst.setString(4, establecimiento);
			pst.setString(5, puntoEmision);
			pst.setString(6, secuencial);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				cantidad = Rs.getInt(1);
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			cantidad = -1;
			System.out.println("Emisor.statusDocumentoContingencia() >> Error "
					+ "al obtener estado de docuemento en fac_cola_documentos "
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.statusDocumentoContingencia() >> Error al cerrar conexiones");
			}
		}
		return cantidad;
	}
	
	public int UpdateEstadoContingencia(String estado, int ambiente,
			String ruc, String codigoDoc, String establecimiento,
			String puntoEmision, String secuencial) throws Exception {

		int resultado = 0;
		Connection Con = null;
		PreparedStatement pst = null;
		try {
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			

			String sql = " update "
					+ ConexionBase.getSchema()
					+ "fac_cola_documentos set \"EstadoDocumento\" = ? "
					+ " where ambiente = ? and \"Ruc\" = ? and \"CodigoDocumento\" = ? "
					+ " and \"CodEstablecimiento\" = ? and \"CodPuntoEmision\" = ? and secuencial = ? and \"EstadoDocumento\" in('CT') ";
			pst = Con.prepareStatement(sql);

			pst.setString(1, estado);
			pst.setInt(2, ambiente);
			pst.setString(3, ruc);
			pst.setString(4, codigoDoc);
			pst.setString(5, establecimiento);
			pst.setString(6, puntoEmision);
			pst.setString(7, secuencial);
			resultado = pst.executeUpdate();
			if (resultado > 0) {
				System.out.println("Actualizado Correctamente");
			} else {
				System.out.println("Actualizado Error");
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.UpdateEstadoContingencia() >> Error "
							+ "al actualizar estado de docuemento en fac_cola_documentos "
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
			throw new Exception(
					"Error,Actualizacion de UpdateEstadoContingencia::,"
							+ e.getMessage());
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.UpdateEstadoContingencia() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}

	
	public String getStatusDocumentoContingencia(int ambiente, String ruc,
			String codigoDoc, String establecimiento, String puntoEmision,
			String secuencial) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		String estado = null;
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " Select \"EstadoDocumento\" from "
					+ ConexionBase.getSchema()
					+ "fac_cola_documentos "
					+ "where ambiente = ? and \"Ruc\" = ? and \"CodigoDocumento\" = ? "
					+ " and \"CodEstablecimiento\" = ? and \"CodPuntoEmision\" = ? and secuencial = ? and \"EstadoDocumento\" in('TD','CD') ";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, ambiente);
			pst.setString(2, ruc);
			pst.setString(3, codigoDoc);
			pst.setString(4, establecimiento);
			pst.setString(5, puntoEmision);
			pst.setString(6, secuencial);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				estado = Rs.getString(1);
			}
			
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.getStatusDocumentoContingencia() >> Error "
							+ "al obtener estado de docuemento en fac_cola_documentos "
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.getStatusDocumentoContingencia() >> Error al cerrar conexiones");
			}
		}
			return estado;
}	
	
	public InformacionTributaria obtieneInfoTributaria(String ps_ruc)
			throws SQLException, IOException, NamingException,
			ClassNotFoundException {
		
		InformacionTributaria infTrib = new InformacionTributaria();
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT \"Ruc\", \"RazonSocial\", \"RazonComercial\", \"DireccionMatriz\", "
					+ " \"ContribEspecial\", \"ObligContabilidad\", \"PathCompGenerados\", \"PathCompFirmados\", \"PathInfoRecibida\",  "
					+ " \"PathCompAutorizados\" , \"PathCompNoAutorizados\", \"PathCompContingencia\", \"emailEnvio\",\"PassFirma\", "
					+ " \"TypeFirma\",\"PathXSD\",\"PathJasper\",\"PathFirma\", COALESCE(\"ruc_firmante\",\"Ruc\") as rucFirmante "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_empresa where \"Ruc\" = ? and \"isActive\" in ('Y','1') ";

			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				System.out.println("Version ->" + Rs.getString(1));
				infTrib.setRuc(Rs.getString(1));
				infTrib.setRazonSocial(Rs.getString(2));
				infTrib.setNombreComercial(Rs.getString(3));
				infTrib.setDireccionMatriz(Rs.getString(4));
				infTrib.setContribEspecial(Rs.getInt(5));
				infTrib.setObligContabilidad(Rs.getString(6));
				infTrib.set_pathGenerados(Rs.getString(7));
				infTrib.set_pathFirmados(Rs.getString(8));
				infTrib.set_pathInfoRecibida(Rs.getString(9));
				infTrib.set_pathAutorizados(Rs.getString(10));
				infTrib.set_pathNoAutorizados(Rs.getString(11));
				infTrib.set_PathCompContingencia(Rs.getString(12));
				infTrib.setMailEmpresa(Rs.getString(13));
				infTrib.set_ClaveFirma(Rs.getString(14));
				infTrib.set_TipoFirma(Rs.getString(15));
				infTrib.set_pathXsd(Rs.getString(16));
				infTrib.set_pathJasper(Rs.getString(17));
				infTrib.set_PathFirma(Rs.getString(18));
				infTrib.setRucFirmante(Rs.getString(19));
			}
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.obtieneInfoTributaria() >> Error "
					+ "al obtener informacion de la empresa" + " >>>> Error :"
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.obtieneInfoTributaria() >> Error al cerrar conexiones");
			}
		}
		return infTrib;
	}
	
	
	
	public InfoEmpresa obtieneInfoEmpresa(String ps_ruc) throws SQLException,
			IOException, NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		InfoEmpresa infEmp = null;
		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT \"RazonSocial\", \"RazonComercial\", \"DireccionMatriz\", "
					+ " \"ContribEspecial\", \"ObligContabilidad\", \"FechaResolucionContribEspecial\", "
					+ " \"PathCompGenerados\", \"PathInfoRecibida\",  "
					+ " \"PathCompFirmados\", \"PathCompAutorizados\", \"PathCompNoAutorizados\",  "
					+ " \"PathCompContingencia\", \"PathFirma\", COALESCE(\"ruc_firmante\",\"Ruc\") as rucFirmante, \"PassFirma\", "
					+ " \"TypeFirma\", \"emailEnvio\", \"PathJasper\", \"PathXSD\", \"PathLogoEmpresa\", "
					+ " \"servidorSMTP\", \"puertoSMTP\", \"userSMTP\", \"passSMTP\" "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_empresa where \"Ruc\" = ? and \"isActive\" in ('Y','1') ";

			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			Rs = pst.executeQuery();

			while (Rs.next()) {

				infEmp = new InfoEmpresa();

				infEmp.setRuc(ps_ruc);
				infEmp.setRazonSocial(Rs.getString(1));
				infEmp.setRazonComercial(Rs.getString(2));
				infEmp.setDirMatriz(Rs.getString(3));
				infEmp.setContribEspecial(Rs.getString(4));
				infEmp.setObligContabilidad(Rs.getString(5));
				infEmp.setFecResolContribEspecial(Rs.getString(6));

				infEmp.setDirectorio(Rs.getString(7));
				infEmp.setDirGenerado(Rs.getString(7));
				infEmp.setDirRecibidos(Rs.getString(8));
				infEmp.setDirFirmados(Rs.getString(9));
				infEmp.setDirAutorizados(Rs.getString(10));
				infEmp.setDirNoAutorizados(Rs.getString(11));
				infEmp.setDirContingencias(Rs.getString(12));

				infEmp.setRutaFirma(Rs.getString(13));
				infEmp.setRucFirmante(Rs.getString(14));
				infEmp.setClaveFirma(Rs.getString(15));
				infEmp.setTipoFirma(Rs.getString(16));

				infEmp.setMailEmpresa(Rs.getString(17));
				infEmp.setPathReports(Rs.getString(18));
				infEmp.setPathXsd(Rs.getString(19));

				infEmp.setDirLogo(Rs.getString(20));
				infEmp.setServidorSmtp(Rs.getString(21));
				infEmp.setPortSmtp(Rs.getString(22));
				infEmp.setUserSmtp(Rs.getString(23));
				infEmp.setPassSmtp(Rs.getString(24));
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.obtieneInfoEmpresa() >> Error "
					+ "al obtener informacion de la empresa" + " >>>> Error :"
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.obtieneInfoEmpresa() >> Error al cerrar conexiones");
			}
		}
		return infEmp;
	}
	
	public ArrayList<Emisor> getTrxContingencia(String ps_ruc, String ps_estado)
			throws SQLException, IOException, NamingException,
			ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		ArrayList<Emisor> ListEmisor = null;
		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " Select top 10 ambiente, CodEstablecimiento, CodPuntoEmision, secuencial, CodigoDocumento, "
					+ " fechaEncolada, Ruta, xml, NameFile, Ruta "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_cola_documentos where EstadoDocumento = ? "
					+ " and Ruc = ? ";

			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_estado);
			pst.setString(2, ps_ruc);
			Rs = pst.executeQuery();
			ListEmisor = new ArrayList<Emisor>();
			
			while (Rs.next()) {
				Emisor em = new Emisor();
				em.setFilexml(Rs.getString("NameFile"));
				em.setXml(Rs.getString("xml"));
				em.setAmbiente(Rs.getInt("ambiente"));
				em.setCodEstablecimiento(Rs.getString("CodEstablecimiento"));
				em.setCodPuntoEmision(Rs.getString("CodPuntoEmision"));
				em.setSecuencial(Rs.getString("secuencial"));
				em.setCodigoDocumento(Rs.getString("CodigoDocumento"));
				em.setFechaEncolada(Rs.getTimestamp("fechaEncolada"));
				em.setRuta(Rs.getString("Ruta"));
				ListEmisor.add(em);
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.getTrxContingencia() >> Error "
					+ "al obtener transacciones en contingencia" + " >>>> Error :"
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.getTrxContingencia() >> Error al cerrar conexiones");
			}
		}
		return ListEmisor;
	}
	
	public InformacionTributaria obtieneInfoEstablecimiento(
			InformacionTributaria infTrib) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT \"Telefono\", \"Correo\", \"DireccionEstablecimiento\" "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_establecimiento where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" = ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, infTrib.getRuc());
			pst.setString(2, infTrib.getCodEstablecimiento());
			Rs = pst.executeQuery();
			while (Rs.next()) {
				infTrib.setTelefonoEstablecimiento(Rs.getString(1));
				infTrib.setMailEstablecimiento(Rs.getString(2));
				infTrib.setDireccionEstablecimiento(Rs.getString(3));
			}
			
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.obtieneInfoEstablecimiento() >> Error "
					+ "al verificar info del establecimiento" + " >>>> Error :"
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.obtieneInfoEstablecimiento() >> Error al cerrar conexiones");
			}
		}
		
		return infTrib;
	}
	
	public InformacionTributaria obtieneMailEstablecimiento(
			InformacionTributaria infTrib) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT \"Correo\", \"PathAnexo\", \"Mensaje\",\"Telefono\", iplocal "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_establecimiento where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" = ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, infTrib.getRuc());
			pst.setString(2, infTrib.getCodEstablecimiento());
			Rs = pst.executeQuery();
			while (Rs.next()) {
				infTrib.setMailEstablecimiento(Rs.getString(1));
				infTrib.setPathAnexoEstablecimiento(Rs.getString(2));
				infTrib.setMensajeEstablecimiento(Rs.getString(3));
				infTrib.setTelefonoEstablecimiento(Rs.getString(4));
				infTrib.setIpEstablecimiento(Rs.getString(5));
			}
			
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.obtieneMailEstablecimiento() >> Error "
					+ "al verificar mail del establecimiento" + " >>>> Error :"
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.obtieneMailEstablecimiento() >> Error al cerrar conexiones");
			}
		}
		
		return infTrib;
	}
	
	public boolean existeEmpresa(String ps_ruc) throws SQLException,
			IOException, NamingException, ClassNotFoundException {
		
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		boolean existEmpresa = false;

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT 1 " + " from " + ConexionBase.getSchema()
					+ "fac_empresa where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				existEmpresa = true;
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.existeEmpresa() >> Error "
					+ "al verificar existencia de la empresa"
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.existeEmpresa() >> Error al cerrar conexiones");
			}
		}
		
		return existEmpresa;
	}

	public boolean existeEstablecimiento(String ps_ruc,
			String ps_CodEstablecimiento) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		boolean existEstablecimiento = false;

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT 1 "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_establecimiento where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" =  ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			pst.setString(2, ps_CodEstablecimiento);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				existEstablecimiento = true;
			}
			
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.existeEstablecimiento() >> Error "
					+ "al verificar existencia del establecimiento"
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.existeEstablecimiento() >> Error al cerrar conexiones");
			}
		}
		
		return existEstablecimiento;
	}
	
	public boolean existePuntoEmision(String ps_ruc,
			String ps_CodEstablecimiento, String ps_CodPuntoEmision)
			throws SQLException, IOException, NamingException,
			ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		boolean existPuntoEmision = false;

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT 1 "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_punto_emision where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" =  ?  and \"CodPuntEmision\" = ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			pst.setString(2, ps_CodEstablecimiento);
			pst.setString(3, ps_CodPuntoEmision);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				existPuntoEmision = true;
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.existePuntoEmision() >> Error "
							+ "al verificar existencia del punto de emision"
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.existePuntoEmision() >> Error al cerrar conexiones");
			}
		}
		return existPuntoEmision;
	}
	
	
	public boolean existeDocumentoPuntoEmision(String ps_ruc,
			String ps_CodEstablecimiento, String ps_CodPuntoEmision,
			String tipoDocumento) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		boolean existPuntoEmision = false;

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT 1 "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_punto_emision where \"Ruc\" = ? "
					+ "and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" =  ?  and \"CodPuntEmision\" = ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			pst.setString(2, ps_CodEstablecimiento);
			pst.setString(3, ps_CodPuntoEmision);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				existPuntoEmision = true;
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out.println("Emisor.existeDocumentoPuntoEmision() >> Error "
					+ "al verificar existencia del punto de emision para el docuemnto"
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.existeDocumentoPuntoEmision() >> Error al cerrar conexiones");
			}
		}
		
		return existPuntoEmision;
	}
	
	
	public String ambienteDocumentoPuntoEmision(String ps_ruc,
			String ps_CodEstablecimiento, String ps_CodPuntoEmision,
			String tipoDocumento) throws SQLException, IOException,
			NamingException, ClassNotFoundException {
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		String ambiente = null;

		ResultSet Rs = null;
		PreparedStatement pst = null;
		try {
			String sql = " SELECT \"TipoAmbiente\" "
					+ " from "
					+ ConexionBase.getSchema()
					+ "fac_punto_emision where \"Ruc\" = ? "
					+ " and \"isActive\" in ('Y','1') and \"CodEstablecimiento\" =  ?  and \"CodPuntEmision\" = ? and \"TipoDocumento\" = ? ";
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ruc);
			pst.setString(2, ps_CodEstablecimiento);
			pst.setString(3, ps_CodPuntoEmision);
			pst.setString(4, tipoDocumento);
			Rs = pst.executeQuery();
			while (Rs.next()) {
				ambiente = (Rs.getString(1).equals("D") ? "1" : (Rs
						.getString(1).equals("P") ? "2" : "-1"));
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.ambienteDocumentoPuntoEmision() >> Error "
							+ "al verificar ambiente de documento"
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (Rs != null)
					Rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.ambienteDocumentoPuntoEmision() >> Error al cerrar conexiones");
			}
		}

		return ambiente;
	}
		
	
	public static synchronized String obtieneClaveContingencia(String ps_ruc, int ps_tipoAmbiente,
			String ps_estado) throws Exception {
		String claveContingencia = "";
		int idClaveContingencia = 0;
		Connection Con = null;
		;
		PreparedStatement pst = null;
		try {
			
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			String storeProcedureObtieneClave = Environment.c.getString("facElectronica.database.facturacion.sql.storeProcedureObtieneClave");
			String sqlObtieneClave = Environment.c.getString("facElectronica.database.facturacion.sql.sqlObtieneClave");
			/*String sql = "{call " + ConexionBase.getSchema()
					+ "pr_GetClaveContingencia(?, ?, ?, ?)}";
			*/
			// CallableStatement cstmt = (CallableStatement) Con
			// .prepareStatement(sql);
			/*
			CallableStatement cstmt = Con
					.prepareCall(" { call pr_GetClaveContingencia (?,?,?,?) }");
			*/
			CallableStatement cstmt = Con.prepareCall(storeProcedureObtieneClave);
			cstmt.setString(1, ps_ruc);
			cstmt.setString(2, String.valueOf(ps_tipoAmbiente));
			cstmt.setString(3, ps_estado);
			cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
			cstmt.execute();
			idClaveContingencia = cstmt.getInt(4);
			cstmt.close();
			//VPI - GBA Con.close();
			//Con = ConexionBase.getConexionBD();
			
			//String driver = Util.driverConection;
			/*
			sqlObtieneClave = " Select clave from " + ConexionBase.getSchema()
					+ "fac_ClavesContingencia "
					+ " where idclavecontingencia=?  ";*/
			ResultSet Rs;
			pst = Con.prepareStatement(sqlObtieneClave);
			pst.setInt(1, idClaveContingencia);
			Rs = pst.executeQuery();

			while (Rs.next()) {
				claveContingencia = Rs.getString(1);
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.obtieneClaveContingencia() >> Error "
							+ "Extraccion de Clave de Contingencia fac_ClavesContingencia"
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
			throw new Exception(
					"Error,Extraccion de Clave de Contingencia fac_ClavesContingencia,"
							+ e.getMessage());
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.obtieneClaveContingencia() >> Error al cerrar conexiones");
			}
		}

		return claveContingencia;
	}
	
	
	public int UpdateErpAutorizacion(String empresa, String databaseErp,
			String autorizacion, Date fechaAutorizacion, String xmlAutorizado,
			String claveAcceso, String idMovimiento) throws Exception {
		int resultado = 0;
		Connection Con = null;
		PreparedStatement pst = null;
		try {
			//VPI - GBA
			//Connection Con = ConexionBase.DBManager.get();
			Con = ConexionBase.getConexionErp(databaseErp);

			String sql = Environment.c.getString("facElectronica.database."
					+ empresa + ".sql.updateInfoSri");
			pst = Con.prepareStatement(sql);

			pst.setString(1, claveAcceso);
			// pst.setDate(2, (java.sql.Date) fechaAutorizacion);
			pst.setString(2, autorizacion);
			pst.setString(3, xmlAutorizado);
			pst.setString(4, idMovimiento);
			/*
			 * pst.setString(2, ps_ruc); pst.setString(3,
			 * String.valueOf(ps_tipoAmbiente));
			 */
			resultado = pst.executeUpdate();

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			resultado = -1;
			System.out.println("Emisor.UpdateErpAutorizacion() >> Error, "
					+ "Actualizacion de UpdateErpAutorizacion"
					+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
			throw new Exception(
					"Emisor.UpdateErpAutorizacion() >> Error, Actualizacion de UpdateErpAutorizacion,"
							+ e.getMessage());
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (Con != null)
					//VPI - GBA 
					Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.UpdateErpAutorizacion() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	
	public int insertaBitacoraDocumento(String ps_ambiente,
										String ps_ruc,  
									    String ps_CodEstablecimiento,
									    String ps_CodPuntEmision,
									    String ps_secuencial,
									    String ps_CodigoDocumento,
									    String ps_fechaEmision,									    
									    String ps_estadoTransaccion,
									    String ps_msgProceso,
									    String ps_msgError,
									    String ps_xmlGenerado,
									    String ps_xmlFirmado,
									    String ps_xmlRespuesta,
									    String ps_xmlAutorizacion,
									    String ps_tipoEmision) throws Exception{
		int resultado = 0;
		if (ServiceData.databaseMotor.equals("PostgreSQL")){
			resultado = insertaBitacoraDocumentoPostgreSQL(ps_ambiente,ps_ruc,ps_CodEstablecimiento,ps_CodPuntEmision,ps_secuencial,
														   ps_CodigoDocumento,ps_fechaEmision,ps_estadoTransaccion,ps_msgProceso,
														   ps_msgError,ps_xmlGenerado,ps_xmlFirmado,ps_xmlRespuesta,ps_xmlAutorizacion);
		}
		if (ServiceData.databaseMotor.equals("SQLServer")){
			resultado = insertaBitacoraDocumentoSQLServer(ps_ambiente,ps_ruc,ps_CodEstablecimiento,ps_CodPuntEmision,ps_secuencial,
														  ps_CodigoDocumento,ps_fechaEmision,ps_estadoTransaccion,ps_msgProceso,
														  ps_msgError,ps_xmlGenerado,ps_xmlFirmado,ps_xmlRespuesta,ps_xmlAutorizacion,ps_tipoEmision);
		}
		return resultado;
	}
	
	public int insertaBitacoraDocumentoPostgreSQL(String ps_ambiente,
			String ps_ruc, String ps_CodEstablecimiento,
			String ps_CodPuntEmision, String ps_secuencial,
			String ps_CodigoDocumento, String ps_fechaEmision,
			String ps_estadoTransaccion, String ps_msgProceso,
			String ps_msgError, String ps_xmlGenerado, String ps_xmlFirmado,
			String ps_xmlRespuesta, String ps_xmlAutorizacion) throws Exception {
		
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		PreparedStatement pst = null;
		int resultado = 0;
		try {
			String sql = " insert into fac_bitacora_documentos(ambiente,\"Ruc\","
					+ "\"CodEstablecimiento\",\"CodPuntEmision\","
					+ "secuencial,\"CodigoDocumento\","
					+ "\"fechaEmision\",\"fechaProceso\","
					+ "\"ESTADO_TRANSACCION\",\"MSJ_PROCESO\","
					+ "\"MSJ_ERROR\",xml_generado,xml_firmado,"
					+ "xml_respuesta,xml_autorizacion) values(?,?,?,?,?,?,to_timestamp(?,'DD-MM-YYYY'),TIMESTAMP WITH TIME ZONE 'now',?,?,?,?,?,?,?);";

			// SimpleDateFormat DATE_FORMAT = new
			// SimpleDateFormat("dd-MM-yyyy");
			// String date = DATE_FORMAT.format(pd_fechaEmision);

			pst = Con.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(ps_ambiente));
			pst.setString(2, ps_ruc);
			pst.setString(3, ps_CodEstablecimiento);
			pst.setString(4, ps_CodPuntEmision);
			pst.setString(5, ps_secuencial);
			pst.setString(6, ps_CodigoDocumento);
			pst.setString(7, ps_fechaEmision);
			pst.setString(8, ps_estadoTransaccion);
			pst.setString(9, ps_msgProceso);
			pst.setString(10, ps_msgError);
			pst.setString(11, ps_xmlGenerado);
			pst.setString(12, ps_xmlFirmado);
			pst.setString(13, ps_xmlRespuesta);
			pst.setString(14, ps_xmlAutorizacion);

			resultado = pst.executeUpdate();

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			resultado = -1;
			System.out
					.println("Emisor.insertaBitacoraDocumentoPostgreSQL() >> Error al insertar "
							+ "en fac_bitacora_documentos"
							+ " >>>> Error :"
							+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.insertaBitacoraDocumentoPostgreSQL() >> Error al cerrar conexiones");
			}
		}

		return resultado;
	}


	
	// INI HFU
	public int insertaColaDocumentos(String ps_ambiente, String ps_ruc,
			String ps_CodEstablecimiento, String ps_CodPuntEmision,
			String ps_secuencial, String ps_CodigoDocumento,
			String ps_fechaEncolamiento, String ps_estadoDocumento,
			String ps_rutaArchivo, String ps_nombreArchivo,
			String ps_claveContingencia, String ps_claveAcceso,
			String ps_claveAccesoGenerada, String ps_xml, String ps_xml_ini){
		
		int resultado = 0;
		
		if (ServiceData.databaseMotor.equals("PostgreSQL")) {
			resultado = insertaColaDocumentosPostgreSQL(ps_ambiente, ps_ruc,
					ps_CodEstablecimiento, ps_CodPuntEmision, ps_secuencial,
					ps_CodigoDocumento, ps_fechaEncolamiento,
					ps_estadoDocumento, ps_rutaArchivo, ps_nombreArchivo);
		}
		
		if (ServiceData.databaseMotor.equals("SQLServer")) {
			resultado = insertaColaDocumentosSQLServer(ps_ambiente, ps_ruc,
					ps_CodEstablecimiento, ps_CodPuntEmision, ps_secuencial,
					ps_CodigoDocumento, ps_fechaEncolamiento,
					ps_estadoDocumento, ps_rutaArchivo, ps_nombreArchivo,
					ps_claveContingencia, ps_claveAcceso,
					ps_claveAccesoGenerada, ps_xml, ps_xml_ini);
		}
		
		return resultado;
	}

	//Metodo para contar claves de contingemcias disponibles
	public int verificaClavesContingencia(String ps_ambiente, String ps_ruc) {
		
		int resultado = -1;
		//El query recibe los campos ambiente y ruc
	
		PreparedStatement pst = null;
		Connection Con = null;
		ResultSet rs = null;
		try {
			String sql = Environment.c.getString("facElectronica.database.facturacion.sql.clavesDisponibles");
			
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			
			pst = Con.prepareStatement(sql);
			pst.setString(1, ps_ambiente);
			pst.setString(2, ps_ruc);
			
			rs= pst.executeQuery();
			while (rs.next()){ 	    		
				resultado=rs.getInt(1);		
			}

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el metodo
			System.out
					.println("Emisor.verificaClavesContingencia() >> Error al verificar "
							+"claves de contingencias disponibles"
							+" >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.verificaClavesContingencia() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	
	public int insertaColaDocumentosPostgreSQL(String ps_ambiente, String ps_ruc,
			String ps_CodEstablecimiento, String ps_CodPuntEmision,
			String ps_secuencial, String ps_CodigoDocumento,
			String ps_fechaEncolamiento, String ps_estadoDocumento,
			String ps_rutaArchivo, String ps_nombreArchivo) {
		
		int resultado = -1;

		PreparedStatement pst = null;
		String sql = "";
		Connection Con = null;
		try {
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			
			//VPI - Falta agregar campos para agregar en la cola documentos con postgresql
			sql = "insert into fac_cola_documentos(ambiente, " + "\"Ruc\", "
					+ "\"CodEstablecimiento\", " + "\"CodPuntEmision\", "
					+ "secuencial, " + "\"CodigoDocumento\", "
					+ "\"fechaEncolada\", " + "\"EstadoDocumento\", "
					+ "\"Ruta\", " + "\"nameFile\") "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(ps_ambiente));
			pst.setString(2, ps_ruc);
			pst.setString(3, ps_CodEstablecimiento);
			pst.setString(4, ps_CodPuntEmision);
			pst.setString(5, ps_secuencial);
			pst.setString(6, ps_CodigoDocumento);
			pst.setString(7, ps_fechaEncolamiento);
			pst.setString(8, ps_estadoDocumento);
			pst.setString(9, ps_rutaArchivo);
			pst.setString(10, ps_nombreArchivo);

			resultado = pst.executeUpdate();

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el metodo
			System.out
					.println("Emisor.insertaColaDocumentosPostgreSQL() >> Error al insertar : \n Ambiente:"
							+ ps_ambiente
							+ " \n Ruc:"
							+ ps_ruc
							+ " \n Establecimiento:"
							+ ps_CodEstablecimiento
							+ " \n Punto de Emision:"
							+ ps_CodPuntEmision
							+ " \n Secuencial:"
							+ ps_secuencial
							+ " \n Cod Docuemnto:"
							+ ps_CodigoDocumento
							+ " \n Fecha Encolamiento"
							+ ps_fechaEncolamiento
							+ " \n Estado Documento:"
							+ ps_estadoDocumento
							+ " \n Ruta Archivo:"
							+ ps_rutaArchivo
							+ " \n Nombre Archivo:"
							+ ps_nombreArchivo
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.insertaColaDocumentosPostgreSQL() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	
	// Para base de datos PostgreSQL
	public int insertaColaDocumentosSQLServer(String ps_ambiente,
			String ps_ruc, String ps_CodEstablecimiento,
			String ps_CodPuntEmision, String ps_secuencial,
			String ps_CodigoDocumento, String ps_fechaEncolamiento,
			String ps_estadoDocumento, String ps_rutaArchivo,
			String ps_nombreArchivo, String ps_claveContingencia,
			String ps_claveAcceso, String ps_claveAccesoGenerada,
			String ps_xml, String ps_xml_ini){

		int resultado = -1;

		PreparedStatement pst = null;

		Connection Con =null;
		String sql = "";
		
		try {
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			
			sql = "insert into fac_cola_documentos(ambiente, "
					+ "\"Ruc\", "
					+ "\"CodEstablecimiento\", "
					+ "\"CodPuntoEmision\", "
					+ "secuencial, "
					+ "\"CodigoDocumento\", "
					+ "\"fechaEncolada\", "
					+ "\"EstadoDocumento\", "
					+ "\"Ruta\", "
					+ "\"nameFile\", clavecontingencia, claveAcceso, clavecontigenciaGenerada, xml, xml_inicial) "
					+ "values(?, ?, ?, ?, ?, ?, GETDATE(), ?, ?, ?, ?, ?, ?, ?, ?)";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(ps_ambiente));
			pst.setString(2, ps_ruc);
			pst.setString(3, ps_CodEstablecimiento);
			pst.setString(4, ps_CodPuntEmision);
			pst.setString(5, ps_secuencial);
			pst.setString(6, ps_CodigoDocumento);
			// pst.setString(7, ps_fechaEncolamiento);
			pst.setString(7, ps_estadoDocumento);
			pst.setString(8, ps_rutaArchivo);
			pst.setString(9, ps_nombreArchivo);
			pst.setString(10, ps_claveContingencia);
			pst.setString(11, ps_claveAcceso);
			pst.setString(12, ps_claveAccesoGenerada);
			pst.setString(13, ps_xml);
			pst.setString(14, ps_xml_ini);

			resultado = pst.executeUpdate();
		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			// metodo
			System.out
					.println("Emisor.insertaColaDocumentosSQLServer() >> Error al insertar : \n Ambiente:"
							+ ps_ambiente
							+ " \n Ruc:"
							+ ps_ruc
							+ " \n Establecimiento:"
							+ ps_CodEstablecimiento
							+ " \n Punto de Emision:"
							+ ps_CodPuntEmision
							+ " \n Secuencial:"
							+ ps_secuencial
							+ " \n Cod Docuemnto:"
							+ ps_CodigoDocumento
							+ " \n Fecha Encolamiento"
							+ ps_fechaEncolamiento
							+ " \n Estado Documento:"
							+ ps_estadoDocumento
							+ " \n Ruta Archivo:"
							+ ps_rutaArchivo
							+ " \n Nombre Archivo:"
							+ ps_nombreArchivo
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.insertaColaDocumentosSQLServer() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	// FIN HFU
	public int insertaBitacoraDocumentoSQLServer(String ps_ambiente,
			String ps_ruc, String ps_CodEstablecimiento,
			String ps_CodPuntEmision, String ps_secuencial,
			String ps_CodigoDocumento, String ps_fechaEmision,
			String ps_estadoTransaccion, String ps_msgProceso,
			String ps_msgError, String ps_xmlGenerado, String ps_xmlFirmado,
			String ps_xmlRespuesta, String ps_xmlAutorizacion,
			String ps_tipoEmision) {
		
		int resultado = -1;
		Connection Con = null;
		PreparedStatement pst = null;
		try {
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			
			String driver = Util.driverConection;
			String ls_sysdate = "";
			if (driver.indexOf("sqlserver") >= 0) {
				ls_sysdate = " GETDATE() ";
			} else {
				ls_sysdate = " now() ";
			}
			String sql = " insert into fac_bitacora_documentos(ambiente,\"Ruc\","
					+ "\"CodEstablecimiento\",\"CodPuntEmision\","
					+ "secuencial,\"CodigoDocumento\","
					+ "\"fechaEmision\",\"fechaProceso\","
					+ "\"ESTADO_TRANSACCION\",\"MSJ_PROCESO\","
					+ "\"MSJ_ERROR\",xml_generado,xml_firmado,"
					+ "xml_respuesta,xml_autorizacion, tipoEmision) values(?,?,?,?,?,?,"
					+ ls_sysdate + "," + ls_sysdate + ",?,?,?,?,?,?,?,?);";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(ps_ambiente));
			pst.setString(2, ps_ruc);
			pst.setString(3, ps_CodEstablecimiento);
			pst.setString(4, ps_CodPuntEmision);
			pst.setString(5, ps_secuencial);
			pst.setString(6, ps_CodigoDocumento);
			// pst.setString(7, ps_fechaEmision);
			pst.setString(7, ps_estadoTransaccion);
			pst.setString(8, ps_msgProceso);
			pst.setString(9, ps_msgError);
			pst.setString(10, ps_xmlGenerado);
			pst.setString(11, ps_xmlFirmado);
			pst.setString(12, ps_xmlRespuesta);
			pst.setString(13, ps_xmlAutorizacion);
			pst.setString(14, ps_tipoEmision);

			resultado = pst.executeUpdate();

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			resultado = -1;
			System.out
					.println("Emisor.insertaBitacoraDocumentoSQLServer() >> Error al insertar : \n Ambiente:"
							+ ps_ambiente
							+ " \n Ruc:"
							+ ps_ruc
							+ " \n Establecimiento:"
							+ ps_CodEstablecimiento
							+ " \n Punto de Emision:"
							+ ps_CodPuntEmision
							+ " \n Secuencial:"
							+ ps_secuencial
							+ " \n Cod Docuemnto:"
							+ ps_CodigoDocumento
							+ " \n Estado transaccion:"
							+ ps_estadoTransaccion
							+ " \n Mesaje proceso:"
							+ ps_msgProceso
							+ " \n Mesaje error:"
							+ ps_msgError
							+ " \n Xml generado:"
							+ ps_xmlGenerado
							+ " \n Xml firmado:"
							+ ps_xmlFirmado
							+ " \n Ruc:"
							+ ps_xmlRespuesta
							+ " \n Xml autorizacion:"
							+ ps_xmlAutorizacion
							+ " \n Tipo Emision:"
							+ ps_tipoEmision
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.insertaBitacoraDocumentoSQLServer() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	
	//VPI se crea metodo generico para bitacorizar 
	// todo tipo de accion en el servicio 
	public int insertaBitacora(Emisor emite ,String ps_estadoTransaccion, String ps_msgProceso,
			String ps_msgError, String ps_xmlGenerado,
			String ps_xmlRespuesta, String ps_xmlAutorizacion) {
		
		int resultado = -1;
		Connection Con = null;
		PreparedStatement pst = null;
		
		int ps_ambiente = emite.getInfEmisor().getAmbiente();
		String ps_ruc = emite.getInfEmisor().getRuc()==null?"":emite.getInfEmisor().getRuc();
		String ps_CodEstablecimiento = emite.getInfEmisor().getCodEstablecimiento()==null?"":emite.getInfEmisor().getCodEstablecimiento();
		String ps_CodPuntEmision = emite.getInfEmisor().getCodPuntoEmision()==null?"":emite.getInfEmisor().getCodPuntoEmision();
		String ps_secuencial = emite.getInfEmisor().getSecuencial()==null?"":emite.getInfEmisor().getSecuencial();
		String ps_CodigoDocumento = emite.getInfEmisor().getTipoComprobante()==null?"":emite.getInfEmisor().getTipoComprobante();
		String ps_tipoEmision = emite.getInfEmisor().getTipoEmision()==null?"":emite.getInfEmisor().getTipoEmision();
		
		
		try {
			//VPI - GBA
			Con = ConexionBase.DBManager.get();
			//Con = ConexionBase.getConexionBD();
			
			String driver = Util.driverConection;
			String ls_sysdate = "";
			if (driver.indexOf("sqlserver") >= 0) {
				ls_sysdate = " GETDATE() ";
			} else {
				ls_sysdate = " now() ";
			}
			String sql = " insert into fac_bitacora_documentos(ambiente,\"Ruc\","
					+ "\"CodEstablecimiento\",\"CodPuntEmision\","
					+ "secuencial,\"CodigoDocumento\","
					+ "\"fechaEmision\",\"fechaProceso\","
					+ "\"ESTADO_TRANSACCION\",\"MSJ_PROCESO\","
					+ "\"MSJ_ERROR\",xml_generado,"
					+ "xml_respuesta,xml_autorizacion, tipoEmision) values(?,?,?,?,?,?,"
					+ ls_sysdate + "," + ls_sysdate + ",?,?,?,?,?,?,?);";

			pst = Con.prepareStatement(sql);
			pst.setInt(1, ps_ambiente);
			pst.setString(2, ps_ruc);
			pst.setString(3, ps_CodEstablecimiento);
			pst.setString(4, ps_CodPuntEmision);
			pst.setString(5, ps_secuencial);
			pst.setString(6, ps_CodigoDocumento);
			pst.setString(7, ps_estadoTransaccion);
			pst.setString(8, ps_msgProceso);
			pst.setString(9, ps_msgError);
			pst.setString(10, ps_xmlGenerado);
			pst.setString(11, ps_xmlRespuesta);
			pst.setString(12, ps_xmlAutorizacion);
			pst.setString(13, ps_tipoEmision);

			resultado = pst.executeUpdate();

		} catch (Exception e) {
			// VPI - SE agrega bloque para que la excepcion sea controlada en el
			System.out
					.println("Emisor.insertaBitacoraDocumentoSQLServer() >> Error al insertar : \n Ambiente:"
							+ ps_ambiente
							+ " \n Ruc:"
							+ ps_ruc
							+ " \n Establecimiento:"
							+ ps_CodEstablecimiento
							+ " \n Punto de Emision:"
							+ ps_CodPuntEmision
							+ " \n Secuencial:"
							+ ps_secuencial
							+ " \n Cod Docuemnto:"
							+ ps_CodigoDocumento
							+ " \n Estado transaccion:"
							+ ps_estadoTransaccion
							+ " \n Mesaje proceso:"
							+ ps_msgProceso
							+ " \n Mesaje error:"
							+ ps_msgError
							+ " \n Xml generado:"
							+ ps_xmlGenerado
							+ " \n Ruc:"
							+ ps_xmlRespuesta
							+ " \n Xml autorizacion:"
							+ ps_xmlAutorizacion
							+ " \n Tipo Emision:"
							+ ps_tipoEmision
							+ " >>>> Error :" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
				//if (Con != null)
					//VPI - GBA Con.close();
			} catch (Exception ex) {
				System.out
						.println("Emisor.insertaBitacoraDocumentoSQLServer() >> Error al cerrar conexiones");
			}
		}
		return resultado;
	}
	
	
	public int obtieneSecuencia(String psRuc,String psCodEstablecimiento,String psCodPuntEmision, String psTiposDocumentos) throws Exception{
		
		ConexionBase Conex;
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
		int liSecuencia = 0, liFlag = 0;
    	ResultSet Rs;
    	PreparedStatement pst;
    	String sql = " SELECT Max(secuencial) " +    			     
    				 " from " + ConexionBase.getSchema() + "fac_cab_documentos where \"Ruc\" = ? " +
    				 " and \"CodEstablecimiento\" =  ? " +
    				 " and \"CodPuntEmision\" =  ? " +
    				 " and \"CodigoDocumento\" =  ? " +    				 
    				 " and \"isActive\" in ('Y','1') ";
    	pst = Con.prepareStatement(sql);
    	pst.setString(1, psRuc);
    	pst.setString(2, psCodEstablecimiento);
    	pst.setString(3, psCodPuntEmision);
    	pst.setString(4, psTiposDocumentos);
    	Rs= pst.executeQuery();
    	while (Rs.next()){    		
    		System.out.println("Secuencial ->"+Rs.getInt(1));
    		liSecuencia = Rs.getInt(1);
    		liFlag = 1;
    		
    	}
    		if (liFlag == 0){
    			liSecuencia = -1;
    		}
    	Rs.close();
    	pst.close();
    	//VPI - GBA Con.close();
		return liSecuencia;
	}

	
	public InformacionTributaria obtieneInfoAdicional(String ps_ruc, String ps_codEstablecimiento, String ps_codPuntoEmision, String ps_tipoDocumento) throws SQLException, IOException, NamingException, ClassNotFoundException{
		InformacionTributaria infTrib = new InformacionTributaria();
		ConexionBase Conex;
		
		//VPI - GBA
		Connection Con = ConexionBase.DBManager.get();
		//Connection Con = ConexionBase.getConexionBD();
		
    	ResultSet Rs;
    	PreparedStatement pst;
    	String sql = " Select \"TipoAmbiente\", \"FormaEmision\" from " + ConexionBase.getSchema() + " fac_punto_emision " +
    			     " where \"Ruc\" = ? " +
    			     " and \"CodEstablecimiento\" = ?  " +
    			     " and \"CodPuntEmision\" = ? " +
    			     " and \"TipoDocumento\" = ? " +
    			     " and \"isActive\" in ('Y','1') ";
    	pst = Con.prepareStatement(sql);
    	
    	pst.setString(1, ps_ruc);
    	pst.setString(2, ps_codEstablecimiento);
    	pst.setString(3, ps_codPuntoEmision);
    	pst.setString(4, ps_tipoDocumento);
    	
    	Rs= pst.executeQuery();
    	while (Rs.next()){ 
    		infTrib.setAmbiente(Integer.parseInt(Rs.getString(1)));
    		infTrib.setTipoEmision(((Rs.getString(2)==null?"1":Rs.getString(2))));
    	}
    	Rs.close();
    	pst.close();
    	//VPI - GBA Con.close();    	
    	
		return infTrib;
	}
	
    public static void main (String arg[]) throws Exception{
    	Emisor e = new Emisor();
    	InformacionTributaria inf = new InformacionTributaria();
    	//inf = e.obtieneInfoTributaria("0992531940001");
    	   	
    	//System.out.println("RazonSocial->"+inf.getRazonSocial());
    	//System.out.println("RazonComercial->"+inf.getNombreComercial());
    	String ls_clave_contingencia;
		try {
			ls_clave_contingencia = e.obtieneClaveContingencia("0992800461001", 2, "0");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new Exception(e1.getMessage());
		}
    	System.out.println("Clave de Contingencia::"+ls_clave_contingencia);
    	//System.out.println("Clave de Acceso de Contingencia::"+LeerDocumentos.generarClaveAccesoContingencia(ls_clave_contingencia));
    }

	public String getFilexml() {
		return filexml;
	}

	public void setFilexml(String filexml) {
		this.filexml = filexml;
	}

	public int getResultado() {
		return resultado;
	}

	public void setResultado(int resultado) {
		this.resultado = resultado;
	}

	public String getFileXmlBackup() {
		return fileXmlBackup;
	}

	public void setFileXmlBackup(String fileXmlBackup) {
		this.fileXmlBackup = fileXmlBackup;
	}

	public String getFileTxt() {
		return fileTxt;
	}

	public void setFileTxt(String fileTxt) {
		this.fileTxt = fileTxt;
	}
    
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public int getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(int ambiente) {
		this.ambiente = ambiente;
	}

	public String getCodEstablecimiento() {
		return codEstablecimiento;
	}

	public void setCodEstablecimiento(String codEstablecimiento) {
		this.codEstablecimiento = codEstablecimiento;
	}

	public String getCodPuntoEmision() {
		return codPuntoEmision;
	}

	public void setCodPuntoEmision(String codPuntoEmision) {
		this.codPuntoEmision = codPuntoEmision;
	}

	public String getSecuencial() {
		return secuencial;
	}

	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}

	public String getCodigoDocumento() {
		return codigoDocumento;
	}

	public void setCodigoDocumento(String codigoDocumento) {
		this.codigoDocumento = codigoDocumento;
	}

	public Date getFechaEncolada() {
		return fechaEncolada;
	}

	public void setFechaEncolada(Date fechaEncolada) {
		this.fechaEncolada = fechaEncolada;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	
	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	@Override
    public String toString() {
		String lsString = "";
		if (infEmisor!=null){
			if (infEmisor.getSecuencial()!=null){
			   lsString = infEmisor.getRuc()+"::"+
						  infEmisor.getCodEstablecimiento()+"-"+infEmisor.getCodPuntoEmision()+"-"+infEmisor.getSecuencial()+"::"+
						  infEmisor.getAmbiente()+"::"+infEmisor.getCodDocumento()+"::"+infEmisor.getTipoEmision()+"::";
			}
		}
		lsString = lsString + filexml+"::";
    	return lsString;
    }
	public String toStringInfo() {
		String lsString = "";
		if (infEmisor!=null){
			if (infEmisor.getClaveAcceso()!=null){
			   lsString = infEmisor.getRuc()+"::";
			}			
		}
		lsString = lsString + filexml+"::";
    	return lsString;
    }

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}
	
	
}
