package com.sun.directory.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.w3c.css.sac.InputSource;

public class EncodingXml {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
			File file = new File("1099253194000106001001010000018.xml");
		  InputStream inputStream= new FileInputStream(file);
		  Reader reader = new InputStreamReader(inputStream,"UTF-8");
		  InputSource is = new InputSource(reader);
		  is.setEncoding("UTF-8");
		  System.out.println("Fin");
	}
}
