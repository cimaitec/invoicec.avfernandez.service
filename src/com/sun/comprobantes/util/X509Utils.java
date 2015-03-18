package com.sun.comprobantes.util;


import ec.gob.sri.comprobantes.util.AutoridadesCertificantes;
import ec.gob.sri.comprobantes.util.X500NameGeneral;
import ec.gob.sri.comprobantes.util.key.KeyStoreProvider;
import ec.gob.sri.comprobantes.util.key.KeyStoreProviderFactory;
import ec.gob.sri.firmaxades.test.FirmasGenericasXAdES;
import ec.gob.sri.firmaxades.test.ValidacionBasica;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.xml.sax.SAXException;

public class X509Utils
{
  public static final int digitalSignature = 0;
  public static final int nonRepudiation = 1;
  public static final int keyEncipherment = 2;
  public static final int dataEncipherment = 3;
  public static final int keyAgreement = 4;
  public static final int keyCertSign = 5;
  public static final int cRLSign = 6;

  public static boolean puedeFirmar(X509Certificate cert)
  {
    boolean resp = false;
    if (cert.getKeyUsage() == null) {
      resp = true;
    }

    
    if ((cert.getKeyUsage()[0] != false) || (cert.getKeyUsage()[1] != false)) {
      resp = true;
    }
    return resp;
  }

  public static String getUsage(X509Certificate cert)
  {
    StringBuilder sb = new StringBuilder();

    if (cert.getKeyUsage() == null) {
      sb.append("no key usage defined for certificate");
    }
    else {
      if (cert.getKeyUsage()[0] != false) {
        sb.append(" digitalSignature ");
      }

      if (cert.getKeyUsage()[6] != false) {
        sb.append(" cRLSign ");
      }

      if (cert.getKeyUsage()[3] != false) {
        sb.append(" dataEncipherment ");
      }

      if (cert.getKeyUsage()[4] != false) {
        sb.append(" keyAgreement ");
      }

      if (cert.getKeyUsage()[5] != false) {
        sb.append(" keyCertSign ");
      }

      if (cert.getKeyUsage()[2] != false) {
        sb.append(" keyEncipherment ");
      }

      if (cert.getKeyUsage()[1] != false) {
        sb.append(" nonRepudiation ");
      }
    }
    return sb.toString();
  }

  public static String getExtensionIdentifier(X509Certificate cert, String oid)
    throws IOException
  {
    String id = null;
    DERObject derObject = null;
    byte[] extensionValue = cert.getExtensionValue(oid);

    if (extensionValue != null) {
      derObject = toDERObject(extensionValue);
      if ((derObject instanceof DEROctetString)) {
        DEROctetString derOctetString = (DEROctetString)derObject;
        derObject = toDERObject(derOctetString.getOctets());
      }
    }
    if (derObject != null)
      id = derObject.toString();
    else {
      id = null;
    }
    return id;
  }

  public static DERObject toDERObject(byte[] data)
    throws IOException
  {
    ByteArrayInputStream inStream = new ByteArrayInputStream(data);
    ASN1InputStream derInputStream = new ASN1InputStream(inStream);
    return derInputStream.readObject();
  }

