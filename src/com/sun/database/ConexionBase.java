package com.sun.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

import com.sun.directory.examples.ServiceData;
import com.util.util.key.Environment;
import com.util.util.key.Util;

import java.sql.Connection;

//import oracle.jdbc.OracleConnection;

public class ConexionBase {
	private static String Schema="";
	// VPI - GBA
	/// Conexión por Thread Local: Son como globales pero ThreadSafe y no requieren Sincronización y por ende no generan Overhead
	//public static final ThreadLocal<Connection> DBManager = new ThreadLocal<Connection>();
	public static final ThreadLocal<Connection> DBManager = new ThreadLocal<Connection>(){
		  //Fallback a una conexión en caso de Error....
		  @Override protected Connection initialValue(){
		   Connection con = null;
		   try {
		    con = getConexionBD();
		   } catch (Exception e) {
		    System.out.println("Error al obtener la conexión fallback hacia la base");
		    e.printStackTrace();
		   }
		   return con;
		  }
		 };
		 
		 
	public static final ThreadLocal<Connection> DBERP = new ThreadLocal<Connection>() {
		// Fallback a una conexión en caso de Error....
		@Override
		protected Connection initialValue() {
			Connection con = null;
			try {
				con = getConexionBD();
			} catch (Exception e) {
				System.out
						.println("Error al obtener la conexión fallback hacia la base");
				e.printStackTrace();
			}
			return con;
		}
	};
	
		 
	private static 	DataSource dataSource=null;

	public static String getSchema(){
		String ls_Schema = "";
		if ((ls_Schema!=null)&&(ls_Schema.length()>0)){
			ls_Schema = "."+ ConexionBase.Schema;
		}
		return ls_Schema;
	}
	private static void setupDataSource(String jndiName) throws IOException, NamingException {
    	//Utilerias propiedades = new Utilerias();
    	Context  initialContext = new InitialContext();
    	//log.info("setupDataSource request: "+ propiedades.getPropiedad("datasource.JNDI"));
    	dataSource = (DataSource)initialContext.lookup(jndiName);
    }
	
