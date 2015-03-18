package com.util.webServices.ws;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mensaje", propOrder={"identificador", "mensaje", "informacionAdicional", "tipo"})
public class Mensaje
{
  protected String identificador;
  protected String mensaje;
  protected String informacionAdicional;
  protected String tipo;

  public String getIdentificador()
  {
    return this.identificador;
  }

  public void setIdentificador(String value)
  {
    this.identificador = value;
  }

  public String getMensaje()
  {
    return this.mensaje;
  }

  public void setMensaje(String value)
  {
    this.mensaje = value;
  }

  public String getInformacionAdicional()
  {
    return this.informacionAdicional;
  }

  public void setInformacionAdicional(String value)
  {
    this.informacionAdicional = value;
  }

  public String getTipo()
  {
    return this.tipo;
  }

  public void setTipo(String value)
  {
    this.tipo = value;
  }
}