  public static String seleccionarCertificado(KeyStore keyStore, String tokenSeleccionado)
    throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateExpiredException, CertificateNotYetValidException, CertificateException
  {
    String aliasSeleccion = null;
    X509Certificate certificado = null;

    Enumeration nombres = keyStore.aliases();
    while (nombres.hasMoreElements()) {
      String aliasKey = (String)nombres.nextElement();
      certificado = (X509Certificate)keyStore.getCertificate(aliasKey);

      X500NameGeneral x500emisor = new X500NameGeneral(certificado.getIssuerDN().getName());
      X500NameGeneral x500sujeto = new X500NameGeneral(certificado.getSubjectDN().getName());

      if ((tokenSeleccionado.equals("SD_BIOPASS")) || ((tokenSeleccionado.equals("SD_EPASS3000")) && (x500emisor.getCN().contains(AutoridadesCertificantes.SECURITY_DATA.getCn()))))
      {
        if ((AutoridadesCertificantes.SECURITY_DATA.getO().equals(x500emisor.getO())) && (AutoridadesCertificantes.SECURITY_DATA.getC().equals(x500emisor.getC())) && (AutoridadesCertificantes.SECURITY_DATA.getO().equals(x500sujeto.getO())) && (AutoridadesCertificantes.SECURITY_DATA.getC().equals(x500sujeto.getC())))
        {
          if ((certificado.getKeyUsage()[0] != false) || (certificado.getKeyUsage()[1] != false)) {
            aliasSeleccion = aliasKey;
            break;
          }
        }
      }
      else if ((tokenSeleccionado.equals("BCE_ALADDIN")) || ((tokenSeleccionado.equals("BCE_IKEY2032")) && (x500emisor.getCN().contains(AutoridadesCertificantes.BANCO_CENTRAL.getCn()))))
      {
        if ((x500emisor.getO().contains(AutoridadesCertificantes.BANCO_CENTRAL.getO())) && (AutoridadesCertificantes.BANCO_CENTRAL.getC().equals(x500emisor.getC())) && (x500sujeto.getO().contains(AutoridadesCertificantes.BANCO_CENTRAL.getO())) && (AutoridadesCertificantes.BANCO_CENTRAL.getC().equals(x500sujeto.getC())))
        {
          if ((certificado.getKeyUsage()[0] != false) || (certificado.getKeyUsage()[1] != false)) {
            aliasSeleccion = aliasKey;
            break;
          }
        }

      }
      else if ((tokenSeleccionado.equals("ANF1")) && (x500emisor.getCN().contains(AutoridadesCertificantes.ANF.getCn())))
      {
        if ((AutoridadesCertificantes.ANF.getO().equals(x500emisor.getO())) && (AutoridadesCertificantes.ANF.getC().equals(x500emisor.getC())) && (AutoridadesCertificantes.ANF.getC().toLowerCase().equals(x500sujeto.getC())))
        {
          if ((certificado.getKeyUsage()[0] != false) || (certificado.getKeyUsage()[1] != false)) {
            aliasSeleccion = aliasKey;
            break;
          }
        }
      }
    }

    return aliasSeleccion;
  }