	// VPI - GBA - INI 
	/// Conexión por Thread Local: Son como globales pero ThreadSafe y no requieren Sincronización y por ende no generan Overhead
	public static void init() {
		try {
			DBManager.set(getConexionBD());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cerrarConexionBD() {
		if (DBManager.get() != null) {
			try {
				DBManager.get().close();
				DBManager.remove();
			} catch (Exception e) {
				System.out
						.println("Error al cerrar la conexión a la base para el Thread: "
								+ Thread.currentThread().getName());
				e.printStackTrace();
			}
		}
	}
	
	// VPI - GBA - FIN
	
	/*
    public static OracleConnection getConexion(String nombreDataSource) throws SQLException, IOException, NamingException{
		if (dataSource== null)
    		setupDataSource(nombreDataSource);
		return (OracleConnection) dataSource.getConnection();
		//return  dataSource.getConnection();
    }*/
    
    public static Connection getConexion(String nombreDataSource)throws SQLException, IOException, NamingException{
    	if (dataSource== null)
    		setupDataSource(nombreDataSource);
		return (Connection) dataSource.getConnection();
		//return  dataSource.getConnection();
    	
    }
    
    public static Connection getConexionPostgres(String Driver, String Url, String User, String Pass)throws SQLException, IOException, NamingException,ClassNotFoundException{

    	Connection connection = null;    	
    	Class.forName(Driver);//"org.postgresql.Driver"
    	
    	connection = DriverManager.getConnection(Url, User, Pass);	
    			//Url -->	"jdbc:postgresql://127.0.0.1:5432/fac_electronica"
    			//User -->  "fac_electronica"
    	//VPI - Dead code
		if (connection != null) {
			connection.setAutoCommit(false);
			System.out.println("Conexion Exitosa");
		} else {
			System.out.println("Fallo de Conexion");
		}
				
		return connection;		
	}
    
    public static Connection getConexionBD( )throws SQLException, IOException, NamingException,ClassNotFoundException{
    	//"org.postgresql.Driver";
    	String driver = Util.driverConection; 
    	System.out.println("driver::"+driver);
    	//"jdbc:postgresql://127.0.0.1:5432/fac_electronica";
    	ServiceData.databaseType = driver;
    	String url = Util.urlConection;
    	System.out.println("url::"+url);
    	//"fac_electronica";
    	String user = Util.userConection;
    	System.out.println("user::"+user);
    	//"fac_electronica777";
    	
    	String schemeKey =  Util.schemeKey;
    	System.out.println("schemeKey::"+schemeKey);
    	String keyFile = Util.keyFile;
    	System.out.println("keyFile::"+keyFile);
    	String pass = "";
    	if (schemeKey!=null){    		
			if (schemeKey.equals("KEY")){
				if (keyFile!=null)
					pass = com.util.util.key.EncriptaClave.getFileKey(keyFile);
				else
					pass = "novalido";
			}else{
				pass = Util.passwordConection;
				System.out.println("pass::"+pass);
			}
    	}else{
    		pass = "novalido";
    	}
    	System.out.println("pass::"+pass);
    	Connection connection = null;    	
    	Class.forName(driver);//"org.postgresql.Driver"
    	if (url.indexOf("postgresql")>0){
    		ServiceData.databaseMotor = "PostgreSQL";
    	}
    	if (url.indexOf("sqlserver")>0){
    		ServiceData.databaseMotor = "SQLServer";
    	}
    	connection = DriverManager.getConnection(url, user, pass);
    	
    	//Url -->	"jdbc:postgresql://127.0.0.1:5432/fac_electronica"
    	//User -->  "fac_electronica"
    	//VPI - Dead code
		if (connection != null) {
			connection.setAutoCommit(true);
			System.out.println("Conexion Exitosa");
			
		} else {
			System.out.println("Fallo de Conexion");
		}
				
		return connection;		
	}

    public static Connection getConexionErp(String ipEstablecimiento)throws SQLException, IOException, NamingException,ClassNotFoundException{
    	
    	String driverConectionErp=Environment.c.getString("facElectronica.database.Empresa.driver");
    	String urlConectionErp=Environment.c.getString("facElectronica.database.Empresa.url");
    	String userConectionErp=Environment.c.getString("facElectronica.database.Empresa.user");
    	String passwordConectionErp=Environment.c.getString("facElectronica.database.Empresa.password");
    	
    	urlConectionErp = urlConectionErp.replace("[ipEstablecimiento]", ipEstablecimiento); 
    	//"org.postgresql.Driver";
    	String driver = driverConectionErp;    	
    	//"jdbc:postgresql://127.0.0.1:5432/fac_electronica";
    	ServiceData.databaseType = driver;
    	String url = urlConectionErp;
    	
    	//"fac_electronica";
    	String user = userConectionErp;
    	//"fac_electronica777";
    	String pass = passwordConectionErp;
    	Connection connection = null;    	
    	Class.forName(driver);//"org.postgresql.Driver"
    	if (url.indexOf("postgresql")>0){
    		ServiceData.databaseMotor = "PostgreSQL";
    	}
    	if (url.indexOf("sqlserver")>0){
    		ServiceData.databaseMotor = "SQLServer";
    	}
    	connection = DriverManager.getConnection(url, user, pass);
    	
    	//Url -->	"jdbc:postgresql://127.0.0.1:5432/fac_electronica"
    	//User -->  "fac_electronica"
    	
    	//VPI - Dead code
		if (connection != null) {
			connection.setAutoCommit(true);
			System.out.println("Conexion Exitosa");
		} else {
			System.out.println("Fallo de Conexion");
		}
				
		return connection;		
	}

    
    public static Connection getConexionPostgresEstatica( )throws SQLException, IOException, NamingException,ClassNotFoundException{
    	//"org.postgresql.Driver";
    	String driver = "org.postgresql.Driver";    	
    	//"jdbc:postgresql://127.0.0.1:5432/fac_electronica";
    	String url = "jdbc:postgresql://192.168.32.117:5432/fac_electronica";
    	//"fac_electronica";
    	String user = "fac_electronica";
    	//"fac_electronica777";
    	String pass = "fac_electronica777";
    	Connection connection = null;    	
    	Class.forName(driver);//"org.postgresql.Driver"
    	
    	connection = DriverManager.getConnection(url, user, pass);
    	
    	//Url -->	"jdbc:postgresql://127.0.0.1:5432/fac_electronica"
    	//User -->  "fac_electronica"
    	
    	//VPI - Dead code
		if (connection != null) {
			System.out.println("Conexion Exitosa");
			connection.setAutoCommit(false);
		} else {
			System.out.println("Fallo de Conexion");
		}
				
		return connection;		
	}
    
    public static void main (String arg[]) throws SQLException, IOException, NamingException, ClassNotFoundException{
    	Connection Con = getConexionPostgres("org.postgresql.Driver","jdbc:postgresql://127.0.0.1:5432/fac_electronica","fac_electronica","fac_electronica777");
    	ResultSet Rs;
    	Statement st;
    	st = Con.createStatement();
    	String sql = "SELECT version() ";    	        
    	Rs= st.executeQuery(sql);
    	while (Rs.next()){ 
    	System.out.println("Version ->"+Rs.getString(1));
    	}
    	Rs.close();
    	st.close();
    	Con.close();
    }
    public void limpiaDataSource(){
        if(dataSource!=null){
            dataSource= null;   
        }
    }
    
    /*
    public void ins (String arg[]) throws SQLException, IOException, NamingException, ClassNotFoundException{
    	Connection Con = getConexionPostgres("org.postgresql.Driver","jdbc:postgresql://127.0.0.1:5432/fac_electronica","fac_electronica","fac_electronica777");
    	ResultSet Rs;
    	Statement st;
    	st = Con.createStatement();
    	String sql = "SELECT version() ";    	        
    	Rs= st.executeQuery(sql);
    	while (Rs.next()){ 
    	System.out.println("Version ->"+Rs.getString(1));
    	}
    	Rs.close();
    	st.close();
    	Con.close();
    }
    
    insert into fac_cab_documentos(ambiente, "Ruc", "TipoIdentificacion", "CodEstablecimiento", 
		       "CodPuntEmision", secuencial, "fechaEmision", "guiaRemision", 
		       "razonSocialComprador", "identificacionComprador","totalSinImpuesto", 
		       "totalDescuento", email, propina, moneda, "infoAdicional", "periodoFiscal", 			       
		       rise, "fechaInicioTransporte", "fechaFinTransporte", placa, 
		       "fechaEmisionDocSustento","motivoRazon", "identificacionDestinatario", "razonSocialDestinatario", 
		       "direccionDestinatario", "motivoTraslado", "docAduaneroUnico", "codEstablecimientoDest", 
		       ruta, "codDocSustento", "numDocSustento", "numAutDocSustento", "fecEmisionDocSustento", 
		       autorizacion, fechaautorizacion, "claveAcceso", "importeTotal", "CodigoDocumento", 
		       "codDocModificado", "numDocModificado","motivoValor", "tipIdentificacionComprador", 
		       "tipoEmision", partida, subtotal12, subtotal0, "subtotalNoIva", "totalvalorICE", iva12, 
		       "isActive", "ESTADO_TRANSACCION", "MSJ_ERROR", "Tipo") 
		       values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,
		       ?,?,?,?,?,?,?,?,?,?,?,?,?,?);
        
	*/
}
