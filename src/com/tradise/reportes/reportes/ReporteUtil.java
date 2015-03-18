package com.tradise.reportes.reportes;
 
import com.sun.DAO.DetalleTotalImpuestos;
import com.sun.DAO.InformacionTributaria;
import com.sun.reportes.detalles.DetallesAdicionales;
import com.sun.reportes.detalles.InfoAdicional;
import com.tradise.reportes.entidades.FacCabDocumento;
import com.tradise.reportes.entidades.FacDetAdicional;
import com.tradise.reportes.entidades.FacDetDocumento;
import com.tradise.reportes.entidades.FacDetMotivosdebito;
import com.tradise.reportes.entidades.FacDetRetencione;
import com.tradise.reportes.entidades.FacEmpresa;
import com.tradise.reportes.entidades.FacGeneral;
import com.tradise.reportes.entidades.FacProducto;
import com.tradise.reportes.servicios.ReporteServicio;
import com.util.util.key.Environment;
import com.util.util.key.GenericTransaction;

import ec.gob.sri.comprobantes.administracion.modelo.Emisor;
import ec.gob.sri.comprobantes.modelo.reportes.DetalleGuiaReporte;
import ec.gob.sri.comprobantes.modelo.reportes.DetallesAdicionalesReporte;
import ec.gob.sri.comprobantes.modelo.reportes.GuiaRemisionReporte;
import ec.gob.sri.comprobantes.modelo.reportes.InformacionAdicional;
import ec.gob.sri.comprobantes.sql.EmisorSQL;
import ec.gob.sri.comprobantes.util.reportes.JasperViwerSRI;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.save.JRPdfSaveContributor;
 
 public class ReporteUtil extends GenericTransaction
 {
   private String Ruc;
   private String codEst;
   private String codPuntEm;
   private String codDoc;
   private String secuencial;
   private ReporteServicio servicio = new ReporteServicio();
   private static String classReference;
   private Locale regionalConf = new Locale("en","US");
 
   private static Emisor obtenerEmisor()
     throws SQLException, ClassNotFoundException
   {
     EmisorSQL emisSQL = new EmisorSQL();
     return emisSQL.obtenerDatosEmisor();
   }
 
 

   public static void main(String[] arg) throws SQLException, ClassNotFoundException {
 }
 

   
   public static String generaPdfDocumentos(com.sun.businessLogic.validate.Emisor emite, String ruc, String codEst, String codPtoEmi, String tipoDocumento, String secuencial, String rutaJasper, String rutaReporte, String nameReporte)
     throws Exception
   {
     ReporteUtil rep = new ReporteUtil();
 
     rep.setRuc(ruc);
     rep.setCodEst(codEst);
     rep.setCodPuntEm(codPtoEmi);
     rep.setCodDoc(tipoDocumento);
     rep.setSecuencial(secuencial);
    classReference = "ReporteUtil";
     String reportePdf="";
     String jasperFile = "";
     try {
       jasperFile = Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc()) == null ? "" : Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc());
     }
     catch (Exception e) {
       jasperFile = Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc()) == null ? "" : Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc());
     }
     if ((jasperFile.equals("")) || (jasperFile == null)) {
       jasperFile = Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc()) == null ? "" : Environment.c.getString("facElectronica.pdf.jasper.doc" + ruc + "_" + rep.getCodDoc());
     }
     if ((jasperFile.equals("")) || (jasperFile == null)) {
       if (rep.getCodDoc().equals("01"))
         jasperFile = "factura.jasper";
       if (rep.getCodDoc().equals("04"))
         jasperFile = "notaCreditoFinal.jasper";
       if (rep.getCodDoc().equals("05"))
         jasperFile = "notaDebitoFinal.jasper";
       if (rep.getCodDoc().equals("06"))
         jasperFile = "guiaRemisionFinal.jasper";
       if (rep.getCodDoc().equals("07"))
         jasperFile = "comprobanteRetencion.jasper";
     }
     try
     {
				
      if (rep.getCodDoc().equals("01"))
         reportePdf=rep.generarReporteFac(emite,rutaJasper + jasperFile, rutaReporte + nameReporte);
       if (rep.getCodDoc().equals("04"))
         reportePdf= rep.generarReporteCred(emite,rutaJasper + jasperFile, rutaReporte + nameReporte);
       if (rep.getCodDoc().equals("05"))
         rep.generarReporteNotaDebito(rutaJasper + jasperFile, rutaReporte + nameReporte);
       if (rep.getCodDoc().equals("06"))
         rep.generarReporteGuia(rutaJasper + jasperFile, rutaReporte + nameReporte);
       if (rep.getCodDoc().equals("07"))
         reportePdf=rep.generarReporteRetencion(emite,rutaJasper + jasperFile, rutaReporte + nameReporte);
     }
     catch (SQLException e)
     {				
       e.printStackTrace();
				return "";
     }
     catch (ClassNotFoundException e) {
       e.printStackTrace();
				return "";
     }
 
     return reportePdf;
   }

   public String generarReporteFac(com.sun.businessLogic.validate.Emisor emite,
								   String urlReporte, 
								   String numfact)
     throws SQLException, ClassNotFoundException
   {
     FileInputStream is = null;
     JRDataSource dataSource = null;
     List detallesAdiciones = new ArrayList();
     List infoAdicional = new ArrayList();
     List detDocumento = new ArrayList();
     //List detAdicional = new ArrayList();
     try
     {
				detDocumento = emite.getInfEmisor().getListDetDocumentos();/*
				if (emite.getInfEmisor().getListInfAdicional()!= null){
		       		if (emite.getInfEmisor().getListInfAdicional().size()>0) {
			       		  for (int i = 0; i < emite.getInfEmisor().getListInfAdicional().size(); i++) {
			       		    InformacionAdicional infoAd = new InformacionAdicional();
			       		    infoAd.setNombre(emite.getInfEmisor().getListInfAdicional().get(i).getName());
			       		    infoAd.setValor(emite.getInfEmisor().getListInfAdicional().get(i).getValue());
			       		    infoAdicional.add(i, infoAd);
			       		  }
			       		}
				}*/
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		        otherSymbols.setDecimalSeparator('.');
		        otherSymbols.setGroupingSeparator(','); 
		        DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
		        
				if (detDocumento!= null){
        if (!detDocumento.isEmpty()){
         for (int i = 0; i < emite.getInfEmisor().getListDetDocumentos().size(); i++) {
           DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
           
		   detAd.setCodigoPrincipal(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoPrincipal());         
           detAd.setDescuento(df.format((emite.getInfEmisor().getListDetDocumentos().get(i).getDescuento())));
           detAd.setCodigoAuxiliar(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoAuxiliar());
           detAd.setDescripcion(emite.getInfEmisor().getListDetDocumentos().get(i).getDescripcion());
           //VPI
           detAd.setCantidad(String.valueOf(df.format(emite.getInfEmisor().getListDetDocumentos().get(i).getCantidad())));
           detAd.setPrecioTotalSinImpuesto(df.format(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioTotalSinImpuesto()));
           detAd.setPrecioUnitario(df.format(new Double(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioUnitario())));
           detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
           detallesAdiciones.add(i, detAd);
         		  }
			    }
     }
			  }
     catch (Exception e) {
       e.printStackTrace();
     }
     try {
       dataSource = new JRBeanCollectionDataSource(detallesAdiciones);
			    //urlReporte="/usr/facElectronica/Procesos/FacturadorElectronico/resources/reportes/factura.jasper";
				System.out.println("urlReporte::"+urlReporte);
       is = new FileInputStream(urlReporte);
			    JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTributaria(emite), obtenerInfoFactura(emite)), dataSource);
				System.out.println("numfact::"+numfact);
				//System.out.println("reporte_view::"+reporte_view);
      JasperExportManager.exportReportToPdfFile(reporte_view, numfact);
      
			  }
     catch (FileNotFoundException ex) {
    	 ex.printStackTrace();
       //Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex1) {
    	   ex1.printStackTrace();
         //Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
       }
     }
     catch (JRException e)
     {	e.printStackTrace();
      //Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
       try
       {
         if (is != null)
          is.close();
       }
       catch (IOException ex) {
    	   ex.printStackTrace();
         //Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
       catch (Exception exc) {
    	   exc.printStackTrace();
    	   /* 128 */         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, exc);
    	          }
     }
     finally
     {
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
    	   ex.printStackTrace();
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
				return numfact;
   }

   
  public String generarReporteCred(com.sun.businessLogic.validate.Emisor emite,
		   String urlReporte, 
		   String numcred)
