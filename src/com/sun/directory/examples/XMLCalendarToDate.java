package com.sun.directory.examples;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;


public class XMLCalendarToDate {
	public static void main(String args[]) {
	       Date today = new Date();
	     
	       //Converting date to XMLGregorianCalendar in Java
	       XMLGregorianCalendar xml = toXMLGregorianCalendar(today);
	       System.out.println("XMLGregorianCalendar from Date in Java      : " + xml) ;
	     
	       //Converting XMLGregorianCalendar to java.util.Date in Java
	       Date date = toDate(xml);
	       System.out.println("java.util.Date from XMLGregorianCalendar in Java : " + date);
	       
	       
	       XMLGregorianCalendar xml2 = XMLGregorianCalendarImpl.parse("2014-08-08T13:42:26.918-05:00");
	       Date date2 = toDate(xml2);
	       System.out.println(xml2.toString());
	       System.out.println("Date2 : " + date2);
	    }
	  
	    /*
	     * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar
	     */
	    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date){
	        GregorianCalendar gCalendar = new GregorianCalendar();
	        gCalendar.setTime(date);
	        XMLGregorianCalendar xmlCalendar = null;
	        try {
	            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
	        } catch (DatatypeConfigurationException ex) {
	        	ex.printStackTrace();
	            //Logger.getLogger(StringReplace.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return xmlCalendar;
	    }
	  
	    /*
	     * Converts XMLGregorianCalendar to java.util.Date in Java
	     */
	    public static Date toDate(XMLGregorianCalendar calendar){
	        if(calendar == null) {
	            return null;
	        }
	        return calendar.toGregorianCalendar().getTime();
	    }

}
