package com.sun.comprobantes.util;

import ec.gob.sri.comprobantes.administracion.modelo.Proxy;
import ec.gob.sri.comprobantes.util.Constantes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProxySQL
{  
  private String url;

  public ProxySQL()
  {
    this.url = null;
  }
  

  public Proxy obtenerProxy()
    throws SQLException, ClassNotFoundException
  {
      return getProxy();
  }

  private Proxy getProxy()    
  {
	  Proxy proxy = new Proxy();
      proxy.setUrl("");
      proxy.setPuerto(1);
      proxy.setUsuario("");
      proxy.setClave("[C@ccd65d");
      proxy.setWsProduccion("https://cel.sri.gob.ec");
      proxy.setWsPruebas("https://celcer.sri.gob.ec");    
    return proxy;
  }
}