throws SQLException, ClassNotFoundException{
     FileInputStream is = null;
     JRDataSource dataSource = null;
     List detallesAdiciones = new ArrayList();
     List infoAdicional = new ArrayList();
     List detDocumento = new ArrayList();
     List detAdicional = new ArrayList();
try{
detDocumento = emite.getInfEmisor().getListDetDocumentos();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
if (detDocumento!= null){
       if (!detDocumento.isEmpty()){
         for (int i = 0; i < emite.getInfEmisor().getListDetDocumentos().size(); i++) {
           DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
					detAd.setCodigoPrincipal(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoPrincipal());

//detAd = emite.getInfEmisor().getListDetDocumentos().get(i).getListDetAdicionalesDocumentos();
           detAd.setDescuento(df.format((emite.getInfEmisor().getListDetDocumentos().get(i).getDescuento())));
           detAd.setCodigoAuxiliar(emite.getInfEmisor().getListDetDocumentos().get(i).getCodigoAuxiliar());
           detAd.setDescripcion(emite.getInfEmisor().getListDetDocumentos().get(i).getDescripcion());
           //VPI
           detAd.setCantidad(String.valueOf(df.format(emite.getInfEmisor().getListDetDocumentos().get(i).getCantidad())));
          detAd.setPrecioTotalSinImpuesto(df.format(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioTotalSinImpuesto()));
           detAd.setPrecioUnitario(df.format(new Double(emite.getInfEmisor().getListDetDocumentos().get(i).getPrecioUnitario())));
           detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
           detallesAdiciones.add(i, detAd);
}
}
}
}
catch (Exception e) {
       e.printStackTrace();
}
try {
       dataSource = new JRBeanCollectionDataSource(detallesAdiciones);
//urlReporte="/usr/facElectronica/Procesos/FacturadorElectronico/resources/reportes/factura.jasper";
System.out.println("urlReporte::"+urlReporte);
       is = new FileInputStream(urlReporte);
JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTributaria(emite), obtenerInfoCredito(emite)), dataSource);
System.out.println("numcred::"+numcred);
//System.out.println("reporte_view::"+reporte_view);
       JasperExportManager.exportReportToPdfFile(reporte_view, numcred);

}
catch (FileNotFoundException ex) {
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
try
{
         if (is != null)
           is.close();
}
catch (IOException ex1) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
}
}
catch (JRException e)
{
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
try
{
         if (is != null)
           is.close();
}
catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
}
}
finally
{
try
{
         if (is != null)
           is.close();
}
catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
}
}
return numcred;
}



 
   public void generarReporteNotaDebito(String urlReporte, String numrep)
     throws SQLException, ClassNotFoundException
   {
     FileInputStream is = null;
     try
     {
       List debito = new ArrayList();
       List adicional = new ArrayList();
       List detAdicional = new ArrayList();
       List infoAdicional = new ArrayList();
       try
       {
		   		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		   		otherSymbols.setDecimalSeparator('.');
		   		otherSymbols.setGroupingSeparator(','); 
		   		DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
   		
         adicional = this.servicio.buscarDetAdicional(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         debito = this.servicio.buscarMotivosDebito(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         if (!adicional.isEmpty()) {
           for (int i = 0; i < adicional.size(); i++) {
             InformacionAdicional info = new InformacionAdicional();
             info.setNombre(((FacDetAdicional)adicional.get(i)).getNombre());
             info.setValor(((FacDetAdicional)adicional.get(i)).getValor());
             infoAdicional.add(i, info);
           }
         }
         if (!debito.isEmpty())
           for (int i = 0; i < debito.size(); i++) {
             DetallesAdicionales detAdi = new DetallesAdicionales();
             detAdi.setRazonModificacion(((FacDetMotivosdebito)debito.get(i)).getRazon());
             detAdi.setValorModificacion(df.format(((FacDetMotivosdebito)debito.get(i)).getBaseImponible()));
             detAdi.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
             detAdicional.add(i, detAdi);
           }
       }
       catch (Exception e)
       {
         e.printStackTrace();
       }
       JRDataSource dataSource = new JRBeanCollectionDataSource(detAdicional);
       is = new FileInputStream(urlReporte);
       JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoND()), dataSource);
       JasperExportManager.exportReportToPdfFile(reporte_view, numrep);
     }
     catch (FileNotFoundException ex) {
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       ex.printStackTrace();
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex1) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
       }
     }
     catch (JRException e)
     {
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
       e.printStackTrace();
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
     finally
     {
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
   }

   public void generarReporteNotaCredito(com.sun.businessLogic.validate.Emisor emite,
		   								 String urlReporte, 
		   								 String numrep)
		     throws SQLException, ClassNotFoundException
		   {
		     FileInputStream is = null;
		     try {
		       List detallesAdiciones = new ArrayList();
		       List infoAdicional = new ArrayList();
		       List detDocumento = new ArrayList();
		       List detAdicional = new ArrayList();
		 
		       JRDataSource dataSource = null;
		       try {
		    	   detDocumento = emite.getInfEmisor().getListDetDocumentos();
		         detAdicional = emite.getInfEmisor().getListInfAdicional();
		         if (!detAdicional.isEmpty()) {
		           for (int i = 0; i < detAdicional.size(); i++) {
		             InformacionAdicional infoAd = new InformacionAdicional();
		             infoAd.setNombre(((FacDetAdicional)detAdicional.get(i)).getNombre());
		             infoAd.setValor(((FacDetAdicional)detAdicional.get(i)).getValor());
		             infoAdicional.add(i, infoAd);
		           }
		         }
		 
						DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				   		otherSymbols.setDecimalSeparator('.');
				   		otherSymbols.setGroupingSeparator(','); 
				   		DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
		         if (!detDocumento.isEmpty()) {
		           for (int i = 0; i < detDocumento.size(); i++) {
		             //FacProducto producto = new FacProducto();
		             DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
		             detAd.setCodigoPrincipal(((FacDetDocumento)detDocumento.get(i)).getCodPrincipal());
		             //producto = this.servicio.buscarProductos(Integer.valueOf(((FacDetDocumento)detDocumento.get(i)).getCodPrincipal().trim()).intValue());
		             //detAd.setDetalle1(producto.getAtributo1());
		             //detAd.setDetalle2(producto.getAtributo2());
		             //detAd.setDetalle3(producto.getAtributo3());
		             detAd.setDescuento(df.format(((FacDetDocumento)detDocumento.get(i)).getDescuento()));
		             detAd.setCodigoAuxiliar(((FacDetDocumento)detDocumento.get(i)).getCodAuxiliar());
		             detAd.setDescripcion(((FacDetDocumento)detDocumento.get(i)).getDescripcion());
		             //VPI
		             detAd.setCantidad(String.valueOf(df.format(((FacDetDocumento)detDocumento.get(i)).getCantidad())));
		             detAd.setPrecioTotalSinImpuesto(df.format(((FacDetDocumento)detDocumento.get(i)).getPrecioTotalSinImpuesto()));
		             detAd.setPrecioUnitario(df.format(((FacDetDocumento)detDocumento.get(i)).getPrecioUnitario()));
		             detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
		             detallesAdiciones.add(i, detAd);
		           }
		         }
		 
		       }
		       catch (Exception e)
		       {
		         e.printStackTrace();
		       }
		       dataSource = new JRBeanCollectionDataSource(detallesAdiciones);
		 
		       is = new FileInputStream(urlReporte);
		       JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTributaria(emite), obtenerInfoNC()), dataSource);
		       JasperExportManager.exportReportToPdfFile(reporte_view, numrep);
		     }
		     catch (FileNotFoundException ex) {
		       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
		       try
		       {
		         if (is != null)
		           is.close();
		       }
		       catch (IOException ex1) {
		         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
		       }
		     }
		     catch (JRException e)
		     {
		       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
		       try
		       {
		         if (is != null)
		           is.close();
		       }
		       catch (IOException ex) {
		         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
		       }
		     }
		     finally
		     {
		       try
		       {
		         if (is != null)
		           is.close();
		       }
		       catch (IOException ex) {
		         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
		       }
		     }
		   }
   
   public void generarReporteNotaCredito(String urlReporte, String numrep)
     throws SQLException, ClassNotFoundException
   {
     FileInputStream is = null;
     try {
       List detallesAdiciones = new ArrayList();
       List infoAdicional = new ArrayList();
       List detDocumento = new ArrayList();
       List detAdicional = new ArrayList();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
       JRDataSource dataSource = null;
       try {
        
    	   		  detDocumento = this.servicio.buscarDatosDetallesDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         detAdicional = this.servicio.buscarDetAdicional(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         if (!detAdicional.isEmpty()) {
           for (int i = 0; i < detAdicional.size(); i++) {
             InformacionAdicional infoAd = new InformacionAdicional();
             infoAd.setNombre(((FacDetAdicional)detAdicional.get(i)).getNombre());
             infoAd.setValor(((FacDetAdicional)detAdicional.get(i)).getValor());
             infoAdicional.add(i, infoAd);
           }
         }
 
         if (!detDocumento.isEmpty()) {
           for (int i = 0; i < detDocumento.size(); i++) {
             FacProducto producto = new FacProducto();
             DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
             detAd.setCodigoPrincipal(((FacDetDocumento)detDocumento.get(i)).getCodPrincipal());
             producto = this.servicio.buscarProductos(Integer.valueOf(((FacDetDocumento)detDocumento.get(i)).getCodPrincipal().trim()).intValue());
             detAd.setDetalle1(producto.getAtributo1());
             detAd.setDetalle2(producto.getAtributo2());
             detAd.setDetalle3(producto.getAtributo3());
             detAd.setDescuento(df.format(((FacDetDocumento)detDocumento.get(i)).getDescuento()));
             detAd.setCodigoAuxiliar(((FacDetDocumento)detDocumento.get(i)).getCodAuxiliar());
             detAd.setDescripcion(((FacDetDocumento)detDocumento.get(i)).getDescripcion());
             //VPI
             detAd.setCantidad(String.valueOf(df.format(((FacDetDocumento)detDocumento.get(i)).getCantidad())));
             detAd.setPrecioTotalSinImpuesto(df.format(((FacDetDocumento)detDocumento.get(i)).getPrecioTotalSinImpuesto()));
             detAd.setPrecioUnitario(df.format(((FacDetDocumento)detDocumento.get(i)).getPrecioUnitario()));
             detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
             detallesAdiciones.add(i, detAd);
           }
         }
 
       }
       catch (Exception e)
       {
         e.printStackTrace();
       }
       dataSource = new JRBeanCollectionDataSource(detallesAdiciones);
 
       is = new FileInputStream(urlReporte);
       JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoNC()), dataSource);
       JasperExportManager.exportReportToPdfFile(reporte_view, numrep);
     }
     catch (FileNotFoundException ex) {
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex1) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
       }
     }
     catch (JRException e)
     {
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
     finally
     {
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
   }
 
   public void generarReporteGuia(String urlReporte, String numrep)
     throws SQLException, ClassNotFoundException
   {
     FileInputStream is = null;
     try
     {
       List detDocumento = new ArrayList();
       List detGuia = new ArrayList();
 
       FacCabDocumento cabDoc = new FacCabDocumento();
       List guiaLista = new ArrayList();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
       try {
         cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         detDocumento = this.servicio.buscarDatosDetallesDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
         if (!detDocumento.isEmpty())
         {
           for (int i = 0; i < detDocumento.size(); i++) {
             DetalleGuiaReporte guiaRem = new DetalleGuiaReporte();
             guiaRem.setCantidad(String.valueOf(((FacDetDocumento)detDocumento.get(i)).getCantidad()));
             guiaRem.setCodigoAuxiliar(((FacDetDocumento)detDocumento.get(i)).getCodAuxiliar());
             guiaRem.setCodigoPrincipal(((FacDetDocumento)detDocumento.get(i)).getCodPrincipal());
             guiaRem.setDescripcion(((FacDetDocumento)detDocumento.get(i)).getDescripcion());
             detGuia.add(i, guiaRem);
           }
 
           GuiaRemisionReporte rep = new GuiaRemisionReporte();
           rep.setCodigoEstab(cabDoc.getCodEstablecimientoDest());
           rep.setDestino(cabDoc.getRuta());
           rep.setDetalles(detGuia);
           rep.setDocAduanero(cabDoc.getDocAduaneroUnico());
           rep.setFechaEmisionSustento(cabDoc.getFechaEmisionDocSustento() == null ? null : new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(cabDoc.getFechaEmisionDocSustento()));
           rep.setMotivoTraslado(cabDoc.getMotivoTraslado());
           rep.setNombreComprobante(cabDoc.getCodigoDocumento());
           rep.setNumDocSustento(cabDoc.getNumDocSustento());
           rep.setNumeroAutorizacion(String.valueOf(cabDoc.getNumAutDocSustento()));
           rep.setRazonSocial(cabDoc.getRazonSocialDestinatario());
           rep.setRucDestinatario(cabDoc.getIdentificacionDestinatario());
           rep.setRuta(cabDoc.getRuta());
           guiaLista.add(rep);
         }
       }
       catch (Exception e)
       {
         e.printStackTrace();
       }
       JRDataSource dataSource = new JRBeanCollectionDataSource(guiaLista);
       is = new FileInputStream(urlReporte);
       JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoGR()), dataSource);
       JasperExportManager.exportReportToPdfFile(reporte_view, numrep);
     }
     catch (FileNotFoundException ex) {
       ex.printStackTrace();
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex1) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
       }
     }
     catch (JRException e)
     {
       e.printStackTrace();
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
     finally
     {
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
   }
 

    public String generarReporteRetencion(com.sun.businessLogic.validate.Emisor emite,
											  String urlReporte, 
											  String numret)
     throws SQLException, ClassNotFoundException
   {
	 FileInputStream is = null;
     try {
       List detRetencion = new ArrayList(); 
       FacCabDocumento cabDoc = new FacCabDocumento();
       InformacionAdicional info = null;
       List infoAdicional = new ArrayList();
       DetallesAdicionales detalles = null;
       List detallesAdicional = new ArrayList();
       List detAdicionals = new ArrayList();
       try {
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
           if (emite.getInfEmisor().getListDetImpuestosRetenciones().size()>0){
						for (int i = 0; i < emite.getInfEmisor().getListDetImpuestosRetenciones().size(); i++) {
							 String comprobante = "";
							 
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodDocSustento().trim().equals("01")) comprobante = "FACTURA";
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodDocSustento().trim().equals("04")) comprobante = "NOTA DE CREDITO";
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodDocSustento().trim().equals("05")) comprobante = "NOTA DE DEBITO";
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodDocSustento().trim().equals("06")) comprobante = "GUIA DE REMISION";
							 detalles = new DetallesAdicionales();
							 detalles.setBaseImponible(df.format(emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getBaseImponible()));
							 detalles.setComprobante(comprobante);
							 detalles.setNombreComprobante(comprobante);
							 detalles.setFechaEmisionCcompModificado(emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getFechaEmisionDocSustento() == null ? null : (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getFechaEmisionDocSustento().replace(" ", "/")));
							 detalles.setNumeroComprobante(String.valueOf(emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getNumDocSustento()));
							 detalles.setPorcentajeRetener(String.valueOf((emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getPorcentajeRetener()))+"%");
							 detalles.setPeridoFiscal(String.valueOf(emite.getInfEmisor().getPeriodoFiscal()));
							 //detalles.setValorRetenido("0.00");
							 double valorRetenido = 0;
							 String valRetenido = "0";
							 try{
								 valorRetenido = emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getValorRetenido();
								 valRetenido = Double.toString(valorRetenido);
							 }catch(Exception e){
								 valRetenido = "0";
							 }
							 detalles.setValorRetenido(((valRetenido)));
							 
							 //detalles.setInfoAdicional(infoAdicional);
							 //FacGeneral general = new FacGeneral();
							 boolean flagIva = false;
							 String descripcion = "";
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigo().equals("1")){
								 descripcion = "RET. FTE";
							 }
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigo().equals("2")){
								 flagIva = true;
								 descripcion = "IVA";
							 }
							 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigo().equals("6")){
								 descripcion = "ISD";
							 }
							 if (flagIva){
								 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigoRetencion().equals("1")){
									 detalles.setPorcentajeRetencion(String.valueOf("721"));
								 }
								 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigoRetencion().equals("2")){
									 detalles.setPorcentajeRetencion(String.valueOf("723"));
								 }
								 if (emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigoRetencion().equals("3")){
									 detalles.setPorcentajeRetencion(String.valueOf("725"));
								 }
							 }else{
								 detalles.setPorcentajeRetencion(emite.getInfEmisor().getListDetImpuestosRetenciones().get(i).getCodigoRetencion());
							 }
							 //general = this.servicio.buscarNombreCodigo(String.valueOf(((FacDetRetencione)detRetencion.get(i)).getCodImpuesto()), "29");							 
							 detalles.setNombreImpuesto(descripcion);							 
							 detallesAdicional.add(i, detalles);														
						}
				}/*
						detAdicionals = emite.getInfEmisor().getListInfAdicional();
						if (!detAdicionals.isEmpty()) {
						  for (int i = 0; i < detAdicionals.size(); i++) {
						    InformacionAdicional infoAd = new InformacionAdicional();
						    infoAd.setNombre(((FacDetAdicional)detAdicionals.get(i)).getNombre());
						    infoAd.setValor(((FacDetAdicional)detAdicionals.get(i)).getValor());
						    infoAdicional.add(i, infoAd);
						  }
						}*/
         		//JZU RETENCION
			    JRDataSource dataSource = new JRBeanCollectionDataSource(detallesAdicional);
				is = new FileInputStream(urlReporte);
				//JasperPrint reporte_view=JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoCompRetencion()), dataSource);
				JasperPrint reporte_view = JasperFillManager.fillReport(is, obtenerMapaParametrosReportes(obtenerParametrosInfoTributaria(emite), obtenerInfoCompRetencion(emite)), dataSource);
				JasperExportManager.exportReportToPdfFile(reporte_view, numret);
     }
     catch (FileNotFoundException ex) {
    	 ex.printStackTrace();
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex1) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex1);
       }
     }
     catch (JRException e)
     {
    	 
       Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
     finally
     {
       try
       {
         if (is != null)
           is.close();
       }
       catch (IOException ex) {
         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
       }
     }
   }catch (Exception ex) {
	   
	         Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, ex);
}
     return numret;
}
 
   private Map<String, Object> obtenerMapaParametrosReportes(Map<String, Object> mapa1, Map<String, Object> mapa2)
   {
     mapa1.putAll(mapa2);
     mapa1.put(JRParameter.REPORT_LOCALE, regionalConf);
     return mapa1;
   }
 


   private Map<String, Object> obtenerParametrosInfoTributaria(com.sun.businessLogic.validate.Emisor emite) throws SQLException, ClassNotFoundException, FileNotFoundException
   {
     Map<String, Object> param = new HashMap<String, Object>(); 	  
     //FacCabDocumento cabDoc = new FacCabDocumento();
     FacEmpresa empresa = new FacEmpresa();
     try {
       /*cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       			  if (cabDoc != null) {*/
    	 InformacionTributaria infoEstablecimiento = emite.obtieneInfoEstablecimiento(emite.getInfEmisor());
         empresa = this.servicio.buscarEmpresa(emite.getInfEmisor().getRuc());
         param.put("RUC", emite.getInfEmisor().getRuc());
         param.put("CLAVE_ACC", (emite.getInfEmisor().getClaveAcceso().trim().equals("")) || (emite.getInfEmisor().getClaveAcceso() == null) ? "" : emite.getInfEmisor().getClaveAcceso());
         param.put("RAZON_SOCIAL", emite.getInfEmisor().getRazonSocial());
         param.put("NOM_COMERCIAL", emite.getInfEmisor().getRazonSocial());
         param.put("DIR_MATRIZ", emite.getInfEmisor().getDireccionMatriz());
         param.put("SUBREPORT_DIR", "C://resources//reportes//");
         param.put("TIPO_EMISION", emite.getInfEmisor().getTipoEmision().equals("1") ? "NORMAL" : "CONTINGENCIA");
         param.put("NUM_AUT", (emite.getInfEmisor().getNumeroAutorizacion() == null) || (emite.getInfEmisor().getNumeroAutorizacion().equals("")) ? null : emite.getInfEmisor().getNumeroAutorizacion());
         param.put("FECHA_AUT", emite.getInfEmisor().getFechaAutorizacion() == null ? null : emite.getInfEmisor().getFechaAutorizacion());
         param.put("NUM_FACT", emite.getInfEmisor().getCodEstablecimiento() + "-" + emite.getInfEmisor().getCodPuntoEmision() + "-" + emite.getInfEmisor().getSecuencial());
         param.put("AMBIENTE", emite.getInfEmisor().getAmbiente() == 1 ? "PRUEBA" : "PRODUCCION");
         //VPI se comenta para traer el de BD
         //param.put("DIR_SUCURSAL", emite.getInfEmisor().getDireccionEstablecimiento());
         param.put("CONT_ESPECIAL", emite.getInfEmisor().getContribEspecial());
         param.put("LLEVA_CONTABILIDAD", (emite.getInfEmisor().getObligContabilidad().trim().equals("S")||emite.getInfEmisor().getObligContabilidad().trim().equals("SI")) ? "SI" : "NO");
         
         //VPI se agregan campos para reporte
         param.put("TELEFONO_LOCAL",infoEstablecimiento.getTelefonoEstablecimiento());
         param.put("CORREO_RETENCION", infoEstablecimiento.getMailEstablecimiento());
         param.put("DIR_SUCURSAL", infoEstablecimiento.getDireccionEstablecimiento());
         
				  if (emite.getInfEmisor().getListInfAdicional()!= null){
						if (emite.getInfEmisor().getListInfAdicional().size()>0) {
				   		  for (int i = 0; i < emite.getInfEmisor().getListInfAdicional().size(); i++) {
				   		    InformacionAdicional infoAd = new InformacionAdicional();
				   		    if (emite.getInfEmisor().getListInfAdicional().get(i).getName().equals("FECHADOCSUSTENTO"))
				   		    	System.out.println(emite.getInfEmisor().getListInfAdicional().get(i).getName()+"-"+ capitalizeString(emite.getInfEmisor().getListInfAdicional().get(i).getValue().replace(" ", "/")));
				   		    else	
				   		    	System.out.println(emite.getInfEmisor().getListInfAdicional().get(i).getName()+"-"+ capitalizeString(emite.getInfEmisor().getListInfAdicional().get(i).getValue().toString()));
				   		    
				   		    param.put(emite.getInfEmisor().getListInfAdicional().get(i).getName(), capitalizeString(emite.getInfEmisor().getListInfAdicional().get(i).getValue()));				   		   
				   		  }
				   		}
				  }
				  if (empresa!=null){
					  if (empresa.getPathLogoEmpresa()!=null){
						  if (empresa.getPathLogoEmpresa().trim().length()>0){
						  File f = new File(empresa.getPathLogoEmpresa());
				  		  System.out.println("LOGO::"+empresa.getPathLogoEmpresa());
				  //if (((this.codDoc.equals("04")) || (this.codDoc.equals("05")) || (this.codDoc.equals("06")) || (this.codDoc.equals("07"))) && 
				  // (f.exists())) param.put("LOGO", new FileInputStream(empresa.getPathLogoEmpresa()));
				  //if ((this.codDoc.equals("01")) && 
				   //(f.exists())) 
				  		  param.put("LOGO", empresa.getPathLogoEmpresa());
						  }
					  }
     			  }
				  
				  //String file = (emite.getInfEmisor().getAmbientePuntoEmision().equals("1") ? "produccion.jpeg" : "pruebas.jpeg");
				  //String ruta = (empresa.getPathMarcaAgua() == null) || (empresa.getPathMarcaAgua().trim().equals("")) ? "C://resources//images//" : empresa.getPathMarcaAgua();
				  /*System.out.println("MARCA_AGUA::" + ruta + file);
			         String marca = empresa.getMarcaAgua().trim().equals("S") ? ruta + file : "C://resources//images//produccion.jpeg";
			         f = new File(marca);
			         if (f.exists()) param.put("MARCA_AGUA", marca);
				  */
       //}
     }
     catch (Exception e) { e.printStackTrace(); }
 
     return param;
   }

   private Map<String, Object> obtenerParametrosInfoTriobutaria() throws SQLException, ClassNotFoundException, FileNotFoundException
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
     FacEmpresa empresa = new FacEmpresa();
     try {
       cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       if (cabDoc != null) {
         empresa = this.servicio.buscarEmpresa(cabDoc.getRuc());
         param.put("RUC", cabDoc.getRuc());
         param.put("CLAVE_ACC", (cabDoc.getClaveAcceso().trim().equals("")) || (cabDoc.getClaveAcceso() == null) ? "1111111" : cabDoc.getClaveAcceso());
         param.put("RAZON_SOCIAL", empresa.getRazonSocial());
         param.put("NOM_COMERCIAL", empresa.getRazonComercial());
         param.put("DIR_MATRIZ", empresa.getDireccionMatriz());
         param.put("SUBREPORT_DIR", "C://resources//reportes//");
         param.put("TIPO_EMISION", cabDoc.getTipoEmision().trim().equals("1") ? "NORMAL" : "CONTINGENCIA");
         param.put("NUM_AUT", (cabDoc.getNumAutDocSustento() == null) || (cabDoc.getNumAutDocSustento().equals("")) ? null : cabDoc.getNumAutDocSustento());
         param.put("FECHA_AUT", cabDoc.getFechaEmisionDocSustento() == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFechaEmisionDocSustento()));
         param.put("NUM_FACT", cabDoc.getCodEstablecimiento() + "-" + cabDoc.getCodPuntEmision() + "-" + cabDoc.getSecuencial());
         param.put("AMBIENTE", cabDoc.getAmbiente().intValue() == 1 ? "PRUEBA" : "PRODUCCION");
         param.put("DIR_SUCURSAL", cabDoc.getDirEstablecimiento());
         param.put("CONT_ESPECIAL", empresa.getContribEspecial());
         param.put("LLEVA_CONTABILIDAD", cabDoc.getObligadoContabilidad());
         File f = new File(empresa.getPathLogoEmpresa());
         if (((this.codDoc.equals("04")) || (this.codDoc.equals("05")) || (this.codDoc.equals("06")) || (this.codDoc.equals("07"))) && 
           (f.exists())) param.put("LOGO", new FileInputStream(empresa.getPathLogoEmpresa()));
         if ((this.codDoc.equals("01")) && 
           (f.exists())) param.put("LOGO", empresa.getPathLogoEmpresa());
         
         
         
         String file = cabDoc.getAmbiente().intValue() == 1 ? "produccion.jpeg" : "pruebas.jpeg";
         String ruta = (empresa.getPathMarcaAgua() == null) || (empresa.getPathMarcaAgua().trim().equals("")) ? "C://resources//images//" : empresa.getPathMarcaAgua();
         /*System.out.println("MARCA_AGUA::" + ruta + file);
			         String marca = empresa.getMarcaAgua().trim().equals("S") ? ruta + file : "C://resources//images//produccion.jpeg";
			         f = new File(marca);
			         if (f.exists()) param.put("MARCA_AGUA", marca);
				  */
       }
     }
     catch (Exception e) { e.printStackTrace(); }
 
     return param;
   }
 
   private Map<String, Object> obtenerInfoFactura(com.sun.businessLogic.validate.Emisor emite)
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
     try {
       /*cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
	       		  if (cabD oc != null) {*/
    	 		   
					DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
					otherSymbols.setDecimalSeparator('.');
					otherSymbols.setGroupingSeparator(','); 
					DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
         param.put("RS_COMPRADOR", capitalizeString(emite.getInfEmisor().getRazonSocialComp()));
         param.put("RUC_COMPRADOR", emite.getInfEmisor().getIdentificacionComp());
         param.put("FECHA_EMISION", emite.getInfEmisor().getFecEmision());
         param.put("GUIA", emite.getInfEmisor().getGuiaRemision());
         param.put("VALOR_TOTAL", df.format(Double.valueOf(emite.getInfEmisor().getImporteTotal())));
         //param.put("IVA", Double.valueOf(emite.getInfEmisor().getTotalIva12()));
					boolean total12 = false, total0= false, totalice=false, totalNoObjeto=false;
					ArrayList<DetalleTotalImpuestos> lisDetImp = emite.getInfEmisor().getListDetDetImpuestos();
					for ( DetalleTotalImpuestos det : lisDetImp){
						//System.out.println("codTotalImpuestos::"+det.getCodTotalImpuestos());
						//System.out.println("codPorcentImpuestos::"+det.getCodPorcentImp());
						//System.out.println("baseImponible::"+det.getBaseImponibleImp());
						//System.out.println("Valor::"+det.getValorImp());
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 2)){
							total12 = true;							
							cabDoc.setSubtotal12(det.getBaseImponibleImp());
							cabDoc.setIva12(det.getValorImp());
						}
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 6)){
							totalNoObjeto = true;
							cabDoc.setSubtotalNoIva(det.getBaseImponibleImp());
						}
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 0)){
							total0 = true;
							cabDoc.setSubtotal0(det.getBaseImponibleImp());							
						}
					}
					if (total12){
						if (cabDoc.getSubtotal12() >= 0)
							param.put("IVA_12", df.format(cabDoc.getSubtotal12()));
							//System.out.println("IVA_12::"+df.format(cabDoc.getSubtotal12()));
						if (cabDoc.getIva12() >= 0)
							param.put("IVA", df.format(cabDoc.getIva12()));
							//System.out.println("IVA::"+df.format(cabDoc.getIva12()));
					}else{
						param.put("IVA_12", "0.00");
						param.put("IVA", "0.00");
						//System.out.println("IVA_12::0.00");
						//System.out.println("IVA::0.00");
					}
					if (totalNoObjeto){
						param.put("NO_OBJETO_IVA", df.format((Double.valueOf(cabDoc.getSubtotalNoIva()))));
						//System.out.println("NO_OBJETO_IVA::"+df.format((Double.valueOf(cabDoc.getSubtotalNoIva()))));
					}else{
						param.put("NO_OBJETO_IVA", "0.00");
						//System.out.println("NO_OBJETO_IVA::0.00");
					}
					
					if (total0){
						if (cabDoc.getSubtotal0() >= 0)
							param.put("IVA_0", df.format(cabDoc.getSubtotal0()));
							//System.out.println("IVA_0::"+df.format(cabDoc.getSubtotal0()==0?"0.00":cabDoc.getSubtotal0()));
					}else{
						param.put("IVA_0", "0.00");
						//System.out.println("IVA_0::0.00");
					}
					if (totalice){
						param.put("ICE", df.format(Double.valueOf(emite.getInfEmisor().getTotalICE())));
						//System.out.println("ICE::"+df.format(Double.valueOf(emite.getInfEmisor().getTotalICE())));
					}else{
						param.put("ICE", "0.00");
						//System.out.println("ICE::0.00");
					}
				  //System.out.println("obtenerInfoFactura::getSubtotal0::"+df.format(cabDoc.getSubtotal0()));
				  //System.out.println("obtenerInfoFactura::getSubtotal12::"+df.format(cabDoc.getSubtotal12()));
         param.put("SUBTOTAL", df.format(Double.valueOf(emite.getInfEmisor().getTotalSinImpuestos())));
         param.put("PROPINA", df.format(Double.valueOf(emite.getInfEmisor().getPropina())));
         param.put("TOTAL_DESCUENTO", df.format(Double.valueOf(emite.getInfEmisor().getTotalDescuento())));
        //}
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }

   private Map<String, Object> obtenerInfoCredito(com.sun.businessLogic.validate.Emisor emite)
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
     try {
       /*cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
	       		  if (cabDoc != null) {*/
		    	    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
					otherSymbols.setDecimalSeparator('.');
					otherSymbols.setGroupingSeparator(','); 
					DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
			         param.put("RS_COMPRADOR", capitalizeString(emite.getInfEmisor().getRazonSocialComp()));
			         param.put("RUC_COMPRADOR", emite.getInfEmisor().getIdentificacionComp());
			         param.put("FECHA_EMISION", emite.getInfEmisor().getFecEmision());
			         param.put("GUIA", emite.getInfEmisor().getGuiaRemision());
         
				  param.put("DOC_MODIFICADO", Double.valueOf(emite.getInfEmisor().getCodDocModificado()));
				  param.put("FECHA_EMISION_DOC_SUSTENTO", emite.getInfEmisor().getFecEmisionDoc());
				  param.put("NUM_DOC_MODIFICADO", emite.getInfEmisor().getNumDocModificado());
					boolean total12 = false, total0= false, totalice=false, totalNoObjeto=false;
					ArrayList<DetalleTotalImpuestos> lisDetImp = emite.getInfEmisor().getListDetDetImpuestos();
					for ( DetalleTotalImpuestos det : lisDetImp){
						//System.out.println("codTotalImpuestos::"+det.getCodTotalImpuestos());
						//System.out.println("codPorcentImpuestos::"+det.getCodPorcentImp());
						//System.out.println("baseImponible::"+det.getBaseImponibleImp());
						//System.out.println("Valor::"+det.getValorImp());
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 2)){
							total12 = true;							
							cabDoc.setSubtotal12(det.getBaseImponibleImp());
							cabDoc.setIva12(det.getValorImp());
						}
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 6)){
							totalNoObjeto = true;
							cabDoc.setSubtotalNoIva(det.getBaseImponibleImp());
						}
						if ((det.getCodTotalImpuestos() == 2)&&(det.getCodPorcentImp() == 0)){
							total0 = true;
							cabDoc.setSubtotal0(det.getBaseImponibleImp());							
						}
					}
					if (total12){
						if (cabDoc.getSubtotal12() >= 0)
							param.put("IVA_12", df.format(cabDoc.getSubtotal12()));
							//System.out.println("IVA_12::"+df.format(cabDoc.getSubtotal12()));
						if (cabDoc.getIva12() >= 0)
							param.put("IVA", df.format(cabDoc.getIva12()));
							//System.out.println("IVA::"+df.format(cabDoc.getIva12()));
					}else{
						param.put("IVA_12", "0.00");
						param.put("IVA", "0.00");
						//System.out.println("IVA_12::0.00");
						//System.out.println("IVA::0.00");
					}
					
					if (totalNoObjeto){
						param.put("NO_OBJETO_IVA", df.format(Double.valueOf(cabDoc.getSubtotalNoIva())));
						//System.out.println("NO_OBJETO_IVA::"+df.format(Double.valueOf(cabDoc.getSubtotalNoIva())));
					}else{
						param.put("NO_OBJETO_IVA", "0.00");
						//System.out.println("NO_OBJETO_IVA::0.00");
					}
					if (total0){
						if (cabDoc.getSubtotal0() >= 0)
							param.put("IVA_0", df.format(cabDoc.getSubtotal0()));
							//System.out.println("IVA_0::"+df.format(cabDoc.getSubtotal0()));
					}else{
						param.put("IVA_0", "0.00");
						//System.out.println("IVA_0::0.00");
					}
					if (totalice){
						param.put("ICE",df.format(Double.valueOf(emite.getInfEmisor().getTotalICE())));
						//System.out.println("ICE::"+df.format(Double.valueOf(emite.getInfEmisor().getTotalICE())));
					}else{
						param.put("ICE", "0.00");
						//System.out.println("ICE::0.00");
					}
				  double total = cabDoc.getSubtotal12()+cabDoc.getSubtotalNoIva()+cabDoc.getSubtotal0()+emite.getInfEmisor().getTotalICE()+cabDoc.getIva12();
							
				  param.put("VALOR_TOTAL", df.format(total));
					
         param.put("SUBTOTAL", df.format(Double.valueOf(emite.getInfEmisor().getTotalSinImpuestos())));
         param.put("PROPINA", df.format(Double.valueOf(emite.getInfEmisor().getPropina())));
         param.put("TOTAL_DESCUENTO", df.format(Double.valueOf(emite.getInfEmisor().getTotalDescuento())));
         //VPI Se agrega parametro Motivo
         param.put("MOTIVO", emite.getInfEmisor().getMotivo());
         
        //}
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }
   
   

   
   private Map<String, Object> obtenerInfoNC(com.sun.businessLogic.validate.Emisor emite)
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
     try {
       //cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       //if (cabDoc != null) {
		    	 	DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
					otherSymbols.setDecimalSeparator('.');
					otherSymbols.setGroupingSeparator(','); 
					DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
         String comprobante = "";
         if (cabDoc.getCodDocModificado().trim().equals("01")) comprobante = "FACTURA";
         if (cabDoc.getCodDocModificado().trim().equals("04")) comprobante = "NOTA DE CREDITO";
         if (cabDoc.getCodDocModificado().trim().equals("05")) comprobante = "NOTA DE DEBITO";
         if (cabDoc.getCodDocModificado().trim().equals("06")) comprobante = "GUIA DE REMISION";

         param.put("RS_COMPRADOR", capitalizeString(emite.getInfEmisor().getRazonSocialComp()));
         param.put("RUC_COMPRADOR", emite.getInfEmisor().getIdentificacionComp());
         param.put("FECHA_EMISION", emite.getInfEmisor().getFecEmision());
         param.put("VALOR_TOTAL", df.format(Double.valueOf(emite.getInfEmisor().getImporteTotal())));
         param.put("IVA", df.format(Double.valueOf(emite.getInfEmisor().getTotalIva12())));

         param.put("IVA_0", df.format(Double.valueOf(emite.getInfEmisor().getTotalSinImpuestos())));
         param.put("IVA_12", df.format(Double.valueOf(emite.getInfEmisor().getTotalIva12())));
         param.put("ICE", df.format(Double.valueOf(emite.getInfEmisor().getTotalICE())));
         param.put("NO_OBJETO_IVA", df.format(Double.valueOf(emite.getInfEmisor().getTotalSinImpuestos())));
         param.put("SUBTOTAL", df.format(Double.valueOf(emite.getInfEmisor().getTotalSinImpuestos())));
         param.put("PROPINA", df.format(Double.valueOf(emite.getInfEmisor().getPropina())));
         param.put("TOTAL_DESCUENTO", df.format(Double.valueOf(emite.getInfEmisor().getTotalDescuento())));

         param.put("NUM_DOC_MODIFICADO", emite.getInfEmisor().getNumDocModificado());
         param.put("FECHA_EMISION_DOC_SUSTENTO", emite.getInfEmisor().getFecEmisionDoc());
         param.put("DOC_MODIFICADO", emite.getInfEmisor().getCodDocModificado());
         param.put("RAZON_MODIF", emite.getInfEmisor().getMotivo());
       //}
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }
 
   private Map<String, Object> obtenerInfoNC()
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
     try {
       cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       if (cabDoc != null) {
         String comprobante = "";
         if (cabDoc.getCodDocModificado().trim().equals("01")) comprobante = "FACTURA";
         if (cabDoc.getCodDocModificado().trim().equals("04")) comprobante = "NOTA DE CREDITO";
         if (cabDoc.getCodDocModificado().trim().equals("05")) comprobante = "NOTA DE DEBITO";
         if (cabDoc.getCodDocModificado().trim().equals("06")) comprobante = "GUIA DE REMISION";
         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
         param.put("VALOR_TOTAL", df.format(cabDoc.getImporteTotal()));
         param.put("IVA", df.format(cabDoc.getIva12()));
         param.put("IVA_0", df.format(cabDoc.getSubtotal0()));
         param.put("IVA_12", df.format(cabDoc.getSubtotal12()));
         param.put("ICE", df.format(cabDoc.getTotalvalorICE()));
         param.put("NO_OBJETO_IVA", df.format(cabDoc.getSubtotalNoIva()));
         param.put("SUBTOTAL", df.format(cabDoc.getTotalSinImpuesto()));
         param.put("PROPINA", df.format(cabDoc.getPropina()));
         param.put("TOTAL_DESCUENTO", df.format(cabDoc.getTotalDescuento()));
         param.put("NUM_DOC_MODIFICADO", cabDoc.getNumDocModificado());
         param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFecEmisionDocSustento() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFecEmisionDocSustento()));
         param.put("FECHA_EMISION", cabDoc.getFechaEmision() == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFechaEmision()));
         param.put("DOC_MODIFICADO", comprobante);
         param.put("RAZON_MODIF", cabDoc.getMotivoRazon());
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }
 
   private Map<String, Object> obtenerInfoGR()
   {
     Map param = new HashMap();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
     FacCabDocumento cabDoc = new FacCabDocumento();
     List infoAdicional = new ArrayList();
     List detAdicional = new ArrayList();
     try {
       cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       detAdicional = this.servicio.buscarDetAdicional(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       if (cabDoc != null) {
         param.put("RS_TRANSPORTISTA", cabDoc.getRazonSocialComprador());
         param.put("RUC_TRANSPORTISTA", cabDoc.getIdentificacionComprador());
         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
         param.put("GUIA", cabDoc.getGuiaRemision());
         param.put("VALOR_TOTAL", df.format(cabDoc.getImporteTotal()));
         param.put("IVA", df.format(cabDoc.getIva12()));
         param.put("IVA_0", df.format(cabDoc.getSubtotal0()));
         param.put("IVA_12", df.format(cabDoc.getSubtotal12()));
         param.put("ICE", df.format(cabDoc.getTotalvalorICE()));
         param.put("NO_OBJETO_IVA", df.format(cabDoc.getSubtotalNoIva()));
         param.put("SUBTOTAL", df.format(cabDoc.getTotalSinImpuesto()));
         param.put("PROPINA", df.format(cabDoc.getPropina()));
         param.put("TOTAL_DESCUENTO", df.format(cabDoc.getTotalDescuento()));
         param.put("PLACA", cabDoc.getPlaca());
         param.put("PUNTO_PARTIDA", cabDoc.getPartida());
         param.put("FECHA_INI_TRANSPORTE", cabDoc.getFechaInicioTransporte());
         param.put("FECHA_FIN_TRANSPORTE", cabDoc.getFechaFinTransporte());
         if (!detAdicional.isEmpty()) {
           for (int i = 0; i < detAdicional.size(); i++) {
             InformacionAdicional infoAdic = new InformacionAdicional();
             infoAdic.setNombre(((FacDetAdicional)detAdicional.get(i)).getNombre());
             infoAdic.setValor(((FacDetAdicional)detAdicional.get(i)).getValor());
             infoAdicional.add(i, infoAdic);
           }
           param.put("INFO_ADICIONAL", infoAdicional);
         }
       }
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
     return param;
   }
 
   private Map<String, Object> obtenerInfoND()
   {
     Map param = new HashMap();
 
     FacCabDocumento cabDoc = new FacCabDocumento();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
     try {
       cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       if (cabDoc != null) {
         String comprobante = "";
         if (cabDoc.getCodDocModificado().trim().equals("01")) comprobante = "FACTURA";
         if (cabDoc.getCodDocModificado().trim().equals("04")) comprobante = "NOTA DE CREDITO";
         if (cabDoc.getCodDocModificado().trim().equals("05")) comprobante = "NOTA DE DEBITO";
         if (cabDoc.getCodDocModificado().trim().equals("06")) comprobante = "GUIA DE REMISION";
         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
         param.put("GUIA", cabDoc.getGuiaRemision());
         param.put("TOTAL", df.format(cabDoc.getImporteTotal()));
         param.put("IVA", df.format(cabDoc.getIva12()));
         param.put("IVA_0", df.format(cabDoc.getSubtotal0()));
         param.put("IVA_12", df.format(cabDoc.getSubtotal12()));
         param.put("ICE", df.format(cabDoc.getTotalvalorICE()));
         param.put("NO_OBJETO_IVA", df.format(cabDoc.getSubtotalNoIva()));
         param.put("SUBTOTAL", df.format(cabDoc.getTotalSinImpuesto()));
         param.put("PROPINA", df.format(cabDoc.getPropina()));
         param.put("TOTAL_SIN_IMP", df.format(cabDoc.getTotalSinImpuesto()));
         param.put("NUM_DOC_MODIFICADO", cabDoc.getNumDocModificado());
         param.put("DOC_MODIFICADO", comprobante);
         param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFecEmisionDocSustento() == null ? "NO ENVIADO" : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFecEmisionDocSustento()));
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }
 
   private Map<String, Object> obtenerInfoCompRetencion()
   {
     Map param = new HashMap(); 
     FacCabDocumento cabDoc = new FacCabDocumento();
     try
     {
       cabDoc = this.servicio.buscarDatosCabDocumentos(this.Ruc, this.codEst, this.codPuntEm, this.codDoc, this.secuencial);
       if (cabDoc != null) {
         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
         param.put("EJERCICIO_FISCAL", cabDoc.getPeriodoFiscal());
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return param;
   }
   private Map<String, Object> obtenerInfoCompRetencion(com.sun.businessLogic.validate.Emisor emite)
   {
	 Map param = new HashMap(); 	     	     
     param.put("RS_COMPRADOR", emite.getInfEmisor().getRazonSocialComp());
     param.put("RUC_COMPRADOR", emite.getInfEmisor().getIdentificacionComp());
     param.put("FECHA_EMISION", emite.getInfEmisor().getFecEmision());
     param.put("EJERCICIO_FISCAL", emite.getInfEmisor().getPeriodoFiscal());
	 return param;
   }
   
   public void showReport(JasperPrint jp)
   {
     JasperViwerSRI jv = new JasperViwerSRI(jp, Locale.US);
     List newSaveContributors = new LinkedList();
     JRSaveContributor[] saveContributors = jv.getSaveContributors();
     for (int i = 0; i < saveContributors.length; i++) {
       if ((saveContributors[i] instanceof JRPdfSaveContributor)) {
         newSaveContributors.add(saveContributors[i]);
       }
     }
     jv.setSaveContributors((JRSaveContributor[])newSaveContributors.toArray(new JRSaveContributor[0]));
 
     JFrame jf = new JFrame();
     jf.setTitle("Generador de RIDE");
     jf.getContentPane().add(jv);
     jf.validate();
     jf.setVisible(true);
     jf.setSize(new Dimension(800, 650));
     jf.setLocation(300, 100);
     jf.setDefaultCloseOperation(1);
   }
 
   public String getRuc()
   {
     return this.Ruc;
   }
   public void setRuc(String ruc) {
     this.Ruc = ruc;
   }
   public String getCodEst() {
     return this.codEst;
   }
   public void setCodEst(String codEst) {
     this.codEst = codEst;
   }
   public String getCodPuntEm() {
     return this.codPuntEm;
   }
   public void setCodPuntEm(String codPuntEm) {
     this.codPuntEm = codPuntEm;
   }
   public String getCodDoc() {
     return this.codDoc;
   }
   public void setCodDoc(String codDoc) {
     this.codDoc = codDoc;
   }
   public String getSecuencial() {
     return this.secuencial;
   }
   public void setSecuencial(String secuencial) {
     this.secuencial = secuencial;
   }

public static String capitalizeString(String string) {
	  char[] chars = string.toLowerCase().toCharArray();
	  boolean found = false;
	  for (int i = 0; i < chars.length; i++) {
	    if (!found && Character.isLetter(chars[i])) {
	      chars[i] = Character.toUpperCase(chars[i]);
	      found = true;
	    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
	      found = false;
	    }
	  }
	  return String.valueOf(chars);
	}
}