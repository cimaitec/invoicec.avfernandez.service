package com.sun.comprobantes.util;

import ec.gob.sri.comprobantes.administracion.modelo.Proxy;
import ec.gob.sri.comprobantes.util.TipoAmbienteEnum;

public class FormGenerales
{
  private static char SEPARADOR_DECIMAL = '.';
  
  public static String devuelveUrlWs(String ambiente, String nombreServicio)
  {
    StringBuilder url = new StringBuilder();
    String direccionIPServicio = null;
    Proxy configuracion = null;
    try
    {
      configuracion = new com.sun.comprobantes.util.ProxySQL().obtenerProxy();
    } catch (Exception ex) {
      //Logger.getLogger(FormGenerales.class.getName()).log(Level.SEVERE, null, ex);
      ex.printStackTrace();
    }
    if (configuracion != null)
    {
      if (configuracion.getUrl() != null) {
        String uri = configuracion.getUrl() + ":" + configuracion.getPuerto();
        ProxyConfig.init(uri);
      }
      System.out.println("Produccion::"+TipoAmbienteEnum.PRODUCCION.getCode());
      System.out.println("Pruebas::"+TipoAmbienteEnum.PRUEBAS.getCode());
      if (ambiente.equals(TipoAmbienteEnum.PRODUCCION.getCode()) == true)
        direccionIPServicio = configuracion.getWsProduccion();
      else if (ambiente.equals(TipoAmbienteEnum.PRUEBAS.getCode()) == true) {
        direccionIPServicio = configuracion.getWsPruebas();
      }
      url.append(direccionIPServicio);

      url.append("/comprobantes-electronicos-ws/");
      url.append(nombreServicio);
      url.append("?wsdl");
      System.out.print(url.toString());
    }
    return url.toString();
  }

 
}