  public static String firmaValidaArchivo(File archivo, String dirPathSalida, String rucEmisor, String tokenID, String password, String rutaCertificado)
  {
    String aliaskey = null;
    String respuesta = null;
    PrivateKey clavePrivada = null;
    KeyStore ks = null;
    String jreVersion = System.getProperty("java.version");
    try
    {
      if ((System.getProperty("os.name").startsWith("Windows") == true) && ((jreVersion.indexOf("1.6") == 0) || (jreVersion.indexOf("1.7") == 0)))
      {
        ks = KeyStore.getInstance("Windows-MY");
        ks.load(null, null);
        fixAliases(ks);
      } else if (ks == null) {
        //ks = KeyStoreProviderFactory.createKeyStoreProvider().getKeystore(password.toCharArray());
    	  ks = KeyStore.getInstance("PKCS12");
          ks.load(new FileInputStream(rutaCertificado), password.toCharArray());
          fixAliases(ks);
      }/* else {
        respuesta = "Sistema operativo o JRE no compatible los los tokens de firma";
      }*/

      aliaskey = seleccionarCertificado(ks, tokenID);

      if (password == null) {
        clavePrivada = (PrivateKey)ks.getKey(aliaskey, null);
      } else {
        KeyStore tmpKs = ks;

        PrivateKey key = (PrivateKey)tmpKs.getKey(aliaskey, password.toCharArray());
        clavePrivada = key;
      }

      if (aliaskey != null)
      {
        String archivoFirmado = dirPathSalida + archivo.getName();

        Provider provider = null;
        if ((System.getProperty("os.name").toUpperCase().indexOf("MAC") == 0) && (!KeyStoreProviderFactory.existeLibreriaMac()))
        {
          provider = Security.getProvider("SunRsaSign");
        }
        else provider = ks.getProvider();

        FirmasGenericasXAdES firmador = new FirmasGenericasXAdES();
        X509Certificate certificado = (X509Certificate)ks.getCertificate(aliaskey);

        certificado.checkValidity(new GregorianCalendar().getTime());

        String rucCertificado = getExtensionIdentifier(certificado, obtenerOidAutoridad(certificado));
        System.out.println("rucCertificado::"+rucCertificado);
        if ((rucEmisor.equals(rucCertificado)) && (clavePrivada != null))
        {
          //VPI que pasa si el firmador es null?? 	
          firmador.ejecutarFirmaXades(archivo.getAbsolutePath(), null, archivoFirmado, provider, certificado, clavePrivada);

          if (!new ValidacionBasica().validarArchivo(new File(archivoFirmado))) {
            respuesta = "Se ha producido un error al momento de crear \nla firma del comprobante electronico, ya que el la firma digital no es valida";
          }
          if (System.getProperty("os.name").startsWith("Windows") == true)
            ks.load(null, null);
        }
        else if (rucCertificado == null) {
          respuesta = "El certificado digital proporcionado no posee los datos de RUC OID: 1.3.6.1.4.1.37XXX.3.11,\nrazon por la cual usted no podra firmar digitalmente documentos para remitir al SRI,\nfavor actualize su certificado digital con la Autoridad Certificadora";
        } else if (clavePrivada == null) {
          respuesta = "No se pudo acceder a la clave privada del certificado";
        } else {
          respuesta = "El Ruc presente en el certificado digital, no coincide con el Ruc registrado en el aplicativo";
        }
      } else {
        respuesta = "No se pudo encontrar un certificado valido para firmar el archivo";
      }
    }
    catch (CertificateExpiredException ex) {
      Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
      return "El certificado con el que intenta firmar el comprobante esta expirado\nfavor actualize su certificado digital con la Autoridad Certificadora";
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
      return "Archivo XML a firmar mal definido o estructurado";
    } catch (SAXException ex) {
      Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
      return "Archivo XML a firmar mal definido o estructurado";
    } catch (Exception ex) {
      Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
      if (ex.getMessage() == null) {
        respuesta = "Error al firmar archivo: No se pudo acceder a la clave privada del certificado";
      }
      return "Error al firmar archivo: " + ex.getMessage();
    }

    return respuesta;
  }

  public static String obtenerOidAutoridad(X509Certificate certificado)
  {
    String oidRaiz = null;

    X500NameGeneral x500emisor = new X500NameGeneral(certificado.getIssuerDN().getName());
    String nombreAutoridad = x500emisor.getCN();

    if (nombreAutoridad.equals(AutoridadesCertificantes.BANCO_CENTRAL.getCn()))
      oidRaiz = AutoridadesCertificantes.BANCO_CENTRAL.getOid();
    else if (nombreAutoridad.equals(AutoridadesCertificantes.ANF.getCn()))
      oidRaiz = AutoridadesCertificantes.ANF.getOid();
    else if (nombreAutoridad.equals(AutoridadesCertificantes.SECURITY_DATA.getCn())) {
      oidRaiz = AutoridadesCertificantes.SECURITY_DATA.getOid();
    }

    oidRaiz = oidRaiz.concat(".3.11");
    return oidRaiz;
  }

  private static void fixAliases(KeyStore keyStore)
  {
    try
    {
      Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
      field.setAccessible(true);
      KeyStoreSpi keyStoreVeritable = (KeyStoreSpi)field.get(keyStore);

      if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName()))
      {
        field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
        field.setAccessible(true);
        Collection entries = (Collection)field.get(keyStoreVeritable);

        for (Iterator i$ = entries.iterator(); i$.hasNext(); ) { Object entry = i$.next();
          field = entry.getClass().getDeclaredField("certChain");
          field.setAccessible(true);
          X509Certificate[] certificates = (X509Certificate[])field.get(entry);

          String hashCode = Integer.toString(certificates[0].hashCode());

          field = entry.getClass().getDeclaredField("alias");
          field.setAccessible(true);
          String alias = (String)field.get(entry);

          if (!alias.equals(hashCode))
            field.set(entry, alias.concat(" - ").concat(hashCode));
        }
      }
    }
    catch (Exception ex)
    {
      Field field;
      Iterator i$;
      Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}