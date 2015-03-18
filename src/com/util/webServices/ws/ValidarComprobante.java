package com.util.webServices.ws;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="validarComprobante", propOrder={"xml"})
public class ValidarComprobante
{
  protected byte[] xml;

  public byte[] getXml()
  {
    return this.xml;
  }

  public void setXml(byte[] value)
  {
    this.xml = ((byte[])value);
  }
}